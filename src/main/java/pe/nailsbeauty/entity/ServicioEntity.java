package pe.nailsbeauty.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity(name = "ServicioEntity")
@Table(name = "servicio")
public class ServicioEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer id;

    @Column(name = "nombre_servicio", length = 100, nullable = false)
    private String nombreServicio;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "duracion")
    private Integer duracion; 

    @Column(name = "precio", precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoServicio estado = EstadoServicio.DISPONIBLE;

    public enum EstadoServicio {
        DISPONIBLE("Disponible"),
        NO_DISPONIBLE("No disponible");

        private final String valor;

        EstadoServicio(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }
    }
}