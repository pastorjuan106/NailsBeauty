package pe.nailsbeauty.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pe.nailsbeauty.entity.ContactoEntity;

@Repository
public interface ContactoRepository extends JpaRepository<ContactoEntity, Integer> {

    @Query("SELECT COUNT(c) FROM ContactoEntity c")
    int contarMensajesTotales();
}