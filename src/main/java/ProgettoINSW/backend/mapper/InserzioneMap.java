package ProgettoINSW.backend.mapper;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneResponse;
import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.foto.FotoResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneCardResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.dto.posizione.PosizioneRequest;
import ProgettoINSW.backend.dto.posizione.PosizioneResponse;
import ProgettoINSW.backend.model.Foto;
import ProgettoINSW.backend.model.Inserzione;
import ProgettoINSW.backend.model.Posizione;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class InserzioneMap {

    // -------------------------------------------------------------
    // POSIZIONE: DTO → ENTITY
    // -------------------------------------------------------------
    public Posizione toPosizione(final PosizioneRequest dto) {
        if (dto == null) {
            return null;
        }

        Posizione posizione = new Posizione();
        posizione.setLatitudine(dto.getLatitudine());
        posizione.setLongitudine(dto.getLongitudine());
        posizione.setDescrizione(dto.getDescrizione_posizione());
        return posizione;
    }

    // -------------------------------------------------------------
    // INSERZIONE BASE: DTO → ENTITY
    // -------------------------------------------------------------
    public Inserzione toDatiInserzione(final DatiInserzioneRequest dto) {
        if (dto == null) {
            return null;
        }

        Inserzione inserzione = new Inserzione();
        inserzione.setTitolo(dto.getTitolo());
        inserzione.setDescrizione(dto.getDescrizione());
        inserzione.setPrezzo(dto.getPrezzo());
        inserzione.setDimensioni(dto.getDimensioni());
        inserzione.setNumeroStanze(dto.getNumero_stanze());
        inserzione.setPiano(dto.getPiano());
        inserzione.setAscensore(dto.getAscensore());
        inserzione.setClasseEnergetica(dto.getClasse_energetica());
        inserzione.setCategoria(dto.getCategoria());
        return inserzione;
    }

    // -------------------------------------------------------------
    // FOTO: DTO → LISTA ENTITY
    // -------------------------------------------------------------
    public List<Foto> toFotoList(final List<FotoRequest> lista, final Inserzione inserzione) {

        if (lista == null || lista.isEmpty() || inserzione == null) {
            return Collections.emptyList();
        }

        return lista.stream()
                .filter(Objects::nonNull)
                .map(dto -> {
                    Foto foto = new Foto();
                    foto.setUrlFoto(dto.getUrl());
                    foto.setInserzione(inserzione);
                    return foto;
                })
                .toList();
    }

    // -------------------------------------------------------------
    // ENTITY → DTO (DETTAGLIO COMPLETO)
    // -------------------------------------------------------------
    public InserzioneResponse toInserzioneResponse(final Inserzione inserzione) {
        if (inserzione == null) {
            return null;
        }

        InserzioneResponse response = new InserzioneResponse();
        response.setId(inserzione.getIdInserzione());
        response.setDati(mapDati(inserzione));
        response.setPosizione(mapPosizione(inserzione));
        response.setFoto(mapFoto(inserzione));
        response.setMessaggio("Inserzione caricata con successo");

        return response;
    }

    // -------------------------------------------------------------
    // METODO PRIVATO: MAP DATI INSERZIONE
    // -------------------------------------------------------------
    private DatiInserzioneResponse mapDati(final Inserzione inserzione) {
        if (inserzione == null) {
            return null;
        }

        DatiInserzioneResponse dati = new DatiInserzioneResponse();
        dati.setTitolo(inserzione.getTitolo());
        dati.setDescrizione(inserzione.getDescrizione());
        dati.setPrezzo(inserzione.getPrezzo());
        dati.setDimensioni(inserzione.getDimensioni());
        dati.setNumeroStanze(inserzione.getNumeroStanze());
        dati.setPiano(inserzione.getPiano());
        dati.setAscensore(inserzione.getAscensore());
        dati.setClasseEnergetica(inserzione.getClasseEnergetica());

        if (inserzione.getCategoria() != null) {
            dati.setCategoria(inserzione.getCategoria().name());
        }

        return dati;
    }

    // -------------------------------------------------------------
    // METODO PRIVATO: MAP POSIZIONE
    // -------------------------------------------------------------
    private PosizioneResponse mapPosizione(final Inserzione inserzione) {
        if (inserzione == null || inserzione.getPosizione() == null) {
            return null;
        }

        PosizioneResponse pos = new PosizioneResponse();
        pos.setLatitudine(inserzione.getPosizione().getLatitudine());
        pos.setLongitudine(inserzione.getPosizione().getLongitudine());
        pos.setDescrizionePosizione(inserzione.getPosizione().getDescrizione());
        return pos;
    }

    // -------------------------------------------------------------
    // METODO PRIVATO: MAP FOTO
    // -------------------------------------------------------------
    private List<FotoResponse> mapFoto(final Inserzione inserzione) {
        if (inserzione == null || inserzione.getFoto() == null) {
            return Collections.emptyList();
        }

        return inserzione.getFoto().stream()
                .filter(Objects::nonNull)
                .map(f -> {
                    FotoResponse fr = new FotoResponse();
                    fr.setUrl(f.getUrlFoto());
                    return fr;
                })
                .toList();
    }

    // -------------------------------------------------------------
    // ENTITY → DTO CARD (LISTA BREVE)
    // -------------------------------------------------------------
    public InserzioneCardResponse toCardResponse(final Inserzione inserzione) {
        if (inserzione == null) {
            return null;
        }

        InserzioneCardResponse card = new InserzioneCardResponse();
        card.setIdInserzione(inserzione.getIdInserzione());
        card.setTitolo(inserzione.getTitolo());
        card.setPrezzo(inserzione.getPrezzo());
        card.setDimensioni(inserzione.getDimensioni());
        card.setNumero_stanze(inserzione.getNumeroStanze());

        String fotoPrincipale =
                (inserzione.getFoto() != null && !inserzione.getFoto().isEmpty())
                        ? inserzione.getFoto().get(0).getUrlFoto()
                        : null;

        card.setFotoPrincipale(fotoPrincipale);

        return card;
    }
}
