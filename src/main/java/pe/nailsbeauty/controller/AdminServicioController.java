package pe.nailsbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.nailsbeauty.entity.ServicioEntity;
import pe.nailsbeauty.service.ServicioService;

import java.util.List;

@Controller
@RequestMapping("/admin/servicios")
public class AdminServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public String getAll(Model model) {
        List<ServicioEntity> servicios = servicioService.getAll();
        model.addAttribute("servicios", servicios);
        return "admin/servicios/lista";
    }

    @GetMapping("/form")
    public String register(Model model) {
        model.addAttribute("servicio", new ServicioEntity());
        return "admin/servicios/form";
    }

    @GetMapping("/form/{id}")
    public String update(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ServicioEntity servicio = servicioService.getById(id);
            model.addAttribute("servicio", servicio);
            return "admin/servicios/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Servicio no encontrado");
            return "redirect:/admin/servicios";
        }
    }

    @PostMapping("/guardar")
    public String save(@ModelAttribute("servicio") ServicioEntity servicio,
                       RedirectAttributes redirectAttributes) {
        try {
            if (servicio.getId() == null) {
                servicioService.add(servicio);
                redirectAttributes.addFlashAttribute("success", "Servicio agregado correctamente");
            } else {
                servicioService.update(servicio.getId(), servicio);
                redirectAttributes.addFlashAttribute("success", "Servicio actualizado correctamente");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/servicios";
    }

    @PostMapping("/eliminar/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            servicioService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Servicio eliminado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/servicios";
    }
}
