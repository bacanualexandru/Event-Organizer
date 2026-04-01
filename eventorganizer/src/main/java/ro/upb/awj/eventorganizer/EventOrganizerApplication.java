/**
 * Clasa principala a aplicatiei Event Organizer.
 * Porneste aplicatia Spring Boot.
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventOrganizerApplication {

	// Metoda main care porneste aplicatia
	public static void main(String[] args) {
		SpringApplication.run(EventOrganizerApplication.class, args);
	}
}
