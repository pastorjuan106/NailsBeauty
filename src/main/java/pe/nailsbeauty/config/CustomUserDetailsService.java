package pe.nailsbeauty.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pe.nailsbeauty.entity.UsuarioEntity;
import pe.nailsbeauty.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        String rol = "ROLE_" + usuario.getRol().name();
        boolean activo = usuario.getEstado() == UsuarioEntity.EstadoUsuario.ACTIVO;

        return new User(
                usuario.getCorreo(),
                usuario.getClave(),
                activo,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(rol))
        );
    }
}
