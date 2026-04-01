/**
 * Repository pentru gestionarea entitatii Event.
 * Contine metode pentru filtrare, sortare, soft delete
 * si selectarea evenimentelor pe interval de timp.
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import ro.upb.awj.eventorganizer.model.Event;
import ro.upb.awj.eventorganizer.model.EventCategory;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Returneaza evenimentele dintr-o anumita categorie.
    List<Event> findByCategory(EventCategory category);

    // Returneaza evenimentele dintr-o categorie, sortate.
    List<Event> findByCategory(EventCategory category, Sort sort);

    // Returneaza evenimentele dintr-un interval de timp (ex: urmatoarele N zile).
    List<Event> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    // Returneaza toate evenimentele active (ne-sterse).
    List<Event> findAllByDeletedFalse();

    // Returneaza evenimentele active, sortate.
    List<Event> findAllByDeletedFalse(Sort sort);

    // Returneaza toate evenimentele sterse (cos).
    List<Event> findAllByDeletedTrue();

    // Returneaza evenimentele sterse, sortate.
    List<Event> findAllByDeletedTrue(Sort sort);

    // Returneaza evenimentele active dintr-o categorie.
    List<Event> findByCategoryAndDeletedFalse(EventCategory category);

    // Returneaza evenimentele active dintr-o categorie, sortate.
    List<Event> findByCategoryAndDeletedFalse(EventCategory category, Sort sort);
}