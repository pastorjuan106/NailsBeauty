package pe.nailsbeauty.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.nailsbeauty.entity.UsuarioEntity;


public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByCorreo(String correo);
}