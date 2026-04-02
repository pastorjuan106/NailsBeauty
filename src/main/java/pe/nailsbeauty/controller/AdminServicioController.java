package pe.nailsbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pe.nailsbeauty.entity.ServicioEntity;
import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.service.ServicioService;

import java.util.List;

@Controller
@RequestMapping("/admin/servicios")
public class AdminServicioController {

    @Autowired
    private ServicioService servicioService;

    // Lista de servicios
    @GetMapping
    public String getAll(Model model, HttpSession session) {
    	UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        List<ServicioEntity> servicios = servicioService.getAll();
        model.addAttribute("servicios", servicios);
        model.addAttribute("usuarioLogeado", usuario);
        return  "admin/servicios/lista";
    }

    // Formulario para crear un nuevo servicio
    @GetMapping("/form")
    public String register(Model model, HttpSession session) {
    	UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        model.addAttribute("servicio", new ServicioEntity());
        model.addAttribute("usuarioLogeado", usuario);
        return  "admin/servicios/form";
    }

    // Formulario para editar un servicio existente
    @GetMapping("/form/{id}")
    public String update(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
    	UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        try {
            ServicioEntity servicio = servicioService.getById(id);
            model.addAttribute("servicio", servicio);
            model.addAttribute("usuarioLogeado", usuario);
            
            return "admin/servicios/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Servicio no encontrado");
            return "redirect:/admin/servicios";
        }
    }

    // Guardar un servicio nuevo o actualizado
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

    // Eliminar un servicio
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
