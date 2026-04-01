/**
 * Repository pentru operatii CRUD asupra entitatii Task.
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.upb.awj.eventorganizer.model.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Returneaza toate taskurile asociate unui eveniment.
    List<Task> findByEventId(Long eventId);
}
