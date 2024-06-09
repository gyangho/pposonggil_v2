package pposonggil.usedStuff.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

public class CustomAccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(
                    "<script>" +
                        "alert('접근이 거부되었습니다.');" +
                        "window.location.href='http://localhost:3000/';" +
                        "</script>"
        );
    }
}
