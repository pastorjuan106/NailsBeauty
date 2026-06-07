package pe.nailsbeauty.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.nailsbeauty.entity.EstadoFactura;
import pe.nailsbeauty.entity.FacturaEntity;
import pe.nailsbeauty.entity.MetodoPago;
import pe.nailsbeauty.service.EmailService;
import pe.nailsbeauty.service.FacturaService;
import pe.nailsbeauty.service.PdfService;

@Controller
@RequestMapping("/admin/facturas")
public class AdminFacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String getAll(Model model,
                        @RequestParam(value = "busqueda", required = false) String busqueda) {
        List<FacturaEntity> facturas;

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            facturas = facturaService.filtrar(busqueda);
            model.addAttribute("busqueda", busqueda);
        } else {
            facturas = facturaService.getAll();
        }

        model.addAttribute("facturas", facturas);
        model.addAttribute("totalFacturasPagadas", facturaService.contarFacturasPagadas());
        model.addAttribute("totalIngresos", facturaService.sumarTotalFacturasPagadas());

        return "admin/facturas/lista";
    }

    @GetMapping("/form")
    public String createForm(Model model) {
        model.addAttribute("factura", new FacturaEntity());
        model.addAttribute("metodosPago", MetodoPago.values());
        return "admin/facturas/form";
    }

    @GetMapping("/form/{id}")
    public String editForm(@PathVariable Integer id, Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            FacturaEntity factura = facturaService.getById(id);
            model.addAttribute("factura", factura);
            model.addAttribute("metodosPago", MetodoPago.values());
            return "admin/facturas/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Factura no encontrada");
            return "redirect:/admin/facturas";
        }
    }

    @PostMapping("/guardar")
    public String save(@ModelAttribute("factura") FacturaEntity factura,
                       @RequestParam(value = "reserva.id", required = false) Integer idReserva,
                       RedirectAttributes redirectAttributes) {
        try {
            if (factura.getId() != null) {
                facturaService.actualizar(factura.getId(), factura.getDescuento(),
                        factura.getMetodoPago(), factura.getObservaciones());
                redirectAttributes.addFlashAttribute("success", "Factura actualizada correctamente");
            } else if (idReserva != null) {
                FacturaEntity nuevaFactura = facturaService.crearDesdeReserva(idReserva);
                facturaService.actualizar(nuevaFactura.getId(), factura.getDescuento(),
                        factura.getMetodoPago(), factura.getObservaciones());
                redirectAttributes.addFlashAttribute("success", "Factura generada correctamente");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/facturas";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Integer id) {
        try {
            FacturaEntity factura = facturaService.getById(id);
            byte[] pdfBytes = pdfService.generarFacturaPdf(factura);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=Factura_" + factura.getNumeroComprobante() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/enviar-correo")
    public String enviarCorreo(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            FacturaEntity factura = facturaService.getById(id);
            emailService.enviarFacturaPorCorreo(factura);
            facturaService.actualizarEstado(id, EstadoFactura.PAGADA);
            redirectAttributes.addFlashAttribute("success",
                    "Factura enviada exitosamente a " + factura.getClienteCorreo());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al enviar correo: " + e.getMessage());
        }
        return "redirect:/admin/facturas";
    }

    @GetMapping("/{id}/detalle")
    public String detalle(@PathVariable Integer id, Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            FacturaEntity factura = facturaService.getById(id);
            model.addAttribute("factura", factura);
            return "admin/facturas/detalle";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Factura no encontrada");
            return "redirect:/admin/facturas";
        }
    }

    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstado(@PathVariable Integer id,
                                @RequestParam("estado") EstadoFactura estado,
                                RedirectAttributes redirectAttributes) {
        try {
            facturaService.actualizarEstado(id, estado);
            redirectAttributes.addFlashAttribute("success", "Estado de factura actualizado");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/facturas";
    }

    @GetMapping("/exportar-csv")
    public ResponseEntity<byte[]> exportarCsv() {
        List<FacturaEntity> facturas = facturaService.getAll();

        String BOM = "\uFEFF";
        StringBuilder csv = new StringBuilder(BOM);
        csv.append("N° Comprobante;Fecha;Cliente;Correo;Servicio;Precio;Descuento;IGV;Total;Método de Pago;Estado\n");

        for (FacturaEntity f : facturas) {
            csv.append(String.format("\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";%.2f;%.2f;%.2f;%.2f;\"%s\";\"%s\"\n",
                    escape(f.getNumeroComprobante()),
                    f.getFechaEmision() != null ? f.getFechaEmision().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "",
                    escape(f.getClienteNombre()),
                    escape(f.getClienteCorreo()),
                    escape(f.getServicioNombre()),
                    f.getPrecioServicio() != null ? f.getPrecioServicio() : BigDecimal.ZERO,
                    f.getDescuento() != null ? f.getDescuento() : BigDecimal.ZERO,
                    f.getIgv() != null ? f.getIgv() : BigDecimal.ZERO,
                    f.getTotalPagar() != null ? f.getTotalPagar() : BigDecimal.ZERO,
                    f.getMetodoPago() != null ? f.getMetodoPago().getValor() : "",
                    f.getEstado() != null ? f.getEstado().getValor() : ""));
        }

        byte[] utf8 = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=facturas.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(utf8);
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
