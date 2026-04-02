package pe.nailsbeauty.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import pe.nailsbeauty.entity.HorarioEntity;
import pe.nailsbeauty.repository.HorarioRepository;
import pe.nailsbeauty.service.HorarioService;

@Service
public class HorarioServiceImpl implements HorarioService {

    @Autowired
    private HorarioRepository repo;

    @Override
    public List<HorarioEntity> getActivos() {
        return repo.findByEstado(HorarioEntity.EstadoHorario.ACTIVO);
    }
    
    @Override
    public HorarioEntity getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
    }
}