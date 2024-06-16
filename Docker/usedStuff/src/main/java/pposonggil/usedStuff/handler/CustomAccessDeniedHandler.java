package pposonggil.usedStuff.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

public class CustomAccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String message =accessDeniedException.getMessage();
        response.setStatus(Integer.parseInt(message));
        response.sendError(Integer.parseInt(message));
        if(message.contains("999"))
        {
            response.sendRedirect("https://pposong.ddns.net/blocked");
        }
    }
}
