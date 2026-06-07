package pe.nailsbeauty.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.service.UsuarioService;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String getAll(Model model, HttpSession session,
                        @RequestParam(value = "busqueda", required = false) String busqueda) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        List<UsuarioEntity> usuarios;

        if (busqueda != null && !busqueda.trim().isEmpty()) {
            usuarios = usuarioService.filtrar(busqueda);
            model.addAttribute("busqueda", busqueda);
        } else {
            usuarios = usuarioService.getAll();
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuarioLogeado", usuario);
        return "admin/usuarios/lista";
    }

    @GetMapping("/form/{id}")
    public String editForm(@PathVariable Long id, Model model,
                           RedirectAttributes redirectAttributes, HttpSession session) {
        UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");
        try {
            UsuarioEntity usuarioEdit = usuarioService.getById(id);
            model.addAttribute("usuarioEdit", usuarioEdit);
            model.addAttribute("roles", UsuarioEntity.RolUsuario.values());
            model.addAttribute("estados", UsuarioEntity.EstadoUsuario.values());
            model.addAttribute("usuarioLogeado", usuario);
            return "admin/usuarios/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam("id") Long id,
                          @RequestParam("rol") UsuarioEntity.RolUsuario rol,
                          @RequestParam("estado") UsuarioEntity.EstadoUsuario estado,
                          RedirectAttributes redirectAttributes) {
        try {
            usuarioService.actualizarRolYEstado(id, rol, estado);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/exportar-csv")
    public ResponseEntity<byte[]> exportarCsv() {
        List<UsuarioEntity> usuarios = usuarioService.getAll();

        String BOM = "\uFEFF";
        StringBuilder csv = new StringBuilder(BOM);
        csv.append("ID;Nombre;Apellido Paterno;Apellido Materno;Correo;Celular;Rol;Estado;Fecha Registro\n");

        for (UsuarioEntity u : usuarios) {
            csv.append(String.format("%d;\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"\n",
                    u.getId(),
                    escape(u.getNombre()),
                    escape(u.getApellidoPaterno()),
                    escape(u.getApellidoMaterno()),
                    escape(u.getCorreo()),
                    escape(u.getCelular() != null ? u.getCelular() : ""),
                    u.getRol() != null ? u.getRol().name() : "",
                    u.getEstado() != null ? u.getEstado().name() : "",
                    u.getFechaRegistro() != null ? u.getFechaRegistro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""));
        }

        byte[] utf8 = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usuarios.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(utf8);
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
