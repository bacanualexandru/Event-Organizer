/**
 * Teste unitare pentru equals() si hashCode().
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    // Verifica daca doua evenimente cu acelasi ID sunt egale.
    @Test
    void equals_returns_true_for_same_id() {
        Event e1 = new Event();
        Event e2 = new Event();

        e1.setId(1L);
        e2.setId(1L);

        assertEquals(e1, e2);
    }

    // Verifica daca doua evenimente cu ID-uri diferite nu sunt egale.
    @Test
    void equals_returns_false_for_different_id() {
        Event e1 = new Event();
        Event e2 = new Event();

        e1.setId(1L);
        e2.setId(2L);

        assertNotEquals(e1, e2);
    }

    // Verifica daca un eveniment nu este egal cu null.
    @Test
    void equals_returns_false_for_null() {
        Event e = new Event();
        e.setId(1L);

        assertNotEquals(e, null);
    }

    // Verifica daca un eveniment nu este egal cu un obiect de alt tip.
    @Test
    void equals_returns_false_for_different_class() {
        Event e = new Event();
        e.setId(1L);

        assertNotEquals(e, "event");
    }

    // Verifica daca hashCode este acelasi pentru evenimente cu acelasi ID.
    @Test
    void hashCode_is_same_for_same_id() {
        Event e1 = new Event();
        Event e2 = new Event();

        e1.setId(1L);
        e2.setId(1L);

        assertEquals(e1.hashCode(), e2.hashCode());
    }
}
