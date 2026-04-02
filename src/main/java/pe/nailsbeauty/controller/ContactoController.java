package pe.nailsbeauty.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pe.nailsbeauty.entity.ContactoEntity;
import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.service.ContactoService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/contacto") 
public class ContactoController {

    private final ContactoService contactoService;
    
    @GetMapping
    public String getAll(Model model, HttpSession session) {
    	UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        List<ContactoEntity> contactos = contactoService.getAll();
        model.addAttribute("contactos", contactos);
        model.addAttribute("usuarioLogeado", usuario);
        return "admin/contactos/adminListaContactos";
    }

    @GetMapping("/form")
    public String mostrarFormularioContacto(Model model, HttpSession session) {
        UsuarioEntity user = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        if (user == null) return "redirect:/login";

        model.addAttribute("contacto", new ContactoEntity());
        model.addAttribute("usuarioLogeado", user);
        return "formContacto";
    }

    @PostMapping("/save")
    public String registrarContacto(@ModelAttribute("contacto") ContactoEntity contacto, 
                                    Model model, HttpSession session) {
        UsuarioEntity user = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        if (user == null) return "redirect:/login";

        contactoService.guardar(contacto);

        model.addAttribute("success", "Tu mensaje ha sido enviado correctamente. ¡Gracias por contactarnos!");
        model.addAttribute("contacto", new ContactoEntity());
        model.addAttribute("usuarioLogeado", user);
        return "formContacto";
    }

    @PostMapping("/eliminar/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            contactoService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Mensaje eliminado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/contacto";
    }
}