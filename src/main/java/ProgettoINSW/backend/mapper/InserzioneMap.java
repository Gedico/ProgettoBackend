package ProgettoINSW.backend.mapper;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneResponse;
import ProgettoINSW.backend.dto.foto.FotoResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.dto.posizione.PosizioneResponse;
import ProgettoINSW.backend.model.Agente;
import ProgettoINSW.backend.model.Foto;
import ProgettoINSW.backend.model.Inserzione;
import ProgettoINSW.backend.model.Posizione;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InserzioneMap {

    // ============================
    // 1️⃣ CREA ENTITY COMPLETA
    // ============================
    public Inserzione toEntity(InserzioneRequest request, Posizione posizione, Agente agente) {
        if (request == null) return null;

        DatiInserzioneRequest dto = request.getDatiInserzioneRequest();
        if (dto == null) return null;

        Inserzione inserzione = new Inserzione();

        // --- Dati inserzione ---
        inserzione.setTitolo(dto.getTitolo());
        inserzione.setDescrizione(dto.getDescrizione());
        inserzione.setPrezzo(dto.getPrezzo());
        inserzione.setDimensioni(dto.getDimensioni());
        inserzione.setNumeroStanze(dto.getNumero_stanze());
        inserzione.setPiano(dto.getPiano());
        inserzione.setAscensore(dto.getAscensore());
        inserzione.setClasseEnergetica(dto.getClasse_energetica());
        inserzione.setCategoria(dto.getCategoria());

        // --- Relazioni ---
        inserzione.setPosizione(posizione);
        inserzione.setAgente(agente);

        return inserzione;
    }


    // ============================
    // 2️⃣ RESPONSE COMPLETA
    // ============================
    public InserzioneResponse toInserzioneResponse(Inserzione inserzione) {

        InserzioneResponse response = new InserzioneResponse();
        response.setId(inserzione.getIdInserzione());

        // --- DATI ---
        DatiInserzioneResponse dati = new DatiInserzioneResponse();
        dati.setTitolo(inserzione.getTitolo());
        dati.setDescrizione(inserzione.getDescrizione());
        dati.setPrezzo(inserzione.getPrezzo());
        dati.setDimensioni(inserzione.getDimensioni());
        dati.setNumeroStanze(inserzione.getNumeroStanze());
        dati.setPiano(inserzione.getPiano());
        dati.setAscensore(inserzione.getAscensore());
        dati.setClasseEnergetica(inserzione.getClasseEnergetica());
        dati.setCategoria(inserzione.getCategoria().name());

        response.setDati(dati);

        // --- POSIZIONE ---
        if (inserzione.getPosizione() != null) {
            PosizioneResponse pos = new PosizioneResponse();
            pos.setLatitudine(inserzione.getPosizione().getLatitudine());
            pos.setLongitudine(inserzione.getPosizione().getLongitudine());
            pos.setDescrizionePosizione(inserzione.getPosizione().getDescrizione());
            response.setPosizione(pos);
        }

        // --- FOTO ---
        if (inserzione.getFoto() != null) {
            List<FotoResponse> fotoList = inserzione.getFoto()
                    .stream()
                    .map(f -> {
                        FotoResponse fr = new FotoResponse();
                        fr.setUrl(f.getUrlFoto());
                        return fr;
                    })
                    .toList();

            response.setFoto(fotoList);
        }

        response.setMessaggio("Inserzione caricata con successo");
        return response;
    }
}

