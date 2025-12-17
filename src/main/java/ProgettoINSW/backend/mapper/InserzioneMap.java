package ProgettoINSW.backend.mapper;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneResponse;
import ProgettoINSW.backend.dto.foto.FotoResponse;
import ProgettoINSW.backend.dto.indicatori.IndicatoreResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneCardResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.dto.posizione.PosizioneResponse;
import ProgettoINSW.backend.model.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
public class InserzioneMap {


/*****************CREA ENTITY COMPLETA (REQUEST → ENTITY)****************************************************************/


    public Inserzione inserzioneToEntity(InserzioneRequest request, Posizione posizione, Agente agente, IndicatoreProssimita indicatoreProssimita) {
        if (request == null) return null;

        DatiInserzioneRequest dto = request.getDatiInserzioneRequest();
        if (dto == null) return null;

        Inserzione inserzione = toDatiInserzione(dto);
        inserzione.setPosizione(posizione);
        inserzione.setAgente(agente);
        inserzione.setIndicatore(indicatoreProssimita);

        return inserzione;
    }



/******************DATI INSERZIONE: DTO → ENTITY**********************************************************************/

    public Inserzione toDatiInserzione(DatiInserzioneRequest dto) {
        if (dto == null) return null;

        Inserzione inserzione = new Inserzione();
        inserzione.setTitolo(dto.getTitolo());
        inserzione.setDescrizione(dto.getDescrizione());
        inserzione.setPrezzo(dto.getPrezzo());
        inserzione.setDimensioni(dto.getDimensioni());
        inserzione.setNumeroStanze(dto.getNumero_stanze());
        inserzione.setPiano(dto.getPiano());
        inserzione.setAscensore(dto.getAscensore());
        inserzione.setClasseEnergetica(dto.getClasse_energetica());

        if (dto.getCategoria() != null) {
            inserzione.setCategoria(dto.getCategoria());
        }

        return inserzione;
    }




/***********ENTITY → RESPONSE COMPLETA**********************************************************************************/


        public InserzioneResponse toInserzioneResponse(Inserzione inserzione) {
            if (inserzione == null) return null;

            InserzioneResponse response = new InserzioneResponse();
            response.setId(inserzione.getIdInserzione());
            response.setDati(mapDati(inserzione));
            response.setPosizione(mapPosizione(inserzione));
            response.setIndicatore(mapIndicatore(inserzione.getIndicatore()));
            response.setFoto(mapFoto(inserzione));
            response.setMessaggio("Inserzione caricata con successo");

            return response;
        }


        private DatiInserzioneResponse mapDati(Inserzione inserzione) {
            if (inserzione == null) return null;

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


        private PosizioneResponse mapPosizione(Inserzione inserzione) {
            if (inserzione == null || inserzione.getPosizione() == null) return null;

            PosizioneResponse pos = new PosizioneResponse();
            pos.setLatitudine(inserzione.getPosizione().getLatitudine());
            pos.setLongitudine(inserzione.getPosizione().getLongitudine());
            pos.setComune(inserzione.getPosizione().getComune());
            pos.setIndirizzo(inserzione.getPosizione().getIndirizzo());

            return pos;
        }


        private List<FotoResponse> mapFoto(Inserzione inserzione) {
            if (inserzione == null || inserzione.getFoto() == null) return Collections.emptyList();

            return inserzione.getFoto().stream()
                    .map(f -> {
                        FotoResponse fr = new FotoResponse();
                        fr.setUrl(f.getUrlFoto());
                        return fr;
                    })
                    .toList();
        }


        private IndicatoreResponse mapIndicatore(IndicatoreProssimita indicatore) {

            if (indicatore == null) {
                return null;
            }

            IndicatoreResponse dto = new IndicatoreResponse();
            dto.setScuoleVicine(indicatore.isVicinoScuola());
            dto.setSupermercatiVicini(indicatore.isVicinoParco());
            dto.setMezziPubbliciVicini(indicatore.isVicinoMezziPubblici());
            return dto;
        }



/*******CARD RESPONSE (LISTA)********************************************************************************************/



    public InserzioneCardResponse toCardResponse(Inserzione inserzione) {
        if (inserzione == null) return null;

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


