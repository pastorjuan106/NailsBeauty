package pe.nailsbeauty.service;

import java.util.List;
import java.util.Optional;

import pe.nailsbeauty.entity.UsuarioEntity;

public interface UsuarioService {
    List<UsuarioEntity> getAll();
    UsuarioEntity getById(Long id);
    UsuarioEntity add(UsuarioEntity usuario);
    UsuarioEntity update(Long id, UsuarioEntity usuario);
    void delete(Long id);
    Optional<UsuarioEntity> findByCorreo(String correo);
    UsuarioEntity validarLogin(String correo, String clave);
    UsuarioEntity actualizarPerfil(Long id, UsuarioEntity usuarioForm, 
            String claveActual, String nuevaClave, String confirmarClave);
}