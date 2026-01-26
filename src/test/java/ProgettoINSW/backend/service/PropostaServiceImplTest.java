package ProgettoINSW.backend.service;

import ProgettoINSW.backend.dto.proposta.AggiornaStatoPropostaRequest;
import ProgettoINSW.backend.dto.proposta.ContropropostaRequest;
import ProgettoINSW.backend.dto.proposta.PropostaResponse;
import ProgettoINSW.backend.mapper.PropostaMap;
import ProgettoINSW.backend.model.*;
import ProgettoINSW.backend.model.enums.StatoInserzione;
import ProgettoINSW.backend.model.enums.StatoProposta;
import ProgettoINSW.backend.repository.*;
import ProgettoINSW.backend.util.JwtUtil;
import ProgettoINSW.backend.service.impl.PropostaServiceImpl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PropostaServiceImplTest {

    @Mock private PropostaRepository propostaRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private AgenteRepository agenteRepository;
    @Mock private UtenteRepository utenteRepository;
    @Mock private InserzioneRepository inserzioneRepository;
    @Mock private PropostaMap propostaMap;

    @InjectMocks
    private PropostaServiceImpl propostaService;

    /* =========================================================
       CREA CONTROPROPOSTA
       ========================================================= */

    @Test
    void creaControproposta_quandoOk_salvaOriginaleRifiutataESalvaControproposta() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();

        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = mock(Inserzione.class);
        when(inserzione.getPrezzo()).thenReturn(new BigDecimal("100000.00")); // minimo 85k

        Utente cliente = new Utente();
        cliente.setIdUtente(99L);

        Proposta originale = new Proposta();
        originale.setIdProposta(10L);
        originale.setAgente(agente);
        originale.setCliente(cliente);
        originale.setInserzione(inserzione);
        originale.setStato(StatoProposta.IN_ATTESA);
        originale.setPrezzoProposta(new BigDecimal("90000.00"));

        ContropropostaRequest req = new ContropropostaRequest();
        req.setNuovoPrezzo(new BigDecimal("90000.00"));
        req.setNote("Ok a 90k");

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(10L)).thenReturn(Optional.of(originale));

        when(propostaMap.toPropostaResponse(any(Proposta.class), any()))
                .thenReturn(new PropostaResponse());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act
            PropostaResponse out = propostaService.creaControproposta(10L, req, token);

            // Assert
            assertNotNull(out);

            ArgumentCaptor<Proposta> captor = ArgumentCaptor.forClass(Proposta.class);
            verify(propostaRepository, times(2)).save(captor.capture());

            var salvate = captor.getAllValues();
            assertEquals(2, salvate.size());

            // 1) originale salvata come RIFIUTATA
            Proposta originaleSalvata = salvate.get(0);
            assertEquals(StatoProposta.RIFIUTATA, originaleSalvata.getStato());

            // 2) controproposta salvata come CONTROPROPOSTA
            Proposta controSalvata = salvate.get(1);
            assertEquals(StatoProposta.CONTROPROPOSTA, controSalvata.getStato());
            assertEquals(new BigDecimal("90000.00"), controSalvata.getPrezzoProposta());
            assertSame(originale, controSalvata.getPropostaPrecedente());

            verifyNoMoreInteractions(propostaRepository);
        }
    }

    @Test
    void creaControproposta_quandoPropostaNonEsiste_lanciaEntityNotFound() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        ContropropostaRequest req = new ContropropostaRequest();
        req.setNuovoPrezzo(new BigDecimal("90000.00"));

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(10L)).thenReturn(Optional.empty());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act + Assert
            assertThrows(EntityNotFoundException.class,
                    () -> propostaService.creaControproposta(10L, req, token));

            verify(propostaRepository, never()).save(any());
            verifyNoInteractions(propostaMap);
        }
    }

    @Test
    void creaControproposta_quandoAgenteNonAutorizzato_lanciaAccessDenied() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();

        Agente agenteToken = new Agente();
        agenteToken.setIdAgente(1L);

        Agente agenteAltro = new Agente();
        agenteAltro.setIdAgente(2L);

        Inserzione inserzione = new Inserzione(); // Oggetto reale, non mock

        Proposta originale = new Proposta();
        originale.setIdProposta(10L);
        originale.setAgente(agenteAltro);
        originale.setInserzione(inserzione);
        originale.setStato(StatoProposta.IN_ATTESA);

        ContropropostaRequest req = new ContropropostaRequest();
        req.setNuovoPrezzo(new BigDecimal("90000.00"));

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agenteToken));
        when(propostaRepository.findById(10L)).thenReturn(Optional.of(originale));

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act + Assert
            assertThrows(org.springframework.security.access.AccessDeniedException.class,
                    () -> propostaService.creaControproposta(10L, req, token));

            verify(propostaRepository, never()).save(any());
            verifyNoInteractions(propostaMap);
        }
    }

    @Test
    void creaControproposta_quandoOriginaleNonModificabile_lanciaIllegalState() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = new Inserzione(); // Oggetto reale, non mock

        Proposta originale = new Proposta();
        originale.setIdProposta(10L);
        originale.setAgente(agente);
        originale.setInserzione(inserzione);
        originale.setStato(StatoProposta.ACCETTATA); // non modificabile

        ContropropostaRequest req = new ContropropostaRequest();
        req.setNuovoPrezzo(new BigDecimal("90000.00"));

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(10L)).thenReturn(Optional.of(originale));

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act + Assert
            assertThrows(IllegalStateException.class,
                    () -> propostaService.creaControproposta(10L, req, token));

            verify(propostaRepository, never()).save(any());
            verifyNoInteractions(propostaMap);
        }
    }

    @Test
    void creaControproposta_quandoPrezzoSottoMinimo_lanciaIllegalArgument() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = mock(Inserzione.class);
        when(inserzione.getPrezzo()).thenReturn(new BigDecimal("100000.00")); // minimo 85k

        Proposta originale = new Proposta();
        originale.setIdProposta(10L);
        originale.setAgente(agente);
        originale.setInserzione(inserzione);
        originale.setStato(StatoProposta.IN_ATTESA);

        ContropropostaRequest req = new ContropropostaRequest();
        req.setNuovoPrezzo(new BigDecimal("80000.00")); // < 85k

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(10L)).thenReturn(Optional.of(originale));

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act + Assert
            assertThrows(IllegalArgumentException.class,
                    () -> propostaService.creaControproposta(10L, req, token));

            verify(propostaRepository, never()).save(any());
            verifyNoInteractions(propostaMap);
        }
    }

    @Test
    void creaControproposta_quandoAccountNonTrovato_lanciaEntityNotFound() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        ContropropostaRequest req = new ContropropostaRequest();
        req.setNuovoPrezzo(new BigDecimal("90000.00"));

        when(accountRepository.findByMail(mail)).thenReturn(Optional.empty());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act + Assert
            assertThrows(EntityNotFoundException.class,
                    () -> propostaService.creaControproposta(10L, req, token));

            verify(propostaRepository, never()).findById(any());
            verify(propostaRepository, never()).save(any());
            verifyNoInteractions(propostaMap);
        }
    }

    @Test
    void creaControproposta_quandoAgenteNonTrovato_lanciaEntityNotFound() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();

        ContropropostaRequest req = new ContropropostaRequest();
        req.setNuovoPrezzo(new BigDecimal("90000.00"));

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.empty());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act + Assert
            assertThrows(EntityNotFoundException.class,
                    () -> propostaService.creaControproposta(10L, req, token));

            verify(propostaRepository, never()).findById(any());
            verify(propostaRepository, never()).save(any());
            verifyNoInteractions(propostaMap);
        }
    }

    @Test
    void creaControproposta_quandoPrezzoAlMinimo85Percento_salvaCorrettamente() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = mock(Inserzione.class);
        when(inserzione.getPrezzo()).thenReturn(new BigDecimal("100000.00"));

        Utente cliente = new Utente();
        cliente.setIdUtente(99L);

        Proposta originale = new Proposta();
        originale.setIdProposta(10L);
        originale.setAgente(agente);
        originale.setCliente(cliente);
        originale.setInserzione(inserzione);
        originale.setStato(StatoProposta.IN_ATTESA);

        ContropropostaRequest req = new ContropropostaRequest();
        req.setNuovoPrezzo(new BigDecimal("85000.00")); // esattamente 85%
        req.setNote("Prezzo minimo");

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(10L)).thenReturn(Optional.of(originale));
        when(propostaMap.toPropostaResponse(any(Proposta.class), any()))
                .thenReturn(new PropostaResponse());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act
            PropostaResponse out = propostaService.creaControproposta(10L, req, token);

            // Assert
            assertNotNull(out);

            ArgumentCaptor<Proposta> captor = ArgumentCaptor.forClass(Proposta.class);
            verify(propostaRepository, times(2)).save(captor.capture());

            Proposta controSalvata = captor.getAllValues().get(1);
            assertEquals(new BigDecimal("85000.00"), controSalvata.getPrezzoProposta());
            assertEquals("Prezzo minimo", controSalvata.getNote());
        }
    }

    @Test
    void creaControproposta_quandoPrezzoUgualeAPrezzoInserzione_salvaCorrettamente() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = mock(Inserzione.class);
        when(inserzione.getPrezzo()).thenReturn(new BigDecimal("100000.00"));

        Utente cliente = new Utente();
        cliente.setIdUtente(99L);

        Proposta originale = new Proposta();
        originale.setIdProposta(10L);
        originale.setAgente(agente);
        originale.setCliente(cliente);
        originale.setInserzione(inserzione);
        originale.setStato(StatoProposta.IN_ATTESA);

        ContropropostaRequest req = new ContropropostaRequest();
        req.setNuovoPrezzo(new BigDecimal("100000.00")); // prezzo pieno

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(10L)).thenReturn(Optional.of(originale));
        when(propostaMap.toPropostaResponse(any(Proposta.class), any()))
                .thenReturn(new PropostaResponse());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act
            PropostaResponse out = propostaService.creaControproposta(10L, req, token);

            // Assert
            assertNotNull(out);

            ArgumentCaptor<Proposta> captor = ArgumentCaptor.forClass(Proposta.class);
            verify(propostaRepository, times(2)).save(captor.capture());

            Proposta controSalvata = captor.getAllValues().get(1);
            assertEquals(new BigDecimal("100000.00"), controSalvata.getPrezzoProposta());
        }
    }

    @Test
    void creaControproposta_quandoOriginaleRifiutata_lanciaIllegalState() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = new Inserzione();

        Proposta originale = new Proposta();
        originale.setIdProposta(10L);
        originale.setAgente(agente);
        originale.setInserzione(inserzione);
        originale.setStato(StatoProposta.RIFIUTATA);

        ContropropostaRequest req = new ContropropostaRequest();
        req.setNuovoPrezzo(new BigDecimal("90000.00"));

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(10L)).thenReturn(Optional.of(originale));

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act + Assert
            assertThrows(IllegalStateException.class,
                    () -> propostaService.creaControproposta(10L, req, token));

            verify(propostaRepository, never()).save(any());
        }
    }

    @Test
    void creaControproposta_quandoOriginaleGiaControproposta_creaUlterioreCatena() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = mock(Inserzione.class);
        when(inserzione.getPrezzo()).thenReturn(new BigDecimal("100000.00"));

        Utente cliente = new Utente();
        cliente.setIdUtente(99L);

        Proposta originale = new Proposta();
        originale.setIdProposta(10L);
        originale.setAgente(agente);
        originale.setCliente(cliente);
        originale.setInserzione(inserzione);
        originale.setStato(StatoProposta.CONTROPROPOSTA);

        ContropropostaRequest req = new ContropropostaRequest();
        req.setNuovoPrezzo(new BigDecimal("90000.00"));

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(10L)).thenReturn(Optional.of(originale));
        when(propostaMap.toPropostaResponse(any(Proposta.class), any()))
                .thenReturn(new PropostaResponse());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act
            PropostaResponse out = propostaService.creaControproposta(10L, req, token);

            // Assert - verifica che sia possibile creare una catena di controproposte
            assertNotNull(out);

            ArgumentCaptor<Proposta> captor = ArgumentCaptor.forClass(Proposta.class);
            verify(propostaRepository, times(2)).save(captor.capture());

            var salvate = captor.getAllValues();

            // La controproposta originale diventa RIFIUTATA
            assertEquals(StatoProposta.RIFIUTATA, salvate.get(0).getStato());

            // Nuova controproposta creata
            assertEquals(StatoProposta.CONTROPROPOSTA, salvate.get(1).getStato());
            assertEquals(new BigDecimal("90000.00"), salvate.get(1).getPrezzoProposta());
        }
    }

    /* =========================================================
       AGGIORNA STATO PROPOSTA
       ========================================================= */

    @Test
    void aggiornaStatoProposta_quandoAccettata_segnaInserzioneVenduta() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = mock(Inserzione.class);

        Proposta proposta = new Proposta();
        proposta.setIdProposta(50L);
        proposta.setAgente(agente);
        proposta.setInserzione(inserzione);
        proposta.setStato(StatoProposta.IN_ATTESA);

        AggiornaStatoPropostaRequest req = new AggiornaStatoPropostaRequest();
        req.setNuovoStato(StatoProposta.ACCETTATA);

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(50L)).thenReturn(Optional.of(proposta));
        when(propostaMap.toPropostaResponse(any(Proposta.class), any()))
                .thenReturn(new PropostaResponse());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act
            propostaService.aggiornaStatoProposta(50L, req, token);

            // Assert
            verify(propostaRepository).save(proposta);
            assertEquals(StatoProposta.ACCETTATA, proposta.getStato());

            verify(inserzione).setStato(StatoInserzione.VENDUTO);
            verify(inserzioneRepository).save(inserzione);
        }
    }

    @Test
    void aggiornaStatoProposta_quandoNonAccettata_nonModificaInserzione() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = mock(Inserzione.class);

        Proposta proposta = new Proposta();
        proposta.setIdProposta(50L);
        proposta.setAgente(agente);
        proposta.setInserzione(inserzione);
        proposta.setStato(StatoProposta.IN_ATTESA);

        AggiornaStatoPropostaRequest req = new AggiornaStatoPropostaRequest();
        req.setNuovoStato(StatoProposta.RIFIUTATA);

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(50L)).thenReturn(Optional.of(proposta));
        when(propostaMap.toPropostaResponse(any(Proposta.class), any()))
                .thenReturn(new PropostaResponse());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act
            propostaService.aggiornaStatoProposta(50L, req, token);

            // Assert
            verify(propostaRepository).save(proposta);
            assertEquals(StatoProposta.RIFIUTATA, proposta.getStato());

            verifyNoInteractions(inserzioneRepository);
            verify(inserzione, never()).setStato(any());
        }
    }

    @Test
    void aggiornaStatoProposta_quandoAgenteNonAutorizzato_lanciaAccessDenied() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();

        Agente agenteToken = new Agente();
        agenteToken.setIdAgente(1L);

        Agente agenteAltro = new Agente();
        agenteAltro.setIdAgente(2L);

        Inserzione inserzione = mock(Inserzione.class);

        Proposta proposta = new Proposta();
        proposta.setIdProposta(50L);
        proposta.setAgente(agenteAltro);
        proposta.setInserzione(inserzione);

        AggiornaStatoPropostaRequest req = new AggiornaStatoPropostaRequest();
        req.setNuovoStato(StatoProposta.ACCETTATA);

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agenteToken));
        when(propostaRepository.findById(50L)).thenReturn(Optional.of(proposta));

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act + Assert
            assertThrows(org.springframework.security.access.AccessDeniedException.class,
                    () -> propostaService.aggiornaStatoProposta(50L, req, token));

            verify(propostaRepository, never()).save(any());
            verifyNoInteractions(inserzioneRepository);
            verifyNoInteractions(propostaMap);
        }
    }

    @Test
    void aggiornaStatoProposta_quandoPropostaNonEsiste_lanciaEntityNotFound() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        AggiornaStatoPropostaRequest req = new AggiornaStatoPropostaRequest();
        req.setNuovoStato(StatoProposta.ACCETTATA);

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(50L)).thenReturn(Optional.empty());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act + Assert
            assertThrows(EntityNotFoundException.class,
                    () -> propostaService.aggiornaStatoProposta(50L, req, token));

            verify(propostaRepository, never()).save(any());
            verifyNoInteractions(inserzioneRepository);
        }
    }

    @Test
    void aggiornaStatoProposta_quandoAccountNonTrovato_lanciaEntityNotFound() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        AggiornaStatoPropostaRequest req = new AggiornaStatoPropostaRequest();
        req.setNuovoStato(StatoProposta.ACCETTATA);

        when(accountRepository.findByMail(mail)).thenReturn(Optional.empty());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act + Assert
            assertThrows(EntityNotFoundException.class,
                    () -> propostaService.aggiornaStatoProposta(50L, req, token));

            verify(propostaRepository, never()).findById(any());
            verify(propostaRepository, never()).save(any());
        }
    }

    @Test
    void aggiornaStatoProposta_quandoAgenteNonTrovato_lanciaEntityNotFound() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();

        AggiornaStatoPropostaRequest req = new AggiornaStatoPropostaRequest();
        req.setNuovoStato(StatoProposta.ACCETTATA);

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.empty());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act + Assert
            assertThrows(EntityNotFoundException.class,
                    () -> propostaService.aggiornaStatoProposta(50L, req, token));

            verify(propostaRepository, never()).findById(any());
            verify(propostaRepository, never()).save(any());
        }
    }

    @Test
    void aggiornaStatoProposta_quandoCambioAControproposta_nonModificaInserzione() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = mock(Inserzione.class);

        Proposta proposta = new Proposta();
        proposta.setIdProposta(50L);
        proposta.setAgente(agente);
        proposta.setInserzione(inserzione);
        proposta.setStato(StatoProposta.IN_ATTESA);

        AggiornaStatoPropostaRequest req = new AggiornaStatoPropostaRequest();
        req.setNuovoStato(StatoProposta.CONTROPROPOSTA);

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(50L)).thenReturn(Optional.of(proposta));
        when(propostaMap.toPropostaResponse(any(Proposta.class), any()))
                .thenReturn(new PropostaResponse());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act
            propostaService.aggiornaStatoProposta(50L, req, token);

            // Assert
            verify(propostaRepository).save(proposta);
            assertEquals(StatoProposta.CONTROPROPOSTA, proposta.getStato());

            verifyNoInteractions(inserzioneRepository);
            verify(inserzione, never()).setStato(any());
        }
    }

    @Test
    void aggiornaStatoProposta_quandoPropostaGiaAccettata_consenteCambioStato() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = mock(Inserzione.class);

        Proposta proposta = new Proposta();
        proposta.setIdProposta(50L);
        proposta.setAgente(agente);
        proposta.setInserzione(inserzione);
        proposta.setStato(StatoProposta.ACCETTATA);

        AggiornaStatoPropostaRequest req = new AggiornaStatoPropostaRequest();
        req.setNuovoStato(StatoProposta.RIFIUTATA);

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(50L)).thenReturn(Optional.of(proposta));
        when(propostaMap.toPropostaResponse(any(Proposta.class), any()))
                .thenReturn(new PropostaResponse());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act
            propostaService.aggiornaStatoProposta(50L, req, token);

            // Assert - verifica che il cambio sia consentito
            verify(propostaRepository).save(proposta);
            assertEquals(StatoProposta.RIFIUTATA, proposta.getStato());
        }
    }

    @Test
    void aggiornaStatoProposta_quandoDaContropropostaAdAccettata_segnaInserzioneVenduta() {
        // Arrange
        String token = "token";
        String mail = "agente@test.it";

        Account account = new Account();
        Agente agente = new Agente();
        agente.setIdAgente(1L);

        Inserzione inserzione = mock(Inserzione.class);

        Proposta proposta = new Proposta();
        proposta.setIdProposta(50L);
        proposta.setAgente(agente);
        proposta.setInserzione(inserzione);
        proposta.setStato(StatoProposta.CONTROPROPOSTA);

        AggiornaStatoPropostaRequest req = new AggiornaStatoPropostaRequest();
        req.setNuovoStato(StatoProposta.ACCETTATA);

        when(accountRepository.findByMail(mail)).thenReturn(Optional.of(account));
        when(agenteRepository.findByAccount(account)).thenReturn(Optional.of(agente));
        when(propostaRepository.findById(50L)).thenReturn(Optional.of(proposta));
        when(propostaMap.toPropostaResponse(any(Proposta.class), any()))
                .thenReturn(new PropostaResponse());

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {
            jwt.when(() -> JwtUtil.extractMail(token)).thenReturn(mail);

            // Act
            propostaService.aggiornaStatoProposta(50L, req, token);

            // Assert
            verify(propostaRepository).save(proposta);
            assertEquals(StatoProposta.ACCETTATA, proposta.getStato());

            verify(inserzione).setStato(StatoInserzione.VENDUTO);
            verify(inserzioneRepository).save(inserzione);
        }
    }
}