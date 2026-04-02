package pe.nailsbeauty.service;

import java.util.List;
import pe.nailsbeauty.entity.ServicioEntity;

public interface ServicioService {
    List<ServicioEntity> getAll();
    ServicioEntity getById(Integer id);
    ServicioEntity add(ServicioEntity servicio);
    ServicioEntity update(Integer id, ServicioEntity servicio);
    void delete(Integer id);
}