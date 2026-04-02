package pe.nailsbeauty.controller;

import pe.nailsbeauty.entity.ReservaEntity;
import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.entity.ReservaEntity.EstadoReserva;
import pe.nailsbeauty.service.ContactoService;
import pe.nailsbeauty.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin/agenda")
public class AgendaController {

    @Autowired
    private ReservaService reservaService;
    @Autowired
    private ContactoService contactoService;
    @GetMapping
    public String mostrarAgenda() {
        return "admin/dashboard"; 
    }

    @GetMapping("/eventos")
    @ResponseBody
    public List<Map<String, Object>> obtenerEventos(
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) Integer idServicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer idHorario
    ) {
        List<ReservaEntity> reservas;

        if (idUsuario != null || idServicio != null || fecha != null || idHorario != null) {
            reservas = reservaService.filtrarReservas(idUsuario, idServicio, fecha, idHorario);
        } else {
            reservas = reservaService.getAll();
        }

        List<Map<String, Object>> eventos = new ArrayList<>();
        for (ReservaEntity r : reservas) {
            if (r.getEstado() == EstadoReserva.PENDIENTE && r.getServicio() != null
                && r.getFechaReserva() != null && r.getHorario() != null) {

                Map<String, Object> evento = new HashMap<>();
                evento.put("id", r.getId());
                evento.put("title", r.getServicio().getNombreServicio() + " - " + r.getUsuario().getNombre());
                evento.put("start", r.getFechaReserva() + "T" + r.getHorario().getHoraInicio());
                evento.put("end", r.getFechaReserva() + "T" + r.getHorario().getHoraFin());
                evento.put("estado", r.getEstado().name());
                eventos.add(evento);
            }
        }
        return eventos;
        
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        int totalReservasMes = reservaService.contarReservasMesActual();
        int reservasPendientes = reservaService.contarReservasPorEstado(ReservaEntity.EstadoReserva.PENDIENTE);
        int mensajesContactos = contactoService.contarMensajesTotales();

        model.addAttribute("totalReservasMes", totalReservasMes);
        model.addAttribute("reservasPendientes", reservasPendientes);
        model.addAttribute("mensajesContactos", mensajesContactos);
        model.addAttribute("usuarioLogeado", usuario);
        return "admin/dashboard";
    }	

    
}