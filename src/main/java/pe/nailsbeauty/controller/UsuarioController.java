package pe.nailsbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.service.UsuarioService;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String correo,
            @RequestParam String clave,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            UsuarioEntity user = service.findByCorreo(correo)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!passwordEncoder.matches(clave, user.getClave())) {
                redirectAttributes.addFlashAttribute("error", "Correo o contraseña incorrectos.");
                return "redirect:/login";
            }

            session.setAttribute("usuarioLogeado", user);
            redirectAttributes.addFlashAttribute("success", "¡Bienvenido " + user.getNombre() + "!");

            if (user.getRol() == UsuarioEntity.RolUsuario.ADMIN) {
                return "redirect:/admin/agenda/dashboard";
            } else {
                return "redirect:/index";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
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

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Sesión cerrada correctamente.");
        return "redirect:/login";
    }
    
    @GetMapping("/perfil")
    public String perfilPage(Model model, HttpSession session) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        model.addAttribute("usuarioLogeado", usuario);
        return "perfil"; 
    }
    
    @PostMapping("/perfil/guardar")
    public String actualizarPerfil(@ModelAttribute("usuario") UsuarioEntity usuarioForm,
                                   String claveActual,
                                   String nuevaClave,
                                   String confirmarClave,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        UsuarioEntity user = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        if (user == null) return "redirect:/login";

        try {
            UsuarioEntity actualizado = service.actualizarPerfil(
                user.getId(), usuarioForm, claveActual, nuevaClave, confirmarClave
            );
            session.setAttribute("usuarioLogeado", actualizado);
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/perfil";
    }
}
