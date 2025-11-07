package ProgettoINSW.backend.mapper;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneResponse;
import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.foto.FotoResponse;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.dto.posizione.PosizioneRequest;
import ProgettoINSW.backend.dto.posizione.PosizioneResponse;
import ProgettoINSW.backend.model.Foto;
import ProgettoINSW.backend.model.Inserzione;
import ProgettoINSW.backend.model.Posizione;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InserzioneMap {

    public Posizione toPosizione(PosizioneRequest dto) {
        if (dto == null) return null;

        Posizione posizione = new Posizione();
        posizione.setLatitudine(dto.getLatitudine());
        posizione.setLongitudine(dto.getLongitudine());
        posizione.setDescrizione(dto.getDescrizione_posizione());
        return posizione;
    }

    // 🔹 DTO → Entity : DatiInserzione
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
        inserzione.setCategoria(dto.getCategoria());
        return inserzione;
    }

    public List<Foto> toFotoList(List<FotoRequest> fotoRequestList, Inserzione inserzione) {
        if (fotoRequestList == null || fotoRequestList.isEmpty()) {
            return List.of();
        }

        return fotoRequestList.stream()
                .map(fotoDTO -> {
                    Foto foto = new Foto();
                    foto.setUrlFoto(fotoDTO.getUrl());
                    foto.setInserzione(inserzione); // 👈 associazione diretta
                    return foto;
                })
                .toList();
    }

    // 🔹 Composizione finale: Entity → DTO: Inserzione completa
    public InserzioneResponse toInserzioneResponse(Inserzione inserzione) {
        InserzioneResponse response = new InserzioneResponse();
        response.setId(inserzione.getIdInserzione());

        // --- dati ---
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

        // --- posizione ---
        if (inserzione.getPosizione() != null) {
            PosizioneResponse pos = new PosizioneResponse();
            pos.setLatitudine(inserzione.getPosizione().getLatitudine());
            pos.setLongitudine(inserzione.getPosizione().getLongitudine());
            pos.setDescrizionePosizione(inserzione.getPosizione().getDescrizione());
            response.setPosizione(pos);
        }

        // --- foto ---
        if (inserzione.getFoto() != null) {
            List<FotoResponse> fotoList = inserzione.getFoto().stream()
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
