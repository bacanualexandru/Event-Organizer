/**
 * Teste pentru functionalitatea de cos (soft delete / restore / hard delete).
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ro.upb.awj.eventorganizer.model.Event;
import ro.upb.awj.eventorganizer.model.EventCategory;
import ro.upb.awj.eventorganizer.model.EventStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SoftDeleteFlowTest {

    @Autowired
    private EventService eventService;

    // Verifica mutarea in cos si restaurarea unui eveniment.
    @Test
    void soft_delete_moves_event_to_trash_and_restore_brings_it_back() {
        Event e = baseEvent("TrashTest");
        Event saved = eventService.save(e);

        // soft delete
        eventService.softDelete(saved.getId());

        assertTrue(eventService.findAllTrash().stream().anyMatch(x -> x.getId().equals(saved.getId())));
        assertTrue(eventService.findAllActive().stream().noneMatch(x -> x.getId().equals(saved.getId())));

        // restore
        eventService.restore(saved.getId());

        assertTrue(eventService.findAllActive().stream().anyMatch(x -> x.getId().equals(saved.getId())));
    }

    private Event baseEvent(String title) {
        Event e = new Event();
        e.setTitle(title);
        e.setDescription("desc");
        e.setLocation("Bucuresti");
        e.setCategory(EventCategory.MUNCA);
        e.setDateTime(LocalDateTime.now().plusDays(2));
        e.setImportant(false);
        e.setStatus(EventStatus.PLANIFICAT);
        return e;
    }
}
