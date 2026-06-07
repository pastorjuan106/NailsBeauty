package pe.nailsbeauty.service;

import pe.nailsbeauty.entity.EstadoFactura;
import pe.nailsbeauty.entity.FacturaEntity;
import pe.nailsbeauty.entity.MetodoPago;

import java.math.BigDecimal;
import java.util.List;

public interface FacturaService {

    List<FacturaEntity> getAll();

    FacturaEntity getById(Integer id);

    FacturaEntity getByReservaId(Integer idReserva);

    FacturaEntity crearDesdeReserva(Integer idReserva);

    FacturaEntity save(FacturaEntity factura);

    FacturaEntity actualizar(Integer id, BigDecimal descuento, MetodoPago metodoPago, String observaciones);

    void actualizarEstado(Integer id, EstadoFactura estado);

    void delete(Integer id);

    String generarNumeroComprobante();

    BigDecimal calcularIgv(BigDecimal precio);

    BigDecimal calcularTotal(BigDecimal precio, BigDecimal descuento, BigDecimal igv);

    List<FacturaEntity> filtrar(String busqueda);

    long contarFacturasPagadas();

    BigDecimal sumarTotalFacturasPagadas();

    boolean existeFacturaPorReserva(Integer idReserva);
}
