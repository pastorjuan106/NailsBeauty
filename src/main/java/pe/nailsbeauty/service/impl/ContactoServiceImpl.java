package pe.nailsbeauty.service.impl;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.nailsbeauty.entity.ContactoEntity;
import pe.nailsbeauty.repository.ContactoRepository;
import pe.nailsbeauty.service.ContactoService;

@Service
@RequiredArgsConstructor
public class ContactoServiceImpl implements ContactoService {

    private final ContactoRepository contactoRepository;

    @Override
    public ContactoEntity guardar(ContactoEntity contacto) {
        return contactoRepository.save(contacto);
    }

    @Override
    public List<ContactoEntity> getAll(){
        return contactoRepository.findAll();
    }
    
    @Override
    public int contarMensajesTotales() {
        return contactoRepository.contarMensajesTotales();
    }
    
    @Override
    public void delete(Integer id) {
        if (!contactoRepository.existsById(id)) {
            throw new RuntimeException("Mensaje no encontrado con ID: " + id);
        }
        contactoRepository.deleteById(id);
    }
}