package pe.nailsbeauty.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pe.nailsbeauty.entity.HorarioEntity;
import pe.nailsbeauty.entity.ReservaEntity;
import pe.nailsbeauty.entity.ServicioEntity;
import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.service.HorarioService;
import pe.nailsbeauty.service.ReservaService;
import pe.nailsbeauty.service.ServicioService;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public String getAll(
            HttpSession session,
            Model model,
            @RequestParam(required = false) Integer servicio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio
    ) {
        UsuarioEntity user = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        if (user == null) return "redirect:/login";

        List<ReservaEntity> reservas = reservaService.getByUsuario(user.getId());

        if (servicio != null) {
            reservas = reservas.stream()
                    .filter(r -> r.getServicio().getId().equals(servicio))
                    .toList();
        }
        if (mes != null) {
            reservas = reservas.stream()
                    .filter(r -> r.getFechaReserva().getMonthValue() == mes)
                    .toList();
        }
        if (anio != null) {
            reservas = reservas.stream()
                    .filter(r -> r.getFechaReserva().getYear() == anio)
                    .toList();
        }

        List<ServicioEntity> services = servicioService.getAll();
        int currentYear = LocalDate.now().getYear();

        Map<Integer, String> meses = Map.ofEntries(
        	    Map.entry(1, "Enero"),
        	    Map.entry(2, "Febrero"),
        	    Map.entry(3, "Marzo"),
        	    Map.entry(4, "Abril"),
        	    Map.entry(5, "Mayo"),
        	    Map.entry(6, "Junio"),
        	    Map.entry(7, "Julio"),
        	    Map.entry(8, "Agosto"),
        	    Map.entry(9, "Septiembre"),
        	    Map.entry(10, "Octubre"),
        	    Map.entry(11, "Noviembre"),
        	    Map.entry(12, "Diciembre")
        	);

        model.addAttribute("reservas", reservas);
        model.addAttribute("services", services);
        model.addAttribute("currentYear", currentYear);
        model.addAttribute("meses", meses); 

        model.addAttribute("filtroServicio", servicio);
        model.addAttribute("filtroMes", mes);
        model.addAttribute("filtroAnio", anio);
        model.addAttribute("usuarioLogeado", user);

        return "reservas/listaReservas";
    }



    @GetMapping("/form")
    public String getForm(@RequestParam(name = "idServicio", required = false) Integer idServicio,
                          Model model, HttpSession session) {

        UsuarioEntity user = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        if (user == null) return "redirect:/login";

        ReservaEntity reservation = new ReservaEntity();
        reservation.setUsuario(user);
        reservation.setHorario(new HorarioEntity());

        
        if (idServicio != null) {
            ServicioEntity servicioSeleccionado = servicioService.getById(idServicio);
            reservation.setServicio(servicioSeleccionado);
        }

        List<ServicioEntity> services = servicioService.getAll();
        List<HorarioEntity> horarios = horarioService.getActivos();

        model.addAttribute("reserva", reservation);
        model.addAttribute("services", services);
        model.addAttribute("horarios", horarios);
        model.addAttribute("usuarioLogeado", user);

        return "reservas/formReservas";
    }
    
    @GetMapping("/horarios/disponibles")
    @ResponseBody
    public List<Map<String, Object>> getHorariosDisponibles(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<HorarioEntity> horariosActivos = horarioService.getActivos();
        List<ReservaEntity> reservasEnFecha = reservaService.getByFecha(fecha);

        // Extrae los IDs de los horarios ocupados
        Set<Integer> horariosOcupados = reservasEnFecha.stream()
                .map(r -> r.getHorario().getId())
                .collect(Collectors.toSet());

        // Creamos una lista con el estado de cada horario
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (HorarioEntity h : horariosActivos) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", h.getId());
            item.put("horaInicio", h.getHoraInicio().toString());
            item.put("horaFin", h.getHoraFin().toString());
            item.put("ocupado", horariosOcupados.contains(h.getId())); 
            resultado.add(item);
        }

        return resultado;
    }
    
    @Autowired
    private HorarioService horarioService;


    @PostMapping("/save")
    public String add(@ModelAttribute("reserva") ReservaEntity reserva,
                      HttpSession session,
                      RedirectAttributes redirectAttributes) {

        UsuarioEntity user = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        if (user == null) return "redirect:/login";

        try {
            if (reserva.getFechaReserva().isBefore(LocalDate.now())) {
                throw new RuntimeException("No se puede reservar una fecha pasada");
            }

            reserva.setUsuario(user);
            reserva.setServicio(servicioService.getById(reserva.getServicio().getId()));
            reserva.setHorario(horarioService.getById(reserva.getHorario().getId()));
            reservaService.add(reserva);

            redirectAttributes.addFlashAttribute("success", "Reserva creada correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/reservas";
    }

    
    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable Integer id, HttpSession session,
                         RedirectAttributes redirectAttributes) {
        UsuarioEntity user = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        if (user == null) return "redirect:/login";

        try {
            ReservaEntity reserva = reservaService.getById(id);
            if (!reserva.getUsuario().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para cancelar esta reserva");
                return "redirect:/reservas";
            }

            reservaService.updateEstado(id, ReservaEntity.EstadoReserva.CANCELADA);
            redirectAttributes.addFlashAttribute("success", "Reserva cancelada correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservas";
    }
}
