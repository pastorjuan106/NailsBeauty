package pe.nailsbeauty.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pe.nailsbeauty.entity.FacturaEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final PdfService pdfService;

    @Async
    public void enviarFacturaPorCorreo(FacturaEntity factura) {
        try {
            byte[] pdfBytes = pdfService.generarFacturaPdf(factura);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("nailsbeauty@gmail.com");
            helper.setTo(factura.getClienteCorreo());
            helper.setSubject("Factura " + factura.getNumeroComprobante() + " - Nails Beauty");

            String htmlContent = construirHtmlEmail(factura);
            helper.setText(htmlContent, true);

            helper.addAttachment("Factura_" + factura.getNumeroComprobante() + ".pdf",
                    new org.springframework.core.io.ByteArrayResource(pdfBytes));

            mailSender.send(message);
            log.info("Factura enviada por correo a: {}", factura.getClienteCorreo());
        } catch (MessagingException e) {
            log.error("Error al enviar factura por correo a {}: {}", factura.getClienteCorreo(), e.getMessage());
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }
    }

    private String construirHtmlEmail(FacturaEntity factura) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #fff7fa; margin: 0; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #d63384, #b3005e); color: white; padding: 30px; text-align: center; }
                        .header h1 { margin: 0; font-size: 28px; }
                        .header p { margin: 5px 0 0; opacity: 0.9; }
                        .content { padding: 30px; }
                        .info-box { background: #fff7fa; border-left: 4px solid #d63384; padding: 15px; margin: 15px 0; border-radius: 0 5px 5px 0; }
                        .info-box p { margin: 5px 0; color: #333; }
                        .total-section { background: #d63384; color: white; padding: 20px; text-align: center; border-radius: 5px; margin: 20px 0; }
                        .total-section h2 { margin: 0; font-size: 24px; }
                        .total-section p { margin: 5px 0 0; opacity: 0.9; }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; border-top: 1px solid #eee; }
                        .attachment-note { background: #f0f0f0; padding: 15px; text-align: center; border-radius: 5px; margin: 15px 0; }
                        .attachment-note p { margin: 0; color: #555; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Nails Beauty</h1>
                            <p>Tu comprobante de pago</p>
                        </div>
                        <div class="content">
                            <p>Estimado/a <strong>%s</strong>,</p>
                            <p>Le enviamos su comprobante de venta adjunto a este correo.</p>
    
                            <div class="info-box">
                                <p><strong>N° Comprobante:</strong> %s</p>
                                <p><strong>Fecha:</strong> %s</p>
                                <p><strong>Servicio:</strong> %s</p>
                                <p><strong>Método de Pago:</strong> %s</p>
                            </div>
    
                            <div class="total-section">
                                <h2>TOTAL: S/. %s</h2>
                                <p>IGV incluido</p>
                            </div>
    
                            <div class="attachment-note">
                                <p>📎 Adjunto: PDF de su factura</p>
                            </div>
    
                            <p style="color: #666; font-size: 13px;">Si tiene alguna consulta, no dude en comunicarse con nosotros.</p>
                        </div>
                        <div class="footer">
                            <p>Nails Beauty - Av. Ejemplo 123, Lima</p>
                            <p>RUC: 20512684759 | Tel: (01) 123-4567</p>
                            <p>© 2026 Nails Beauty. Todos los derechos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                factura.getClienteNombre(),
                factura.getNumeroComprobante(),
                factura.getFechaEmision().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                factura.getServicioNombre(),
                factura.getMetodoPago().getValor(),
                factura.getTotalPagar()
        );
    }
}
