/**
 * Teste pentru recalcularea statusului evenimentului dupa modificari pe taskuri.
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ro.upb.awj.eventorganizer.model.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TaskServiceRecomputeStatusTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private TaskService taskService;

    // Verifica statusul dupa toggle done.
    @Test
    void toggle_done_recomputes_event_status() {
        Event e = baseEvent("ToggleTest");
        Event savedEvent = eventService.save(e);

        Task t1 = new Task("t1", false);
        t1.setPriority(TaskPriority.LOW);

        Task added = taskService.addTaskToEvent(savedEvent.getId(), t1);

        // dupa adaugare (0%), status ar trebui PLANIFICAT
        Event afterAdd = eventService.findById(savedEvent.getId());
        assertEquals(EventStatus.PLANIFICAT, afterAdd.getStatus());

        // toggle -> 100% (1 task done) => FINALIZAT
        taskService.toggleDone(added.getId());
        Event afterToggle = eventService.findById(savedEvent.getId());
        assertEquals(EventStatus.FINALIZAT, afterToggle.getStatus());
    }

    private Event baseEvent(String title) {
        Event e = new Event();
        e.setTitle(title);
        e.setDescription("desc");
        e.setLocation("Bucuresti");
        e.setCategory(EventCategory.SOCIAL);
        e.setDateTime(LocalDateTime.now().plusDays(1));
        e.setImportant(false);
        e.setStatus(EventStatus.PLANIFICAT);
        return e;
    }
}
