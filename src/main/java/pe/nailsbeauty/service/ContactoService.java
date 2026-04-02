 package pe.nailsbeauty.service;


 import java.util.List;

import pe.nailsbeauty.entity.ContactoEntity;

 public interface ContactoService {
     ContactoEntity guardar(ContactoEntity contacto);
     List<ContactoEntity> getAll();
     int contarMensajesTotales();
     void delete(Integer id);

 }