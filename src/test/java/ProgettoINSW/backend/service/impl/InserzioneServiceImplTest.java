package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.datiInserzione.DatiInserzioneRequest;
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
    /*@Test
    void creaInserzione_quandoOk_salvaInserzioneEImmagini() throws Exception {
        // GIVEN
        String token = "valid-token";

        // 1. Mock della richiesta principale
        InserzioneRequest request = mock(InserzioneRequest.class);
        // 2. Mock dell'oggetto annidato (DatiInserzioneRequest) per evitare il NullPointerException
        DatiInserzioneRequest datiRequest = mock(DatiInserzioneRequest.class);

        // Configuriamo la catena: request.getDatiInserzioneRequest() -> datiRequest -> getTitolo()
        when(request.getDatiInserzioneRequest()).thenReturn(datiRequest);
        when(datiRequest.getTitolo()).thenReturn("Appartamento Test");

        MultipartFile[] immagini = new MultipartFile[]{mock(MultipartFile.class)};

        Agente agente = new Agente();

        // Prepariamo la posizione con dati per superare la validazione
        Posizione posizione = new Posizione();
        posizione.setIndirizzo("Via Roma 1");
        posizione.setComune("Napoli");

        IndicatoreProssimita indicatore = new IndicatoreProssimita();
        Inserzione inserzione = new Inserzione();
        inserzione.setPosizione(posizione);
        InserzioneResponse response = new InserzioneResponse();

        // Configurazione dei Service
        when(agenteService.getAgenteFromToken(token)).thenReturn(agente);
        when(posizioneService.creaPosizione(any())).thenReturn(posizione);
        when(indicatoreProssimitaService.generaIndicatoriPerInserzione(any())).thenReturn(indicatore);

        // Matchers flessibili per il Mapper
        when(map.inserzioneToEntity(any(), any(), any(), any())).thenReturn(inserzione);
        when(inserzioneRepository.save(any(Inserzione.class))).thenReturn(inserzione);
        when(map.toInserzioneResponse(any())).thenReturn(response);

        // WHEN
        InserzioneResponse result = inserzioneService.creaInserzione(request, immagini, token);

        // THEN
        assertNotNull(result);
        verify(agenteService).getAgenteFromToken(token);
        verify(posizioneService).creaPosizione(any());
        verify(inserzioneRepository).save(any(Inserzione.class));
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
    }*/

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
