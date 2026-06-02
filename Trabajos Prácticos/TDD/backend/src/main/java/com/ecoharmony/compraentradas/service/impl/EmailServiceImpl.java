package com.ecoharmony.compraentradas.service.impl;

import com.ecoharmony.compraentradas.service.*;

import com.ecoharmony.compraentradas.model.Compra;
import com.ecoharmony.compraentradas.model.EstadoCompra;
import com.ecoharmony.compraentradas.model.TipoPase;
import com.ecoharmony.compraentradas.model.Visitante;
import com.ecoharmony.compraentradas.repository.UsuarioRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

  private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final NumberFormat FORMATO_PRECIO =
      NumberFormat.getNumberInstance(new Locale("es", "AR"));

  private final JavaMailSender mailSender;
  private final UsuarioRepository usuarioRepository;

  public EmailServiceImpl(JavaMailSender mailSender, UsuarioRepository usuarioRepository) {
    this.mailSender = mailSender;
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  public void enviarConfirmacion(Compra compra) {
    if (compra.getEstado() != EstadoCompra.PENDIENTE_BOLETERIA
        && compra.getEstado() != EstadoCompra.CONFIRMADA) {
      return;
    }

    String emailDestino =
        usuarioRepository.findById(compra.getUsuarioId()).map(u -> u.getEmail()).orElse(null);

    if (emailDestino == null) return;

    try {
      MimeMessage mime = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mime, true, “UTF-8”);
      helper.setTo(emailDestino);
      helper.setSubject(“Confirmación de compra #” + compra.getId() + “ – EcoHarmony Park”);
      helper.setText(construirHtml(compra), true);

      ClassPathResource logoResource = new ClassPathResource(“images/logo.jpg”);
      if (logoResource.exists()) {
        helper.addInline(“logo-ecoharmony”, logoResource);
      }

      mailSender.send(mime);
      log.info(“Mail enviado a {}”, emailDestino);
    } catch (MailException | jakarta.mail.MessagingException e) {
      log.warn(
          “No se pudo enviar el mail a {} (sin servidor SMTP): {}”, emailDestino, e.getMessage());
    }
  }

  private String construirHtml(Compra compra) {
    String fecha = compra.getFechaVisita().format(FORMATO_FECHA);
    String formaPago =
        compra.getFormaPago().name().equals("EFECTIVO")
            ? "Efectivo en boletería"
            : "Tarjeta (Mercado Pago)";
    String qrBase64 = generarQrBase64(compra);

    StringBuilder filas = new StringBuilder();
    BigDecimal total = BigDecimal.ZERO;
    int numero = 1;

    for (Visitante v : compra.getVisitantes()) {
      BigDecimal subtotal = calcularPrecio(v);
      total = total.add(subtotal);

      String nombreCelda = (v.getNombre() != null) ? v.getNombre() : “–“;
      String descuento = describir(v);

      filas.append(
          """
                <tr>
                  <td style="padding:10px 8px;border-bottom:1px solid #EDE8D5;color:#1B4332">%d</td>
                  <td style="padding:10px 8px;border-bottom:1px solid #EDE8D5">%s</td>
                  <td style="padding:10px 8px;border-bottom:1px solid #EDE8D5">%d años</td>
                  <td style="padding:10px 8px;border-bottom:1px solid #EDE8D5">%s</td>
                  <td style="padding:10px 8px;border-bottom:1px solid #EDE8D5;color:#888">%s</td>
                  <td style="padding:10px 8px;border-bottom:1px solid #EDE8D5;text-align:right;font-weight:600;color:#1B4332">$%s</td>
                </tr>
                """
              .formatted(
                  numero++,
                  nombreCelda,
                  v.getEdad(),
                  v.getTipoPase() == TipoPase.VIP ? "VIP" : "Regular",
                  descuento,
                  formatearPrecio(subtotal)));
    }

    String imgQr =
        qrBase64.isEmpty()
            ? ""
            : """
            <div style="text-align:center;margin:28px 0 8px">
              <img src="data:image/png;base64,%s" width="160" height="160"
                   alt="QR de acceso" style="border:4px solid #D8F3DC;border-radius:8px;padding:4px"/>
              <p style="color:#888;font-size:11px;margin-top:6px">Presentá este código al ingresar al parque</p>
            </div>
            """
                .formatted(qrBase64);

    return """
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"></head>
            <body style="margin:0;padding:0;background:#F8F3E8;font-family:'DM Sans',Arial,sans-serif">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#F8F3E8;padding:32px 0">
                <tr><td align="center">
                  <table width="600" cellpadding="0" cellspacing="0" style="background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,.08)">

                    <!-- Header -->
                    <tr>
                      <td style="background:#1B4332;padding:32px 40px;text-align:center">
                        <img src="cid:logo-ecoharmony" width="72" height="72"
                             alt="EcoHarmony Park"
                             style="border-radius:50%%;object-fit:cover;border:2px solid #6A994E;margin-bottom:12px;display:block;margin-left:auto;margin-right:auto"/>
                        <p style="margin:0 0 4px;color:#52B788;font-size:11px;letter-spacing:3px;text-transform:uppercase">Reserva confirmada</p>
                        <h1 style="margin:0;color:#fff;font-size:32px;font-weight:300;letter-spacing:1px">EcoHarmony Park</h1>
                      </td>
                    </tr>

                    <!-- Intro -->
                    <tr>
                      <td style="padding:32px 40px 16px;text-align:center">
                        <h2 style="margin:0 0 8px;color:#1B4332;font-size:22px">¡Tu compra fue confirmada!</h2>
                        <p style="margin:0;color:#666;font-size:14px">A continuación encontrás el detalle de tu reserva.</p>
                      </td>
                    </tr>

                    <!-- Datos generales -->
                    <tr>
                      <td style="padding:8px 40px 24px">
                        <table width="100%%" cellpadding="0" cellspacing="0" style="background:#F8F3E8;border-radius:10px;padding:20px 24px">
                          <tr>
                            <td style="padding:6px 0;font-size:13px;color:#888;width:40%%">Número de orden</td>
                            <td style="padding:6px 0;font-size:13px;font-weight:700;color:#1B4332">#%s</td>
                          </tr>
                          <tr>
                            <td style="padding:6px 0;font-size:13px;color:#888">Fecha de visita</td>
                            <td style="padding:6px 0;font-size:13px;font-weight:700;color:#1B4332">%s</td>
                          </tr>
                          <tr>
                            <td style="padding:6px 0;font-size:13px;color:#888">Forma de pago</td>
                            <td style="padding:6px 0;font-size:13px;font-weight:700;color:#1B4332">%s</td>
                          </tr>
                        </table>
                      </td>
                    </tr>

                    <!-- Visitantes -->
                    <tr>
                      <td style="padding:0 40px 24px">
                        <p style="margin:0 0 10px;font-size:12px;font-weight:600;color:#1B4332;text-transform:uppercase;letter-spacing:2px">Visitantes</p>
                        <table width="100%%" cellpadding="0" cellspacing="0" style="font-size:13px;color:#333">
                          <thead>
                            <tr style="background:#F8F3E8">
                              <th style="padding:8px;text-align:left;color:#888;font-weight:500">#</th>
                              <th style="padding:8px;text-align:left;color:#888;font-weight:500">Nombre</th>
                              <th style="padding:8px;text-align:left;color:#888;font-weight:500">Edad</th>
                              <th style="padding:8px;text-align:left;color:#888;font-weight:500">Pase</th>
                              <th style="padding:8px;text-align:left;color:#888;font-weight:500">Descuento</th>
                              <th style="padding:8px;text-align:right;color:#888;font-weight:500">Subtotal</th>
                            </tr>
                          </thead>
                          <tbody>%s</tbody>
                        </table>
                      </td>
                    </tr>

                    <!-- Total -->
                    <tr>
                      <td style="padding:0 40px 8px">
                        <table width="100%%" cellpadding="0" cellspacing="0">
                          <tr>
                            <td style="padding:16px 0;border-top:2px solid #1B4332;font-size:18px;font-weight:700;color:#1B4332">Total</td>
                            <td style="padding:16px 0;border-top:2px solid #1B4332;font-size:18px;font-weight:700;color:#1B4332;text-align:right">$%s</td>
                          </tr>
                        </table>
                      </td>
                    </tr>

                    <!-- QR -->
                    <tr><td>%s</td></tr>

                    <!-- Footer -->
                    <tr>
                      <td style="background:#1B4332;padding:24px 40px;text-align:center">
                        <p style=”margin:0;color:#52B788;font-size:12px”>EcoHarmony Park – Sistema de reservas</p>
                        <p style=”margin:4px 0 0;color:#D8F3DC;font-size:11px”>Este es un correo automático, no respondas este mensaje.</p>
                      </td>
                    </tr>

                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """
        .formatted(
            compra.getId(), fecha, formaPago, filas.toString(), formatearPrecio(total), imgQr);
  }

  private String generarQrBase64(Compra compra) {
    try {
      String contenido =
          String.join(
              "\n",
              "EcoHarmony Park",
              "Orden #" + compra.getId(),
              "Fecha: " + compra.getFechaVisita().format(FORMATO_FECHA),
              "Entradas: " + compra.getVisitantes().size());
      QRCodeWriter writer = new QRCodeWriter();
      BitMatrix matrix = writer.encode(contenido, BarcodeFormat.QR_CODE, 300, 300);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(matrix, "PNG", out);
      return Base64.getEncoder().encodeToString(out.toByteArray());
    } catch (Exception e) {
      log.warn("No se pudo generar el QR: {}", e.getMessage());
      return "";
    }
  }

  private BigDecimal calcularPrecio(Visitante v) {
    BigDecimal base = v.getTipoPase().getPrecio();
    if (v.getEdad() <= 3) return BigDecimal.ZERO;
    if (v.getEdad() <= 15 || v.getEdad() >= 60)
      return base.divide(new BigDecimal("2"), 0, RoundingMode.HALF_UP);
    return base;
  }

  private String describir(Visitante v) {
    if (v.getEdad() <= 3) return “Gratis (≤3 años)”;
    if (v.getEdad() <= 15) return “50% off (≤15 años)”;
    if (v.getEdad() >= 60) return “50% off (≥60 años)”;
    return “–“;
  }

  private String formatearPrecio(BigDecimal valor) {
    return FORMATO_PRECIO.format(valor);
  }
}

