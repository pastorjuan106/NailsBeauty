package pe.nailsbeauty.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pe.nailsbeauty.entity.ServicioEntity;
import pe.nailsbeauty.service.ServicioService;

@RestController
@RequestMapping("/servicios")
@CrossOrigin(origins = "*")
public class ServicioRestController {

    @Autowired
    private ServicioService servicioService;

    // Obtener todos los servicios
    @GetMapping
    public List<ServicioEntity> getAll() {
        return servicioService.getAll();
    }

    // Obtener un servicio por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        ServicioEntity servicio = servicioService.getById(id);
        if (servicio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(servicio);
    }

    // Agregar un nuevo servicio (desde formulario HTML)
    @PostMapping
    public ResponseEntity<?> add(ServicioEntity servicio) {
        try {
            ServicioEntity newServicio = servicioService.add(servicio);
            return ResponseEntity.ok(newServicio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar un servicio
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, ServicioEntity servicio) {
        try {
            ServicioEntity updated = servicioService.update(id, servicio);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Eliminar un servicio
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            servicioService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
