package pe.nailsbeauty.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity(name = "ReservaEntity")
@Table(name = "reserva")
public class ReservaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    private ServicioEntity servicio;

    @ManyToOne
    @JoinColumn(name = "id_horario", nullable = false)
    private HorarioEntity horario;   

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDate fechaReserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(name = "observacion", length = 250)
    private String observacion;

    @Column(name = "fecha_registro", nullable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public enum EstadoReserva {
        PENDIENTE,
        ATENDIDA,
        CANCELADA
    }
}