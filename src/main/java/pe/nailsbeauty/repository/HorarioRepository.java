package pe.nailsbeauty.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.nailsbeauty.entity.HorarioEntity;

@Repository
public interface HorarioRepository extends JpaRepository<HorarioEntity, Integer> {
	List<HorarioEntity> findByEstado(HorarioEntity.EstadoHorario estado);
}