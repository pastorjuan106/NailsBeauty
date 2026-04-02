package pe.nailsbeauty.service;

import java.time.LocalDate;
import java.util.List;

import pe.nailsbeauty.entity.ReservaEntity;
import pe.nailsbeauty.entity.ReservaEntity.EstadoReserva;

public interface ReservaService {

    List<ReservaEntity> getAll();
    ReservaEntity getById(Integer id);
    ReservaEntity add(ReservaEntity reserva);
    ReservaEntity updateEstado(Integer id, ReservaEntity.EstadoReserva estado);
    void delete(Integer id);
    List<ReservaEntity> getByUsuario(Long idUsuario);
    List<ReservaEntity> filtrarReservas(Long idUsuario, Integer idServicio, LocalDate fecha, Integer idHorario);
    List<ReservaEntity> getByFecha(LocalDate fecha);
    int contarReservasMesActual();
    int contarReservasPorEstado(ReservaEntity.EstadoReserva estado);

    
}