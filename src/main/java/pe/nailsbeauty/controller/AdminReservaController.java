package pe.nailsbeauty.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.nailsbeauty.entity.*;
import pe.nailsbeauty.service.FacturaService;
import pe.nailsbeauty.service.ReservaService;

@Controller
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private FacturaService facturaService;

    @GetMapping
    public String getAll(Model model) {
        List<ReservaEntity> reservas = reservaService.getAll();
        model.addAttribute("reservas", reservas);
        model.addAttribute("estadosReserva", ReservaEntity.EstadoReserva.values());
        return "admin/reservas/adminListaReservas";
    }

    @PostMapping("/guardar")
    public String guardarEstado(@RequestParam("id") Integer id,
                                @RequestParam("estado") ReservaEntity.EstadoReserva estado,
                                @RequestParam(value = "generarFactura", required = false, defaultValue = "false") boolean generarFactura,
                                RedirectAttributes redirectAttributes) {
        try {
            reservaService.updateEstado(id, estado);

            if (estado == ReservaEntity.EstadoReserva.ATENDIDA && generarFactura) {
                if (!facturaService.existeFacturaPorReserva(id)) {
                    ReservaEntity reserva = reservaService.getById(id);
                    FacturaEntity factura = FacturaEntity.builder()
                            .numeroComprobante(facturaService.generarNumeroComprobante())
                            .reserva(reserva)
                            .nombreComercial("Nails Beauty")
                            .ruc("20512684759")
                            .direccion("Av. Ejemplo 123, Lima")
                            .clienteNombre(reserva.getUsuario().getNombre() + " " +
                                    reserva.getUsuario().getApellidoPaterno() + " " +
                                    reserva.getUsuario().getApellidoMaterno())
                            .clienteCorreo(reserva.getUsuario().getCorreo())
                            .servicioNombre(reserva.getServicio().getNombreServicio())
                            .precioServicio(reserva.getServicio().getPrecio())
                            .descuento(BigDecimal.ZERO)
                            .igv(facturaService.calcularIgv(reserva.getServicio().getPrecio()))
                            .totalPagar(facturaService.calcularTotal(
                                    reserva.getServicio().getPrecio(),
                                    BigDecimal.ZERO,
                                    facturaService.calcularIgv(reserva.getServicio().getPrecio())))
                            .metodoPago(MetodoPago.EFECTIVO)
                            .estado(EstadoFactura.PENDIENTE_PAGO)
                            .build();

                    FacturaEntity facturaGuardada = facturaService.save(factura);
                    redirectAttributes.addFlashAttribute("success",
                            "Reserva atendida. Factura " + facturaGuardada.getNumeroComprobante() + " generada.");
                    return "redirect:/admin/facturas/form/" + facturaGuardada.getId();
                } else {
                    redirectAttributes.addFlashAttribute("info",
                            "Reserva atendida. Ya existe una factura para esta reserva.");
                }
            } else {
                redirectAttributes.addFlashAttribute("success", "Estado actualizado correctamente");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reservas";
    }

    @PostMapping("/eliminar/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Reserva eliminada correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/reservas";
    }

    @GetMapping("/exportar-csv")
    public ResponseEntity<byte[]> exportarCsv() {
        List<ReservaEntity> reservas = reservaService.getAll();

        String BOM = "\uFEFF";
        StringBuilder csv = new StringBuilder(BOM);
        csv.append("ID Reserva;Cliente;Servicio;Fecha;Hora;Estado;Observación;Fecha Registro\n");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (ReservaEntity r : reservas) {
            String cliente = r.getUsuario() != null
                    ? r.getUsuario().getNombre() + " " + r.getUsuario().getApellidoPaterno()
                    : "";
            String servicio = r.getServicio() != null ? r.getServicio().getNombreServicio() : "";
            String fecha = r.getFechaReserva() != null ? r.getFechaReserva().format(dateFormatter) : "";
            String hora = r.getHorario() != null ? r.getHorario().getHoraInicio().toString() : "";
            String estado = r.getEstado() != null ? r.getEstado().name() : "";
            String observacion = r.getObservacion() != null ? r.getObservacion().replace(";", ",") : "";
            String fechaRegistro = r.getFechaRegistro() != null ? r.getFechaRegistro().format(dateTimeFormatter) : "";

            csv.append(String.format("%d;\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"\n",
                    r.getId(),
                    escape(cliente),
                    escape(servicio),
                    fecha,
                    hora,
                    estado,
                    escape(observacion),
                    fechaRegistro));
        }

        byte[] utf8 = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reservas.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(utf8);
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
