package ProgettoINSW.backend.mapper;

import ProgettoINSW.backend.dto.foto.FotoRequest;
import ProgettoINSW.backend.dto.immobile.ImmobileRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.dto.posizione.PosizioneRequest;
import ProgettoINSW.backend.model.FotoImmobili;
import ProgettoINSW.backend.model.Immobile;
import ProgettoINSW.backend.model.Posizione;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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



    // 🔹 DTO → Entity : Immobile
    public Immobile toImmobile(ImmobileRequest dto) {
        if (dto == null) return null;

        Immobile immobile = new Immobile();
        immobile.setTitolo(dto.getTitolo());
        immobile.setDescrizione(dto.getDescrizione());
        immobile.setPrezzo(dto.getPrezzo());
        immobile.setDimensioni(dto.getDimensioni());
        immobile.setNumeroStanze(dto.getNumero_stanze());
        immobile.setPiano(dto.getPiano());
        immobile.setAscensore(dto.getAscensore());
        immobile.setClasseEnergetica(dto.getClasse_energetica());
        immobile.setCategoria(dto.getCategoria());
        return immobile;
    }


    public List<FotoImmobili> toFotoList(List<FotoRequest> fotoRequestList, Immobile immobile) {
        if (fotoRequestList == null || fotoRequestList.isEmpty()) {
            return List.of();
        }

        return fotoRequestList.stream()
                .map(fotoDTO -> {
                    FotoImmobili foto = new FotoImmobili();
                    foto.setUrlFoto(fotoDTO.getUrl());
                    foto.setImmobile(immobile); // 👈 associazione diretta
                    return foto;
                })
                .toList();
    }

    // 🔹 Composizione finale: Entity → DTO: Inserzione completa
    public InserzioneResponse toInserzioneResponse(Immobile immobile) {
        InserzioneResponse response = new InserzioneResponse();
        response.setImmobile(immobile);
        response.setMessaggio("Inserzione creata con successo");
        return response;
    }

}
