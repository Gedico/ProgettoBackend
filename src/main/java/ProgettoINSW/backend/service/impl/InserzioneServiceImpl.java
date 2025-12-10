package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneFiltriRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.geopify.GeoapifyFeature;
import ProgettoINSW.backend.dto.geopify.GeoapifyResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneCardResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.mapper.InserzioneMap;
import ProgettoINSW.backend.model.*;
import ProgettoINSW.backend.model.enums.Categoria;
import ProgettoINSW.backend.model.enums.Role;
import ProgettoINSW.backend.model.enums.StatoInserzione;
import ProgettoINSW.backend.model.enums.TipoIndicatore;
import ProgettoINSW.backend.repository.*;
import ProgettoINSW.backend.repository.IndicatoreRepository;
import ProgettoINSW.backend.service.GeoapifyClient;
import ProgettoINSW.backend.service.InserzioneService;
import ProgettoINSW.backend.specification.InserzioneSpecification;
import ProgettoINSW.backend.util.JwtUtil;
import ProgettoINSW.backend.util.ValidazioneUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InserzioneServiceImpl implements InserzioneService {

    private static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final int MAX_IMAGE_COUNT = 20;
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final InserzioneMap map;
    private final InserzioneRepository inserzioneRepository;
    private final PosizioneRepository posizioneRepository;
    private final FotoRepository fotoRepository;
    private final AgenteRepository agenteRepository;
    private final AccountRepository accountRepository;
    private final ValidazioneUtil validazioneUtil;
    private final CloudStorageServiceImpl cloudStorageService;
    private final GeoapifyClient geoapifyClient;
    private final IndicatoreRepository indicatoreRepository;


    /*********CREA INSERZIONE*************************************************************************************************************/

    @Transactional
    @Override
    public InserzioneResponse creaInserzione(InserzioneRequest request,
                                             MultipartFile[] immagini,
                                             String token) throws IOException {

        // 0. Fix fondamentale: se immagini è null → array vuoto
        if (immagini == null) {
            immagini = new MultipartFile[0];
        }

        // 1. Recupero agente da JWT
        String mail = JwtUtil.extractMail(token);

        Account account = accountRepository.findByMail(mail)
                .orElseThrow(() -> new EntityNotFoundException("Account non trovato per l'email: " + mail));

        Agente agente = agenteRepository.findByAccount(account)
                .orElseThrow(() -> new EntityNotFoundException("Agente non trovato per l'account: " + mail));

        // 2. Salvataggio posizione
        Posizione posizione = map.toPosizione(request.getPosizione());
        posizioneRepository.save(posizione);

        // 3. Creazione inserzione
        Inserzione inserzione = map.toDatiInserzione(request.getDatiInserzioneRequest());
        inserzione.setPosizione(posizione);
        inserzione.setAgente(agente);
        inserzioneRepository.save(inserzione);

        // 4. Gestione immagini (opzionali)
        List<Foto> fotoSalvate = new ArrayList<>();
        List<String> urlCaricati = new ArrayList<>();

        try {

            // Validazione numero massimo immagini
            if (immagini.length > MAX_IMAGE_COUNT) {
                throw new IllegalArgumentException(
                        "Numero massimo di immagini superato. Consentite al massimo " + MAX_IMAGE_COUNT + " immagini.");
            }

            // Upload effettivo SOLO se ci sono immagini
            for (MultipartFile file : immagini) {

                // Validazione (tipo MIME, dimensione massima, ecc.)
                validaImmagine(file);

                // Upload sul cloud
                String url = cloudStorageService.uploadFile(file);
                urlCaricati.add(url);

                // Creo entità Foto
                Foto foto = new Foto();
                foto.setUrlFoto(url);
                foto.setInserzione(inserzione);
                fotoSalvate.add(foto);
            }

            // Salvo tutte le foto in DB
            fotoRepository.saveAll(fotoSalvate);

        } catch (Exception e) {

            // ROLLBACK MANUALE DELLE IMMAGINI CARICATE
            for (String url : urlCaricati) {
                try {
                    cloudStorageService.deleteFile(url);
                } catch (Exception ex) {
                    // Qui sarebbe consigliato fare logger.warn(...)
                }
            }

            // Lasciamo che la @Transactional effettui rollback del DB
            throw e;
        }

        // 5. Creazione indicatori Geoapify
        try {
            BigDecimal lat = posizione.getLatitudine();
            BigDecimal lon = posizione.getLongitudine();

            // 5.1 Chiamata all'API geopify
            GeoapifyResponse geoRes = geoapifyClient.cercaLuoghi(lat, lon);

            // 5.2 Conversione in lista di indicatori
            List<IndicatoreProx> indicatori = generaIndicatoriInserzione(geoRes, inserzione);

            // 5.3 Salvataggio indicatori
            indicatoreRepository.saveAll(indicatori);

        } catch (Exception e) {
            // Questo non deve bloccare la creazione dell'inserzione
            // ma è utile loggare
            // logger.error("Errore nel calcolo indicatori: ", e);
        }

        // 6. Restituisci la response completa
        return map.toInserzioneResponse(inserzione);
    }





    /******MODIFICA INSERZIONE*************************************************************************************************************************/

    @Transactional
    @Override
    public DatiInserzioneRequest modificaInserzione(Long id, DatiInserzioneRequest request, String token) {

        if (validazioneUtil.verificaAgenteInserzione(id, token)) {
            throw new RuntimeException("Non puoi modificare un'inserzione che non hai pubblicato");
        }

        Inserzione inserzione = inserzioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inserzione non trovata"));

        if (request.getTitolo() != null) inserzione.setTitolo(request.getTitolo());
        if (request.getDescrizione() != null) inserzione.setDescrizione(request.getDescrizione());
        if (request.getPrezzo() != null) inserzione.setPrezzo(request.getPrezzo());
        if (request.getDimensioni() != null) inserzione.setDimensioni(request.getDimensioni());
        if (request.getNumero_stanze() != null) inserzione.setNumeroStanze(request.getNumero_stanze());
        if (request.getPiano() != null) inserzione.setPiano(request.getPiano());
        if (request.getAscensore() != null) inserzione.setAscensore(request.getAscensore());
        if (request.getClasse_energetica() != null) inserzione.setClasseEnergetica(request.getClasse_energetica());
        if (request.getCategoria() != null)
            inserzione.setCategoria(Categoria.valueOf(request.getCategoria().toUpperCase()));

        inserzioneRepository.save(inserzione);

        return new DatiInserzioneRequest("Inserzione aggiornata con successo", true);
    }


/*****GET INSERZIONI**************************************************************************************************************************/


    @Override
    public List<InserzioneResponse> ricercaInserzioni(DatiInserzioneFiltriRequest filtri) {

        var specification = InserzioneSpecification.filtra(filtri);

        List<Inserzione> risultati = inserzioneRepository.findAll(specification);

        return risultati.stream()
                .map(map::toInserzioneResponse)
                .toList();
    }


    @Override
    public InserzioneResponse getInserzioneById(Long id) {
        Inserzione inserzione = inserzioneRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Inserzione con ID " + id + " non trovato."));
        return map.toInserzioneResponse(inserzione);
    }

    @Override
    public List<InserzioneCardResponse> getInserzioniRecenti() {

        Pageable limit = PageRequest.of(0, 4);

        List<Inserzione> lista = inserzioneRepository.findUltime4ConFoto(limit);

        return lista.stream()
                .map(inserzione -> {
                    InserzioneCardResponse dto = new InserzioneCardResponse();

                    dto.setIdInserzione(inserzione.getIdInserzione());
                    dto.setTitolo(inserzione.getTitolo());
                    dto.setPrezzo(inserzione.getPrezzo());
                    dto.setDimensioni(inserzione.getDimensioni());
                    dto.setNumero_stanze(inserzione.getNumeroStanze());

                    // Prima foto
                    if (inserzione.getFoto() != null && !inserzione.getFoto().isEmpty()) {
                        dto.setFotoPrincipale(inserzione.getFoto().get(0).getUrlFoto());
                    }

                    return dto;
                })
                .toList();


    }


    @Override
    public List<InserzioneResponse> getAllInserzioni() {
        List<Inserzione> inserzioni = inserzioneRepository.findAllConRelazioni();
        return inserzioni.stream()
                .map(map::toInserzioneResponse)
                .toList();
    }

    @Override
    public List<InserzioneCardResponse> getInserzioniPerAgente(String token) {

        // 1. Estrazione email dal JWT
        String mail = JwtUtil.extractMail(token);

        // 2. Recupero account
        Account account = accountRepository.findByMailIgnoreCase(mail)
                .orElseThrow(() -> new RuntimeException("Account non trovato per " + mail));

        // 3. Recupero agente collegato
        Agente agente = agenteRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Agente non trovato per l'account " + mail));

        Long idAgente = agente.getIdAgente();

        // 4. Recupero inserzioni dell’agente
        List<Inserzione> lista = inserzioneRepository.findByAgente_IdAgente(idAgente);

        // 5. Mappatura a card DTO
        return lista.stream()
                .map(map::toCardResponse)
                .toList();
    }





    /*****ELIMINA INSERZIONI**********************************************************************************************************************/


    @Override
    public void eliminaInserzione(Long id, String token) {
        String mailAgente = JwtUtil.extractMail(token);

        Inserzione inserzione = inserzioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inserzione non trovato"));

        Account accountRichiedente = accountRepository.findByMailIgnoreCase(mailAgente)
                .orElseThrow(() -> new RuntimeException("Account non trovato"));

        boolean isAdmin = accountRichiedente.getRuolo().equals(Role.ADMIN);
        boolean isProprietario = inserzione.getAgente().getAccount().getMail().equalsIgnoreCase(mailAgente);

        if (!isAdmin && !isProprietario) {
            throw new RuntimeException("Non puoi eliminare un'inserzione che non hai pubblicato");
        }

        inserzioneRepository.delete(inserzione);
    }



/*******FUNZIONI AUSILIARi*****************************************************************************************************************/



    @Override
    public void cambiaStato(Long id, String token, String nuovoStato) {

        if (validazioneUtil.verificaAgenteInserzione(id, token)) {
            throw new RuntimeException("Non puoi modificare lo stato di un'inserzione che non hai pubblicato");
        }

        Inserzione inserzione = inserzioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inserzione non trovata"));

        try {
            StatoInserzione statoEnum = StatoInserzione.valueOf(nuovoStato.toUpperCase());
            inserzione.setStato(statoEnum);
            inserzioneRepository.save(inserzione);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Stato non valido: deve essere DISPONIBILE o VENDUTO");
        }
    }


    private void validaImmagine(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uno dei file immagine è vuoto o non valido.");
        }

        if (file.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException("Una delle immagini supera la dimensione massima consentita di 5 MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Formato immagine non supportato. Sono ammessi solo JPG, PNG, WEBP.");
        }
    }


    private TipoIndicatore mappaCategoria(List<String> categories) {

        if (categories == null) return null;

        for (String cat : categories) {
            if (cat.contains("education.school")) return TipoIndicatore.SCUOLA;
            if (cat.contains("healthcare.hospital")) return TipoIndicatore.OSPEDALE;
            if (cat.contains("commercial.supermarket")) return TipoIndicatore.SUPERMERCATO;
            if (cat.contains("leisure.park")) return TipoIndicatore.PARCO;
            if (cat.contains("public_transport")) return TipoIndicatore.MEZZI_PUBBLICI;
            if (cat.contains("catering.restaurant")) return TipoIndicatore.RISTORANTE;
        }

        return null;
    }



    private List<IndicatoreProx> generaIndicatoriInserzione(
            GeoapifyResponse response,
            Inserzione inserzione
    ) {
        List<IndicatoreProx> indicatori = new ArrayList<>();

        if (response == null || response.getFeatures() == null) {
            return indicatori;
        }

        for (GeoapifyFeature feature : response.getFeatures()) {
            if (feature.getProperties() == null) continue;

            TipoIndicatore tipo = mappaCategoria(feature.getProperties().getCategories());
            if (tipo == null) continue;

            IndicatoreProx ind = new IndicatoreProx();
            ind.setTipo(tipo);
            ind.setDistanza(feature.getProperties().getDistance());
            ind.setInserzione(inserzione);

            indicatori.add(ind);
        }

        return indicatori;
    }


}
