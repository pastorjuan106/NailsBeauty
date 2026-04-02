package pe.nailsbeauty.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import pe.nailsbeauty.entity.UsuarioEntity;


public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        HttpSession session = request.getSession(false);

        if (session != null) {
            UsuarioEntity usuario = (UsuarioEntity) session.getAttribute("usuarioLogeado");
            
            if (usuario != null && usuario.getRol() == UsuarioEntity.RolUsuario.ADMIN) {
                return true; 
            }
        }

        response.sendRedirect("/login");
        return false;
    }
}