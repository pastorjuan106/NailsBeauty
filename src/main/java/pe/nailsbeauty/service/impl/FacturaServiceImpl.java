package pe.nailsbeauty.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.nailsbeauty.entity.*;
import pe.nailsbeauty.repository.FacturaRepository;
import pe.nailsbeauty.service.FacturaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepository;

    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");

    @Override
    public List<FacturaEntity> getAll() {
        return facturaRepository.findByOrderByFechaEmisionDesc();
    }

    @Override
    public FacturaEntity getById(Integer id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
    }

    @Override
    public FacturaEntity getByReservaId(Integer idReserva) {
        return facturaRepository.findByReservaId(idReserva)
                .orElseThrow(() -> new RuntimeException("No existe factura para la reserva ID: " + idReserva));
    }

    @Override
    @Transactional
    public FacturaEntity crearDesdeReserva(Integer idReserva) {
        if (facturaRepository.existsByReservaId(idReserva)) {
            throw new RuntimeException("Ya existe una factura para la reserva ID: " + idReserva);
        }

        ReservaEntity reserva = new ReservaEntity();
        reserva.setId(idReserva);

        FacturaEntity factura = FacturaEntity.builder()
                .numeroComprobante(generarNumeroComprobante())
                .reserva(reserva)
                .nombreComercial("Nails Beauty")
                .ruc("20512684759")
                .direccion("Av. Ejemplo 123, Lima")
                .build();

        return facturaRepository.save(factura);
    }

    @Override
    @Transactional
    public FacturaEntity save(FacturaEntity factura) {
        if (factura.getNumeroComprobante() == null) {
            factura.setNumeroComprobante(generarNumeroComprobante());
        }
        if (factura.getNombreComercial() == null) {
            factura.setNombreComercial("Nails Beauty");
        }
        if (factura.getRuc() == null) {
            factura.setRuc("20512684759");
        }
        if (factura.getDireccion() == null) {
            factura.setDireccion("Av. Ejemplo 123, Lima");
        }
        return facturaRepository.save(factura);
    }

    @Override
    @Transactional
    public FacturaEntity actualizar(Integer id, BigDecimal descuento, MetodoPago metodoPago, String observaciones) {
        FacturaEntity factura = getById(id);

        if (descuento != null) {
            factura.setDescuento(descuento);
        }
        if (metodoPago != null) {
            factura.setMetodoPago(metodoPago);
        }
        if (observaciones != null) {
            factura.setObservaciones(observaciones);
        }

        BigDecimal igv = calcularIgv(factura.getPrecioServicio());
        BigDecimal total = calcularTotal(factura.getPrecioServicio(), factura.getDescuento(), igv);
        factura.setIgv(igv);
        factura.setTotalPagar(total);

        return facturaRepository.save(factura);
    }

    @Override
    @Transactional
    public void actualizarEstado(Integer id, EstadoFactura estado) {
        FacturaEntity factura = getById(id);
        factura.setEstado(estado);
        facturaRepository.save(factura);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (facturaRepository.existsById(id)) {
            facturaRepository.deleteById(id);
        }
    }

    @Override
    public String generarNumeroComprobante() {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long total = facturaRepository.count();
        return String.format("FAC-%s-%05d", fecha, total + 1);
    }

    @Override
    public BigDecimal calcularIgv(BigDecimal precio) {
        if (precio == null) return BigDecimal.ZERO;
        return precio.multiply(IGV_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calcularTotal(BigDecimal precio, BigDecimal descuento, BigDecimal igv) {
        if (precio == null) precio = BigDecimal.ZERO;
        if (descuento == null) descuento = BigDecimal.ZERO;
        if (igv == null) igv = BigDecimal.ZERO;
        return precio.subtract(descuento).add(igv).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<FacturaEntity> filtrar(String busqueda) {
        return facturaRepository.filtrarFacturas(busqueda);
    }

    @Override
    public long contarFacturasPagadas() {
        return facturaRepository.contarFacturasPagadas();
    }

    @Override
    public BigDecimal sumarTotalFacturasPagadas() {
        return facturaRepository.sumarTotalFacturasPagadas();
    }

    @Override
    public boolean existeFacturaPorReserva(Integer idReserva) {
        return facturaRepository.existsByReservaId(idReserva);
    }
}
