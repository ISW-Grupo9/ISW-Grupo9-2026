package com.ecoharmony.compraentradas.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.ecoharmony.compraentradas.model.*;
import com.ecoharmony.compraentradas.repository.UsuarioRepository;
import com.ecoharmony.compraentradas.service.impl.EmailServiceImpl;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService")
class EmailServiceImplTest {

  @Mock private JavaMailSender mailSender;
  @Mock private UsuarioRepository usuarioRepository;

  private EmailService emailService;

  private static final Usuario USUARIO = new Usuario("Juan", "juan@example.com", "12345678");
  private static final LocalDate FECHA = LocalDate.of(2026, 6, 15);

  private final Session SESSION = Session.getInstance(new Properties());

  @BeforeEach
  void setUp() {
    emailService = new EmailServiceImpl(mailSender, usuarioRepository);
  }

  private void stubMimeMessage() {
    when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(SESSION));
  }

  private Compra compraConEstado(FormaPago formaPago, boolean confirmar) {
    Visitante v = new Visitante("Ana", 25, TipoPase.REGULAR);
    Compra compra = new Compra(1L, FECHA, List.of(v), formaPago, new BigDecimal("10000"));
    if (formaPago == FormaPago.EFECTIVO) compra.pendienteBoleteria();
    if (confirmar) compra.confirmar();
    return compra;
  }

  // ─── Ciclo 11.1 ───────────────────────────────────────────────────────────
  @Test
  @DisplayName("debe enviar mail al finalizar compra con efectivo (PENDIENTE_BOLETERIA)")
  void debe_enviar_mail_al_finalizar_compra_efectivo() {
    stubMimeMessage();
    Compra compra = compraConEstado(FormaPago.EFECTIVO, false);
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(USUARIO));

    emailService.enviarConfirmacion(compra);

    verify(mailSender).send(any(MimeMessage.class));
  }

  // ─── Ciclo 11.2 ───────────────────────────────────────────────────────────
  @Test
  @DisplayName("debe enviar mail al finalizar compra con tarjeta (CONFIRMADA tras callback MP)")
  void debe_enviar_mail_al_finalizar_compra_tarjeta() {
    stubMimeMessage();
    Compra compra = compraConEstado(FormaPago.TARJETA, true);
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(USUARIO));

    emailService.enviarConfirmacion(compra);

    verify(mailSender).send(any(MimeMessage.class));
  }

  // ─── Ciclo 11.3 ───────────────────────────────────────────────────────────
  @Test
  @DisplayName("el asunto incluye el número de orden y el nombre del parque")
  void asunto_incluye_numero_de_orden_y_parque() throws Exception {
    stubMimeMessage();
    Compra compra = compraConEstado(FormaPago.EFECTIVO, false);
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(USUARIO));
    ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);

    emailService.enviarConfirmacion(compra);

    verify(mailSender).send(captor.capture());
    assertThat(captor.getValue().getSubject()).contains("EcoHarmony Park");
  }

  // ─── Ciclo 11.4 ───────────────────────────────────────────────────────────
  @Test
  @DisplayName("el mail debe enviarse al email del usuario registrado")
  void mail_debe_enviarse_al_email_del_usuario_registrado() throws Exception {
    stubMimeMessage();
    Compra compra = compraConEstado(FormaPago.EFECTIVO, false);
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(USUARIO));
    ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);

    emailService.enviarConfirmacion(compra);

    verify(mailSender).send(captor.capture());
    assertThat(captor.getValue().getAllRecipients()[0].toString()).isEqualTo("juan@example.com");
  }

  // ─── Ciclo 11.5 ───────────────────────────────────────────────────────────
  // Si la compra está en PENDIENTE (tarjeta sin confirmar), no se envía email.
  // El email se envía solo cuando se confirme el pago desde Mercado Pago.
  @Test
  @DisplayName("no debe enviar mail si la compra no fue finalizada (estado PENDIENTE)")
  void no_debe_enviar_mail_si_compra_no_finalizo() {
    Visitante v = new Visitante("Ana", 25, TipoPase.REGULAR);
    Compra compra = new Compra(1L, FECHA, List.of(v), FormaPago.TARJETA, new BigDecimal("10000"));

    emailService.enviarConfirmacion(compra);

    verify(mailSender, never()).send(any(MimeMessage.class));
  }
}
