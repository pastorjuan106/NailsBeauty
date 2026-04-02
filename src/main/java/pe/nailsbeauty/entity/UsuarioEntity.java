package pe.nailsbeauty.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity(name = "UsuarioEntity")
@Table(name = "usuario")
public class UsuarioEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "apellido_paterno", length = 100, nullable = false)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 100, nullable = false)
    private String apellidoMaterno;

    @Column(name = "correo", length = 100, nullable = false, unique = true)
    private String correo;

    @Column(name = "celular", length = 15)
    private String celular;

    @Column(name = "clave", length = 255, nullable = false)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    @Builder.Default
    private RolUsuario rol = RolUsuario.CLIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Column(name = "fecha_registro", nullable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public enum RolUsuario {
        CLIENTE, ADMIN
    }

    public enum EstadoUsuario {
        ACTIVO, INACTIVO
    }
}