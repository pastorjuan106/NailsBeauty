package pe.nailsbeauty.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.service.ServicioService;

@Controller
public class IndexController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping({"/", "/index"})
    public String index(Model model, HttpSession session) {
        // Cargar servicios
        model.addAttribute("servicios", servicioService.getAll());

        // Exponer usuario logueado desde la sesión
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        model.addAttribute("usuarioLogeado", usuario);

        return "index";
    }
}