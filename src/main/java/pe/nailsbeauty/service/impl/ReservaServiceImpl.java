package pe.nailsbeauty.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.nailsbeauty.entity.ReservaEntity;
import pe.nailsbeauty.entity.ReservaEntity.EstadoReserva;
import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.repository.ReservaRepository;
import pe.nailsbeauty.repository.UsuarioRepository;
import pe.nailsbeauty.service.ReservaService;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<ReservaEntity> getAll() {
        return reservaRepository.findAll();
    }

    @Override
    public ReservaEntity getById(Integer id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    @Override
    public ReservaEntity add(ReservaEntity reserva) {
        return reservaRepository.save(reserva);
    }

    @Override
    public ReservaEntity updateEstado(Integer id, ReservaEntity.EstadoReserva estado) {
        ReservaEntity reserva = getById(id);
        reserva.setEstado(estado);
        return reservaRepository.save(reserva);
    }

    @Override
    public void delete(Integer id) {
        reservaRepository.deleteById(id);
    }

    @Override
    public List<ReservaEntity> getByUsuario(Long idUsuario) {
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return reservaRepository.findByUsuarioOrderByFechaReservaDescHoraInicioAsc(usuario);
    }

    @Override
    public List<ReservaEntity> filtrarReservas(Long idUsuario, Integer idServicio, LocalDate fecha, Integer idHorario) {
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return reservaRepository.filtrarReservas(usuario, idServicio, fecha, idHorario);
    }

    @Override
    public List<ReservaEntity> getByFecha(LocalDate fecha) {
        return reservaRepository.findByFechaReserva(fecha);
    }
    

    @Override
    public int contarReservasMesActual() {
        return reservaRepository.contarReservasMesActual();
    }

    @Override
    public int contarReservasPorEstado(ReservaEntity.EstadoReserva estado) {
        return reservaRepository.countByEstado(estado);
    }
}
