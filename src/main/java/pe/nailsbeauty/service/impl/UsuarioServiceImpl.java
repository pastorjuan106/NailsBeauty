package pe.nailsbeauty.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.repository.UsuarioRepository;
import pe.nailsbeauty.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public List<UsuarioEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public UsuarioEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public UsuarioEntity add(UsuarioEntity usuario) {
        if (repository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        usuario.setClave(passwordEncoder.encode(usuario.getClave())); 
        return repository.save(usuario);
    }

    @Override
    public UsuarioEntity update(Long id, UsuarioEntity usuario) {
        return repository.findById(id)
                .map(existingUser -> {
                    existingUser.setNombre(usuario.getNombre());
                    existingUser.setApellidoPaterno(usuario.getApellidoPaterno());
                    existingUser.setApellidoMaterno(usuario.getApellidoMaterno());
                    existingUser.setCorreo(usuario.getCorreo());
                    existingUser.setCelular(usuario.getCelular());
                    existingUser.setEstado(usuario.getEstado());
                    existingUser.setRol(usuario.getRol());
                    
                    return repository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
    
    @Override
    public Optional<UsuarioEntity> findByCorreo(String correo) {
        return repository.findByCorreo(correo);
    }
    
    @Override
    public UsuarioEntity validarLogin(String correo, String clave) {
        Optional<UsuarioEntity> optUsuario = repository.findByCorreo(correo);

        if (optUsuario.isPresent()) {
            UsuarioEntity usuario = optUsuario.get();

            if (new BCryptPasswordEncoder().matches(clave, usuario.getClave())) {
                return usuario;
            }

            throw new RuntimeException("Contraseña incorrecta");
        }

        throw new RuntimeException("No existe un usuario con ese correo");
    }
    
    @Override
    public UsuarioEntity actualizarPerfil(Long id, UsuarioEntity usuarioForm, 
                                          String claveActual, String nuevaClave, String confirmarClave) {
        return repository.findById(id).map(usuario -> {

            // Actualizar datos básicos
            usuario.setNombre(usuarioForm.getNombre());
            usuario.setApellidoPaterno(usuarioForm.getApellidoPaterno());
            usuario.setApellidoMaterno(usuarioForm.getApellidoMaterno());
            usuario.setCelular(usuarioForm.getCelular());

            if (claveActual != null && !claveActual.isEmpty()) {
                if (!passwordEncoder.matches(claveActual, usuario.getClave())) {
                    throw new RuntimeException("La contraseña actual es incorrecta.");
                }
                if (!nuevaClave.equals(confirmarClave)) {
                    throw new RuntimeException("La nueva contraseña y su confirmación no coinciden.");
                }
                usuario.setClave(passwordEncoder.encode(nuevaClave));
            }

            return repository.save(usuario);

        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }


}