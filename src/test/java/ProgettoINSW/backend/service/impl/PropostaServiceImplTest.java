package ProgettoINSW.backend.service.impl;

import ProgettoINSW.backend.dto.proposta.PropostaRequest;
import ProgettoINSW.backend.dto.proposta.PropostaResponse;
import ProgettoINSW.backend.mapper.PropostaMap;
import ProgettoINSW.backend.model.*;
import ProgettoINSW.backend.model.enums.StatoInserzione;
import ProgettoINSW.backend.model.enums.StatoProposta;
import ProgettoINSW.backend.repository.*;
import ProgettoINSW.backend.util.JwtUtil;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropostaServiceImplTest {

    @InjectMocks
    private PropostaServiceImpl propostaService;

    @Mock private PropostaRepository propostaRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private AgenteRepository agenteRepository;
    @Mock private UtenteRepository utenteRepository;
    @Mock private InserzioneRepository inserzioneRepository;
    @Mock private PropostaMap propostaMap;

    /* =========================================================
       TEST 1 — inviaProposta_quandoOk_salvaPropostaInAttesa
       ========================================================= */
    @Test
    void inviaProposta_quandoOk_salvaPropostaInAttesa() {

        String token = "token";

        Account account = new Account();
        account.setId(1L);
        account.setMail("utente@test.it");

        Utente utente = new Utente();
        utente.setIdUtente(10L);
        utente.setAccount(account);

        Agente agente = new Agente();
        agente.setIdAgente(20L);

        Inserzione inserzione = new Inserzione();
        inserzione.setIdInserzione(100L);
        inserzione.setPrezzo(new BigDecimal("100000.00"));
        inserzione.setStato(StatoInserzione.DISPONIBILE);
        inserzione.setAgente(agente);

        PropostaRequest request = new PropostaRequest();
        request.setIdInserzione(100L);
        request.setPrezzoProposta(new BigDecimal("90000.00"));

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {

            jwt.when(() -> JwtUtil.extractMail(token))
                    .thenReturn("utente@test.it");

            when(accountRepository.findByMail("utente@test.it"))
                    .thenReturn(Optional.of(account));

            when(utenteRepository.findByAccount_Id(1L))
                    .thenReturn(Optional.of(utente));

            when(inserzioneRepository.findById(100L))
                    .thenReturn(Optional.of(inserzione));

            when(propostaRepository.existsByClienteAndInserzione(utente, inserzione))
                    .thenReturn(false);

            when(propostaRepository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            when(propostaMap.toPropostaResponse(any(), any()))
                    .thenReturn(new PropostaResponse());

            PropostaResponse response =
                    propostaService.inviaProposta(request, token);

            ArgumentCaptor<Proposta> captor =
                    ArgumentCaptor.forClass(Proposta.class);

            verify(propostaRepository).save(captor.capture());

            Proposta salvata = captor.getValue();

            assertEquals(StatoProposta.IN_ATTESA, salvata.getStato());
            assertEquals(utente, salvata.getCliente());
            assertEquals(inserzione, salvata.getInserzione());
            assertEquals(new BigDecimal("90000.00"), salvata.getPrezzoProposta());
            assertNotNull(response);
        }
    }

    /* =========================================================
       TEST 2 — utente non trovato → EntityNotFoundException
       ========================================================= */
    @Test
    void inviaProposta_quandoUtenteNonTrovato_lanciaEntityNotFound() {

        String token = "token";

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {

            jwt.when(() -> JwtUtil.extractMail(token))
                    .thenReturn("utente@test.it");

            when(accountRepository.findByMail("utente@test.it"))
                    .thenReturn(Optional.empty());

            PropostaRequest request = new PropostaRequest();
            request.setIdInserzione(1L);
            request.setPrezzoProposta(BigDecimal.TEN);

            assertThrows(EntityNotFoundException.class,
                    () -> propostaService.inviaProposta(request, token));

            verify(propostaRepository, never()).save(any());
        }
    }

    /* =========================================================
       TEST 3 — inserzione non esistente → EntityNotFoundException
       ========================================================= */
    @Test
    void inviaProposta_quandoInserzioneNonEsiste_lanciaEntityNotFound() {

        String token = "token";

        Account account = new Account();
        account.setId(1L);
        account.setMail("utente@test.it");

        Utente utente = new Utente();
        utente.setIdUtente(10L);
        utente.setAccount(account);

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {

            jwt.when(() -> JwtUtil.extractMail(token))
                    .thenReturn("utente@test.it");

            when(accountRepository.findByMail("utente@test.it"))
                    .thenReturn(Optional.of(account));

            when(utenteRepository.findByAccount_Id(1L))
                    .thenReturn(Optional.of(utente));

            when(inserzioneRepository.findById(99L))
                    .thenReturn(Optional.empty());

            PropostaRequest request = new PropostaRequest();
            request.setIdInserzione(99L);
            request.setPrezzoProposta(BigDecimal.TEN);

            assertThrows(EntityNotFoundException.class,
                    () -> propostaService.inviaProposta(request, token));
        }
    }

    /* =========================================================
       TEST 4 — inserzione non disponibile → IllegalStateException
       ========================================================= */
    @Test
    void inviaProposta_quandoInserzioneNonDisponibile_lanciaIllegalState() {

        String token = "token";

        Account account = new Account();
        account.setId(1L);
        account.setMail("utente@test.it");

        Utente utente = new Utente();
        utente.setIdUtente(10L);
        utente.setAccount(account);

        Inserzione inserzione = new Inserzione();
        inserzione.setIdInserzione(1L);
        inserzione.setPrezzo(new BigDecimal("100000"));
        inserzione.setStato(StatoInserzione.VENDUTO);

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {

            jwt.when(() -> JwtUtil.extractMail(token))
                    .thenReturn("utente@test.it");

            when(accountRepository.findByMail("utente@test.it"))
                    .thenReturn(Optional.of(account));

            when(utenteRepository.findByAccount_Id(1L))
                    .thenReturn(Optional.of(utente));

            when(inserzioneRepository.findById(1L))
                    .thenReturn(Optional.of(inserzione));

            PropostaRequest request = new PropostaRequest();
            request.setIdInserzione(1L);
            request.setPrezzoProposta(new BigDecimal("90000"));

            assertThrows(IllegalStateException.class,
                    () -> propostaService.inviaProposta(request, token));
        }
    }

    /* =========================================================
       TEST 5 — prezzo sotto soglia → IllegalArgumentException
       ========================================================= */
    @Test
    void inviaProposta_quandoPrezzoSottoSoglia_lanciaIllegalArgument() {

        String token = "token";

        Account account = new Account();
        account.setId(1L);
        account.setMail("utente@test.it");

        Utente utente = new Utente();
        utente.setIdUtente(10L);
        utente.setAccount(account);

        Inserzione inserzione = new Inserzione();
        inserzione.setIdInserzione(1L);
        inserzione.setPrezzo(new BigDecimal("100000"));
        inserzione.setStato(StatoInserzione.DISPONIBILE);

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {

            jwt.when(() -> JwtUtil.extractMail(token))
                    .thenReturn("utente@test.it");

            when(accountRepository.findByMail("utente@test.it"))
                    .thenReturn(Optional.of(account));

            when(utenteRepository.findByAccount_Id(1L))
                    .thenReturn(Optional.of(utente));

            when(inserzioneRepository.findById(1L))
                    .thenReturn(Optional.of(inserzione));

            PropostaRequest request = new PropostaRequest();
            request.setIdInserzione(1L);
            request.setPrezzoProposta(new BigDecimal("80000")); // < 85%

            assertThrows(IllegalArgumentException.class,
                    () -> propostaService.inviaProposta(request, token));
        }
    }

    /* =========================================================
       TEST 6 — prezzo al limite 85% → OK
       ========================================================= */
    @Test
    void inviaProposta_quandoPrezzoAlLimite85Percento_salvaCorrettamente() {

        String token = "token";

        Account account = new Account();
        account.setId(1L);
        account.setMail("utente@test.it");

        Utente utente = new Utente();
        utente.setIdUtente(10L);
        utente.setAccount(account);

        Inserzione inserzione = new Inserzione();
        inserzione.setIdInserzione(1L);
        inserzione.setPrezzo(new BigDecimal("100000"));
        inserzione.setStato(StatoInserzione.DISPONIBILE);

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {

            jwt.when(() -> JwtUtil.extractMail(token))
                    .thenReturn("utente@test.it");

            when(accountRepository.findByMail("utente@test.it"))
                    .thenReturn(Optional.of(account));

            when(utenteRepository.findByAccount_Id(1L))
                    .thenReturn(Optional.of(utente));

            when(inserzioneRepository.findById(1L))
                    .thenReturn(Optional.of(inserzione));

            when(propostaRepository.existsByClienteAndInserzione(any(), any()))
                    .thenReturn(false);

            when(propostaRepository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            when(propostaMap.toPropostaResponse(any(), any()))
                    .thenReturn(new PropostaResponse());

            PropostaRequest request = new PropostaRequest();
            request.setIdInserzione(1L);
            request.setPrezzoProposta(new BigDecimal("85000"));

            assertDoesNotThrow(
                    () -> propostaService.inviaProposta(request, token)
            );
        }
    }

    /* =========================================================
       TEST 7 — proposta già esistente → IllegalStateException
       ========================================================= */
    @Test
    void inviaProposta_quandoPropostaGiaEsistente_lanciaIllegalState() {

        String token = "token";

        Account account = new Account();
        account.setId(1L);
        account.setMail("utente@test.it");

        Utente utente = new Utente();
        utente.setIdUtente(10L);
        utente.setAccount(account);

        Inserzione inserzione = new Inserzione();
        inserzione.setIdInserzione(1L);
        inserzione.setPrezzo(new BigDecimal("100000"));
        inserzione.setStato(StatoInserzione.DISPONIBILE);

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {

            jwt.when(() -> JwtUtil.extractMail(token))
                    .thenReturn("utente@test.it");

            when(accountRepository.findByMail("utente@test.it"))
                    .thenReturn(Optional.of(account));

            when(utenteRepository.findByAccount_Id(1L))
                    .thenReturn(Optional.of(utente));

            when(inserzioneRepository.findById(1L))
                    .thenReturn(Optional.of(inserzione));

            when(propostaRepository.existsByClienteAndInserzione(utente, inserzione))
                    .thenReturn(true);

            PropostaRequest request = new PropostaRequest();
            request.setIdInserzione(1L);
            request.setPrezzoProposta(new BigDecimal("90000"));

            assertThrows(IllegalStateException.class,
                    () -> propostaService.inviaProposta(request, token));
        }
    }

    /* =========================================================
       TEST 8 — prezzo uguale a inserzione → OK
       ========================================================= */
    @Test
    void inviaProposta_quandoPrezzoUgualeInserzione_salvaCorrettamente() {

        String token = "token";

        Account account = new Account();
        account.setId(1L);
        account.setMail("utente@test.it");

        Utente utente = new Utente();
        utente.setIdUtente(10L);
        utente.setAccount(account);

        Inserzione inserzione = new Inserzione();
        inserzione.setIdInserzione(1L);
        inserzione.setPrezzo(new BigDecimal("100000"));
        inserzione.setStato(StatoInserzione.DISPONIBILE);

        try (MockedStatic<JwtUtil> jwt = mockStatic(JwtUtil.class)) {

            jwt.when(() -> JwtUtil.extractMail(token))
                    .thenReturn("utente@test.it");

            when(accountRepository.findByMail("utente@test.it"))
                    .thenReturn(Optional.of(account));

            when(utenteRepository.findByAccount_Id(1L))
                    .thenReturn(Optional.of(utente));

            when(inserzioneRepository.findById(1L))
                    .thenReturn(Optional.of(inserzione));

            when(propostaRepository.existsByClienteAndInserzione(any(), any()))
                    .thenReturn(false);

            when(propostaRepository.save(any()))
                    .thenAnswer(inv -> inv.getArgument(0));

            when(propostaMap.toPropostaResponse(any(), any()))
                    .thenReturn(new PropostaResponse());

            PropostaRequest request = new PropostaRequest();
            request.setIdInserzione(1L);
            request.setPrezzoProposta(new BigDecimal("100000"));

            assertDoesNotThrow(
                    () -> propostaService.inviaProposta(request, token)
            );
        }
    }
}



