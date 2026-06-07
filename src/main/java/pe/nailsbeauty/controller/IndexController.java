package pe.nailsbeauty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.nailsbeauty.service.ServicioService;

@Controller
public class IndexController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("servicios", servicioService.getAll());
        return "index";
    }
}
