package pe.nailsbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.nailsbeauty.entity.ServicioEntity;

@Repository
public interface ServicioRepository extends JpaRepository<ServicioEntity, Integer> {
}