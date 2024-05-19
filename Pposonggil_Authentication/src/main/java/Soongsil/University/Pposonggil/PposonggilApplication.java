package Soongsil.University.Pposonggil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
public class PposonggilApplication {

	public static void main(String[] args) {
		SpringApplication.run(PposonggilApplication.class, args);
	}

}
