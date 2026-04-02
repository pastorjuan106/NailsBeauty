package pe.nailsbeauty.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pe.nailsbeauty.entity.ReservaEntity;
import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.service.ReservaService;

@Controller
@RequestMapping("/admin/reservas")
public class AdminReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public String getAll(Model model,  HttpSession session) {
    	UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        List<ReservaEntity> reservas = reservaService.getAll();
        model.addAttribute("reservas", reservas);
        model.addAttribute("estadosReserva", ReservaEntity.EstadoReserva.values());
        model.addAttribute("usuarioLogeado", usuario);

        return "admin/reservas/adminListaReservas";
    }

    @PostMapping("/guardar")
    public String guardarEstado(@RequestParam("id") Integer id,
                                @RequestParam("estado") ReservaEntity.EstadoReserva estado,
                                RedirectAttributes redirectAttributes) {
        try {
            reservaService.updateEstado(id, estado);
            redirectAttributes.addFlashAttribute("success", "Estado actualizado correctamente");
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
}	