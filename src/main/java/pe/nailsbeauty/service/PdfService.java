package pe.nailsbeauty.service;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;
import pe.nailsbeauty.entity.FacturaEntity;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final DeviceRgb PINK = new DeviceRgb(214, 51, 132);
    private static final DeviceRgb LIGHT_PINK = new DeviceRgb(255, 247, 250);
    private static final DeviceRgb GOLD = new DeviceRgb(212, 175, 55);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarFacturaPdf(FacturaEntity factura) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(20, 30, 20, 30);

        document.add(crearEncabezado(factura));
        document.add(new Paragraph("\n"));
        document.add(crearInfoComprobante(factura));
        document.add(new Paragraph("\n"));
        document.add(crearInfoCliente(factura));
        document.add(new Paragraph("\n"));
        document.add(crearTablaDetalle(factura));
        document.add(new Paragraph("\n"));
        document.add(crearResumen(factura));
        document.add(new Paragraph("\n"));
        document.add(crearPiePago(factura));
        document.add(new Paragraph("\n"));
        document.add(crearPiePagina());

        document.close();
        return baos.toByteArray();
    }

    private Table crearEncabezado(FacturaEntity factura) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .useAllAvailableWidth();

        Cell leftCell = new Cell()
                .add(new Paragraph("NAILS BEAUTY")
                        .setFontColor(PINK)
                        .setFontSize(22)
                        .setBold())
                .add(new Paragraph(factura.getDireccion())
                        .setFontSize(9)
                        .setFontColor(ColorConstants.DARK_GRAY))
                .add(new Paragraph("RUC: " + factura.getRuc())
                        .setFontSize(9)
                        .setFontColor(ColorConstants.DARK_GRAY))
                .add(new Paragraph("Tel: (01) 123-4567")
                        .setFontSize(9)
                        .setFontColor(ColorConstants.DARK_GRAY))
                .setBorder(new SolidBorder(PINK, 1))
                .setBackgroundColor(LIGHT_PINK)
                .setPadding(10);

        Cell rightCell = new Cell()
                .add(new Paragraph("BOLETA DE VENTA")
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(PINK)
                        .setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("N° " + factura.getNumeroComprobante())
                        .setFontSize(11)
                        .setBold()
                        .setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("Fecha: " + factura.getFechaEmision().format(DATE_FORMAT))
                        .setFontSize(9)
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(new SolidBorder(PINK, 1))
                .setBackgroundColor(LIGHT_PINK)
                .setPadding(10);

        table.addCell(leftCell);
        table.addCell(rightCell);
        return table;
    }

    private Table crearInfoComprobante(FacturaEntity factura) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .useAllAvailableWidth();

        table.addCell(new Cell()
                .add(new Paragraph("ESTADO: " + factura.getEstado().getValor())
                        .setFontSize(10)
                        .setBold()
                        .setFontColor(factura.getEstado() == pe.nailsbeauty.entity.EstadoFactura.PAGADA
                                ? new DeviceRgb(0, 128, 0) : GOLD))
                .setBorder(null)
                .setPadding(5));

        table.addCell(new Cell()
                .add(new Paragraph("Método de Pago: " + factura.getMetodoPago().getValor())
                        .setFontSize(10)
                        .setBold())
                .setBorder(null)
                .setPadding(5)
                .setTextAlignment(TextAlignment.RIGHT));

        return table;
    }

    private Table crearInfoCliente(FacturaEntity factura) {
        Table table = new Table(1).useAllAvailableWidth();

        Cell headerCell = new Cell()
                .add(new Paragraph("DATOS DEL CLIENTE")
                        .setFontSize(11)
                        .setBold()
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(PINK)
                .setPadding(8);

        table.addCell(headerCell);

        Cell contentCell = new Cell()
                .add(new Paragraph("Cliente: " + factura.getClienteNombre()).setFontSize(10))
                .add(new Paragraph("Correo: " + factura.getClienteCorreo()).setFontSize(10))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setPadding(10);

        table.addCell(contentCell);
        return table;
    }

    private Table crearTablaDetalle(FacturaEntity factura) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{40, 20, 20, 20}))
                .useAllAvailableWidth();

        String[] headers = {"Servicio", "Precio", "Descuento", "Subtotal"};
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setFontSize(9).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(PINK)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(6));
        }

        table.addCell(crearCelda(factura.getServicioNombre(), TextAlignment.LEFT));
        table.addCell(crearCelda("S/. " + factura.getPrecioServicio(), TextAlignment.CENTER));
        table.addCell(crearCelda("S/. " + factura.getDescuento(), TextAlignment.CENTER));
        table.addCell(crearCelda("S/. " + factura.getPrecioServicio().subtract(factura.getDescuento()), TextAlignment.CENTER));

        return table;
    }

    private Cell crearCelda(String texto, TextAlignment alignment) {
        return new Cell()
                .add(new Paragraph(texto).setFontSize(10))
                .setTextAlignment(alignment)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setPadding(6);
    }

    private Table crearResumen(FacturaEntity factura) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .useAllAvailableWidth();

        BigDecimal subtotal = factura.getPrecioServicio().subtract(factura.getDescuento());

        addResumenRow(table, "Subtotal:", "S/. " + subtotal, TextAlignment.RIGHT);
        addResumenRow(table, "IGV (18%):", "S/. " + factura.getIgv(), TextAlignment.RIGHT);

        Cell totalLabel = new Cell()
                .add(new Paragraph("TOTAL A PAGAR:").setFontSize(12).setBold().setFontColor(PINK))
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(5);

        Cell totalValue = new Cell()
                .add(new Paragraph("S/. " + factura.getTotalPagar()).setFontSize(14).setBold().setFontColor(PINK))
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(5);

        table.addCell(totalLabel);
        table.addCell(totalValue);

        return table;
    }

    private void addResumenRow(Table table, String label, String value, TextAlignment alignment) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setFontSize(10))
                .setBorder(null)
                .setTextAlignment(alignment)
                .setPadding(3));

        table.addCell(new Cell()
                .add(new Paragraph(value).setFontSize(10))
                .setBorder(null)
                .setTextAlignment(alignment)
                .setPadding(3));
    }

    private Table crearPiePago(FacturaEntity factura) {
        Table table = new Table(1).useAllAvailableWidth();

        Cell cell = new Cell()
                .add(new Paragraph("Método de pago: " + factura.getMetodoPago().getValor())
                        .setFontSize(10))
                .setBorder(null)
                .setPadding(5);

        if (factura.getObservaciones() != null && !factura.getObservaciones().isEmpty()) {
            cell.add(new Paragraph("Observaciones: " + factura.getObservaciones())
                    .setFontSize(9)
                    .setFontColor(ColorConstants.DARK_GRAY));
        }

        table.addCell(cell);
        return table;
    }

    private Paragraph crearPiePagina() {
        return new Paragraph("¡Gracias por su preferencia!")
                .setFontSize(11)
                .setBold()
                .setFontColor(PINK)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);
    }
}
