package pe.nailsbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.service.UsuarioService;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("usuario") UsuarioEntity usuario,
                               RedirectAttributes redirectAttributes) {
        try {
            if (usuario.getRol() == null) {
                usuario.setRol(UsuarioEntity.RolUsuario.CLIENTE);
            }
            service.add(usuario);

            redirectAttributes.addFlashAttribute("success", "Registro exitoso, ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/perfil")
    public String perfilPage(Model model) {
        UsuarioEntity usuario = getUsuarioActual();
        model.addAttribute("usuarioLogeado", usuario);
        return "perfil";
    }

    @PostMapping("/perfil/guardar")
    public String actualizarPerfil(@ModelAttribute("usuario") UsuarioEntity usuarioForm,
                                   String claveActual,
                                   String nuevaClave,
                                   String confirmarClave,
                                   RedirectAttributes redirectAttributes) {
        UsuarioEntity user = getUsuarioActual();

        try {
            UsuarioEntity actualizado = service.actualizarPerfil(
                user.getId(), usuarioForm, claveActual, nuevaClave, confirmarClave
            );
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/perfil";
    }

    private UsuarioEntity getUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();
        return service.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
