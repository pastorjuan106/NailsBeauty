package pe.nailsbeauty.restcontroller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import pe.nailsbeauty.dto.Login;
import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*") 
public class UsuarioRestController {	

    @Autowired
    private UsuarioService service;

    // Obtener todos los usuarios
    @GetMapping
    public List<UsuarioEntity> getAll() {
        return service.getAll();
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public UsuarioEntity getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // Registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioEntity usuario) {
        try {
            UsuarioEntity newUser = service.add(usuario);
            return ResponseEntity.ok(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Actualizar datos del usuario (sin modificar contraseña)
    @PutMapping("/{id}")
    public UsuarioEntity update(@PathVariable Long id, @RequestBody UsuarioEntity usuario) {
        return service.update(id, usuario);
    }

    // Eliminar usuario por ID
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    
    @PostMapping("/api/usuarios/login")
    public ResponseEntity<?> login(@RequestBody Login request) {
        Optional<UsuarioEntity> userOpt = service.findByCorreo(request.getCorreo());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Correo o contraseña incorrectos");
        }

        UsuarioEntity user = userOpt.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(request.getClave(), user.getClave())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Correo o contraseña incorrectos");
        }

        return ResponseEntity.ok(user);
    }
}