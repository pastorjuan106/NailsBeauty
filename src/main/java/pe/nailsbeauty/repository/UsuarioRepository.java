package pe.nailsbeauty.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.nailsbeauty.entity.UsuarioEntity;


public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByCorreo(String correo);

    @Query("SELECT u FROM UsuarioEntity u WHERE " +
           "(:busqueda IS NULL OR :busqueda = '' OR " +
           "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.apellidoPaterno) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.correo) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<UsuarioEntity> filtrarUsuarios(@Param("busqueda") String busqueda);
}