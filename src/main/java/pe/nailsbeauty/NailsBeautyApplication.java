package pe.nailsbeauty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NailsBeautyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NailsBeautyApplication.class, args);
	}

}
