package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.inserzione.InserzioneRequest;
import ProgettoINSW.backend.dto.inserzione.InserzioneResponse;
import ProgettoINSW.backend.dto.posizione.PosizioneRequest;
import ProgettoINSW.backend.mapper.InserzioneMap;
import ProgettoINSW.backend.model.*;
import ProgettoINSW.backend.repository.InserzioneRepository;
import ProgettoINSW.backend.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InserzioneServiceImplTest {

    // ---------- MOCK DELLE DIPENDENZE ----------
    @Mock
    private InserzioneMap map;

    @Mock
    private InserzioneRepository inserzioneRepository;

    @Mock
    private IndicatoreProssimitaService indicatoreProssimitaService;

    @Mock
    private FotoService fotoService;

    @Mock
    private AgenteService agenteService;

    @Mock
    private PosizioneService posizioneService;

    // ---------- SERVICE DA TESTARE ----------
    @InjectMocks
    private InserzioneServiceImpl inserzioneService;

    // ---------- TEST HAPPY PATH ----------
    @Test
    void creaInserzione_quandoOk_salvaInserzioneEImmagini() throws Exception {

        // GIVEN
        String token = "valid-token";

        InserzioneRequest request = mock(InserzioneRequest.class);
        MultipartFile[] immagini = new MultipartFile[]{mock(MultipartFile.class)};

        Agente agente = new Agente();
        Posizione posizione = new Posizione();
        IndicatoreProssimita indicatore = new IndicatoreProssimita();
        Inserzione inserzione = new Inserzione();
        InserzioneResponse response = new InserzioneResponse();

        when(agenteService.getAgenteFromToken(token)).thenReturn(agente);
        when(posizioneService.creaPosizione(any())).thenReturn(posizione);
        when(indicatoreProssimitaService.generaIndicatoriPerInserzione(request))
                .thenReturn(indicatore);
        when(map.inserzioneToEntity(request, posizione, agente, indicatore))
                .thenReturn(inserzione);
        when(inserzioneRepository.save(inserzione)).thenReturn(inserzione);
        when(map.toInserzioneResponse(inserzione)).thenReturn(response);

        // WHEN
        InserzioneResponse result =
                inserzioneService.creaInserzione(request, immagini, token);

        // THEN
        assertNotNull(result);

        verify(agenteService).getAgenteFromToken(token);
        verify(posizioneService).creaPosizione(any());
        verify(indicatoreProssimitaService).generaIndicatoriPerInserzione(request);
        verify(inserzioneRepository).save(inserzione);
        verify(fotoService).processImages(immagini, inserzione);
        verify(map).toInserzioneResponse(inserzione);
    }


    @Test
    void creaInserzione_quandoTokenNonValido_lanciaEntityNotFound() throws Exception {

        // GIVEN
        String token = "invalid-token";
        InserzioneRequest request = mock(InserzioneRequest.class);
        MultipartFile[] immagini = new MultipartFile[]{mock(MultipartFile.class)};

        when(agenteService.getAgenteFromToken(token))
                .thenThrow(new EntityNotFoundException("Agente non trovato"));

        // WHEN + THEN
        assertThrows(EntityNotFoundException.class, () ->
                inserzioneService.creaInserzione(request, immagini, token)
        );

        // Verifica assenza di side effects
        verify(agenteService).getAgenteFromToken(token);
        verifyNoInteractions(
                posizioneService,
                indicatoreProssimitaService,
                inserzioneRepository,
                fotoService,
                map
        );
    }

    @Test
    void creaInserzione_quandoRequestNull_lanciaIllegalArgumentException() throws Exception {

        // GIVEN
        String token = "valid-token";
        MultipartFile[] immagini = new MultipartFile[]{mock(MultipartFile.class)};

        // WHEN + THEN
        assertThrows(IllegalArgumentException.class, () ->
                inserzioneService.creaInserzione(null, immagini, token)
        );

        // Nessuna dipendenza deve essere invocata
        verifyNoInteractions(
                agenteService,
                posizioneService,
                indicatoreProssimitaService,
                inserzioneRepository,
                fotoService,
                map
        );
    }


    @Test
    void creaInserzione_quandoPosizioneNull_lanciaIllegalArgumentException() throws Exception {

        // GIVEN
        String token = "valid-token";
        InserzioneRequest request = mock(InserzioneRequest.class);
        MultipartFile[] immagini = new MultipartFile[]{mock(MultipartFile.class)};

        when(request.getPosizione()).thenReturn(null);

        // WHEN + THEN
        assertThrows(IllegalArgumentException.class, () ->
                inserzioneService.creaInserzione(request, immagini, token)
        );

        // Verifica assenza di side effects
        verifyNoInteractions(
                agenteService,
                posizioneService,
                indicatoreProssimitaService,
                inserzioneRepository,
                fotoService,
                map
        );
    }


    @Test
    void creaInserzione_quandoIOExceptionDuranteUpload_propagaEccezione() throws Exception {

        // GIVEN
        String token = "valid-token";

        InserzioneRequest request = mock(InserzioneRequest.class);
        MultipartFile[] immagini = new MultipartFile[]{mock(MultipartFile.class)};

        Agente agente = new Agente();
        Posizione posizione = new Posizione();
        IndicatoreProssimita indicatore = new IndicatoreProssimita();
        Inserzione inserzione = new Inserzione();

        PosizioneRequest posizioneRequest = mock(PosizioneRequest.class);
        when(request.getPosizione()).thenReturn(posizioneRequest);

        when(agenteService.getAgenteFromToken(token)).thenReturn(agente);
        when(posizioneService.creaPosizione(any())).thenReturn(posizione);
        when(indicatoreProssimitaService.generaIndicatoriPerInserzione(request))
                .thenReturn(indicatore);
        when(map.inserzioneToEntity(request, posizione, agente, indicatore))
                .thenReturn(inserzione);
        when(inserzioneRepository.save(inserzione)).thenReturn(inserzione);

        doThrow(new IOException("Errore upload immagini"))
                .when(fotoService).processImages(immagini, inserzione);

        // WHEN + THEN
        assertThrows(IOException.class, () ->
                inserzioneService.creaInserzione(request, immagini, token)
        );

        // Verifica che NON venga costruita la response
        verify(map, never()).toInserzioneResponse(any());

        // Inserzione salvata ma eccezione propagata (coerente col metodo)
        verify(inserzioneRepository).save(inserzione);
    }

}
