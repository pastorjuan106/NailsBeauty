package pe.nailsbeauty.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.nailsbeauty.entity.FacturaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<FacturaEntity, Integer> {

    Optional<FacturaEntity> findByNumeroComprobante(String numeroComprobante);

    Optional<FacturaEntity> findByReservaId(Integer idReserva);

    boolean existsByReservaId(Integer idReserva);

    List<FacturaEntity> findByOrderByFechaEmisionDesc();

    @Query("SELECT f FROM FacturaEntity f WHERE " +
           "(:busqueda IS NULL OR :busqueda = '' OR " +
           "LOWER(f.numeroComprobante) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(f.clienteNombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(f.clienteCorreo) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<FacturaEntity> filtrarFacturas(@Param("busqueda") String busqueda);

    @Query("SELECT COUNT(f) FROM FacturaEntity f WHERE f.estado = pe.nailsbeauty.entity.EstadoFactura.PAGADA")
    long contarFacturasPagadas();

    @Query("SELECT COALESCE(SUM(f.totalPagar), 0) FROM FacturaEntity f WHERE f.estado = pe.nailsbeauty.entity.EstadoFactura.PAGADA")
    java.math.BigDecimal sumarTotalFacturasPagadas();
}
