package ProgettoINSW.backend.mapper;

import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.dto.posizione.PosizioneRequest;
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
        response.setInserzione(inserzione);
        response.setMessaggio("Inserzione creata con successo");
        return response;
    }

}
