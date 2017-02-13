package pl.szleperm.messenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MessengerApplication{

    public static void main(String[] args) {
        SpringApplication.run(MessengerApplication.class, args);
        /*SpringApplicationBuilder builder = new SpringApplicationBuilder(MessengerApplication.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);*/ // Todo delete Dev database tool
    }
}
