package pposonggil.usedStuff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UsedStuffApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsedStuffApplication.class, args);
	}

}
