package pe.nailsbeauty.service;

import java.util.List;
import pe.nailsbeauty.entity.HorarioEntity;

public interface HorarioService {
    List<HorarioEntity> getActivos();
    HorarioEntity getById(Integer id);
}