package pe.nailsbeauty.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.nailsbeauty.entity.ServicioEntity;
import pe.nailsbeauty.repository.ServicioRepository;
import pe.nailsbeauty.service.ServicioService;

@Service
public class ServicioServiceImpl implements ServicioService {

    @Autowired
    private ServicioRepository repository;

    @Override
    public List<ServicioEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public ServicioEntity getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
    }

    @Override
    public ServicioEntity add(ServicioEntity servicio) {
        return repository.save(servicio);
    }

    @Override
    public ServicioEntity update(Integer id, ServicioEntity servicio) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setNombreServicio(servicio.getNombreServicio());
                    existing.setDescripcion(servicio.getDescripcion());
                    existing.setDuracion(servicio.getDuracion());
                    existing.setPrecio(servicio.getPrecio());
                    existing.setImagenUrl(servicio.getImagenUrl());
                    existing.setEstado(servicio.getEstado());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}