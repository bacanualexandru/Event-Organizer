/**
 * Teste unitare pentru calculul progresului unui eveniment.
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventProgressTest {

    // Verifica progresul cand nu exista taskuri.
    @Test
    void progress_is_0_when_no_tasks() {
        Event e = new Event();
        assertEquals(0, e.getProgress());
    }

    // Verifica progresul cand 1 din 3 taskuri este finalizat (33%).
    @Test
    void progress_is_33_when_1_of_3_tasks_done() {
        Event event = new Event();

        Task t1 = new Task("mancat", false);
        Task t2 = new Task("piscina", true);
        Task t3 = new Task("alergat", false);

        for (Task t : new Task[]{t1, t2, t3}) {
            t.setEvent(event);
            event.getTasks().add(t);
        }

        assertEquals(33, event.getProgress());
    }

    // Verifica progresul cand toate taskurile sunt finalizate (100%).
    @Test
    void progress_is_100_when_all_tasks_done() {
        Event e = new Event();

        Task t1 = new Task("t1", true);
        Task t2 = new Task("t2", true);

        for (Task t : new Task[]{t1, t2}) {
            t.setEvent(e);
            e.getTasks().add(t);
        }

        assertEquals(100, e.getProgress());
    }

    // Verifica progresul cand 2 din 4 taskuri sunt finalizate (50%).
    @Test
    void progress_is_50_when_2_of_4_done() {
        Event e = new Event();

        Task t1 = new Task("t1", true);
        Task t2 = new Task("t2", true);
        Task t3 = new Task("t3", false);
        Task t4 = new Task("t4", false);

        for (Task t : new Task[]{t1, t2, t3, t4}) {
            t.setEvent(e);
            e.getTasks().add(t);
        }

        assertEquals(50, e.getProgress());
    }
}
