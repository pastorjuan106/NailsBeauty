package pe.nailsbeauty.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.nailsbeauty.entity.ReservaEntity;
import pe.nailsbeauty.entity.ReservaEntity.EstadoReserva;
import pe.nailsbeauty.entity.UsuarioEntity;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Integer> {

    // Ordenamos por fecha y por el inicio del horario
    @Query("SELECT r FROM ReservaEntity r WHERE r.usuario = :usuario ORDER BY r.fechaReserva DESC, r.horario.horaInicio ASC")
    List<ReservaEntity> findByUsuarioOrderByFechaReservaDescHoraInicioAsc(@Param("usuario") UsuarioEntity usuario);

    // Reservas por fecha
    @Query("SELECT r FROM ReservaEntity r WHERE r.fechaReserva = :fecha")
    List<ReservaEntity> findByFechaReserva(@Param("fecha") LocalDate fecha);

    // Filtro dinámico (servicio, fecha, horario)
    @Query("SELECT r FROM ReservaEntity r " +
           "WHERE r.usuario = :usuario " +
           "AND (:idServicio IS NULL OR r.servicio.id = :idServicio) " +
           "AND (:fechaReserva IS NULL OR r.fechaReserva = :fechaReserva) " +
           "AND (:idHorario IS NULL OR r.horario.id = :idHorario) " +
           "ORDER BY r.fechaReserva DESC, r.horario.horaInicio ASC")
    List<ReservaEntity> filtrarReservas(@Param("usuario") UsuarioEntity usuario,
                                        @Param("idServicio") Integer idServicio,
                                        @Param("fechaReserva") LocalDate fechaReserva,
                                        @Param("idHorario") Integer idHorario);
    
 //  // Contar reservas del mes actual
    @Query("SELECT COUNT(r) FROM ReservaEntity r WHERE MONTH(r.fechaReserva) = MONTH(CURRENT_DATE) AND YEAR(r.fechaReserva) = YEAR(CURRENT_DATE)")
    int contarReservasMesActual();

    // Contar reservas por estado
    int countByEstado(ReservaEntity.EstadoReserva estado);
}