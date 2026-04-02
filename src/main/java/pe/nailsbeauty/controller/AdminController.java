package pe.nailsbeauty.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pe.nailsbeauty.entity.UsuarioEntity;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");

        if (usuario == null || usuario.getRol() != UsuarioEntity.RolUsuario.ADMIN) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para acceder a esta página.");
            return "redirect:/login";
        }
        model.addAttribute("usuarioLogeado", usuario);
        return "admin/dashboard";
    }
}
