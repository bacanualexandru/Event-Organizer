/**
 * Clasa pentru reprezentarea unui task asociat unui eveniment.
 * Un task poate avea prioritate si stare de finalizare.
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titlul taskului este obligatoriu")
    @Size(min = 2, max = 80, message = "Titlul taskului trebuie sa aiba intre 2 si 80 de caractere")
    private String title;

    @Size(max = 300, message = "Descrierea taskului poate avea maxim 300 de caractere")
    private String description;

    // Indica daca taskul este finalizat
    private boolean done;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    // Evenimentul caruia ii apartine taskul
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // Constructor default.
    public Task() {
    }

    // Constructor util pentru initializare rapida.
    public Task(String title, boolean done) {
        this.title = title;
        this.done = done;
    }

    // Returneaza ID-ul taskului.
    public Long getId() {
        return id;
    }

    // Returneaza titlul taskului.
    public String getTitle() {
        return title;
    }

    // Seteaza titlul taskului.
    public void setTitle(String title) {
        this.title = title;
    }

    // Returneaza descrierea taskului.
    public String getDescription() {
        return description;
    }

    // Seteaza descrierea taskului.
    public void setDescription(String description) {
        this.description = description;
    }

    // Verifica daca taskul este finalizat.
    public boolean isDone() {
        return done;
    }

    // Seteaza starea de finalizare a taskului.
    public void setDone(boolean done) {
        this.done = done;
    }

    // Returneaza prioritatea taskului.
    public TaskPriority getPriority() {
        return priority;
    }

    // Seteaza prioritatea taskului.
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    // Returneaza evenimentul asociat taskului.
    public Event getEvent() {
        return event;
    }

    // Asociaza taskul unui eveniment.
    public void setEvent(Event event) {
        this.event = event;
    }

    // Metoda equals.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id != null && id.equals(task.id);
    }

    // Metoda hashCode.
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
