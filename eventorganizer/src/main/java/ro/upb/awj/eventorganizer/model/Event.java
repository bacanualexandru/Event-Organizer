/**
 * Clasa pentru reprezentarea unui eveniment.
 * Contine campurile principale, validari, relatie cu taskurile si calculul progresului.
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titlul este obligatoriu")
    @Size(min = 3, max = 80, message = "Titlul trebuie sa aiba intre 3 si 80 de caractere")
    private String title;

    @Size(max = 500, message = "Descrierea poate avea maxim 500 de caractere")
    private String description;

    @NotNull(message = "Data si ora sunt obligatorii")
    @FutureOrPresent(message = "Data evenimentului nu poate fi in trecut")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateTime;

    @NotBlank(message = "Locatia este obligatorie")
    @Size(max = 120, message = "Locatia poate avea maxim 120 de caractere")
    private String location;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Statusul este obligatoriu")
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Categoria este obligatorie")
    private EventCategory category;

    private boolean important;

    @Column(nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Task> tasks = new ArrayList<>();

    // Constructor default.
    public Event() {
    }

    // Returneaza ID-ul evenimentului.
    public Long getId() {
        return id;
    }

    // Seteaza ID-ul (folosit la editare in formular).
    public void setId(Long id) {
        this.id = id;
    }

    // Returneaza titlul evenimentului.
    public String getTitle() {
        return title;
    }

    // Seteaza titlul evenimentului.
    public void setTitle(String title) {
        this.title = title;
    }

    // Returneaza descrierea evenimentului.
    public String getDescription() {
        return description;
    }

    // Seteaza descrierea evenimentului.
    public void setDescription(String description) {
        this.description = description;
    }

    // Returneaza data si ora evenimentului.
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    // Seteaza data si ora evenimentului.
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    // Returneaza locatia evenimentului.
    public String getLocation() {
        return location;
    }

    // Seteaza locatia evenimentului.
    public void setLocation(String location) {
        this.location = location;
    }

    // Returneaza statusul evenimentului.
    public EventStatus getStatus() {
        return status;
    }

    // Seteaza statusul evenimentului.
    public void setStatus(EventStatus status) {
        this.status = status;
    }

    // Returneaza categoria evenimentului.
    public EventCategory getCategory() {
        return category;
    }

    // Seteaza categoria evenimentului.
    public void setCategory(EventCategory category) {
        this.category = category;
    }

    // Returneaza daca evenimentul este marcat ca important.
    public boolean isImportant() {
        return important;
    }

    // Seteaza marcajul de important pentru eveniment.
    public void setImportant(boolean important) {
        this.important = important;
    }

    // Returneaza daca evenimentul este sters logic (in cos).
    public boolean isDeleted() {
        return deleted;
    }

    // Seteaza starea de stergere logica (soft delete).
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // Returneaza lista de taskuri asociate evenimentului.
    public List<Task> getTasks() {
        return tasks;
    }

    // Calculeaza progresul evenimentului in functie de taskurile finalizate.
    @Transient
    public int getProgress() {
        if (tasks.isEmpty()) {
            return 0;
        }
        long completed = tasks.stream()
                .filter(Task::isDone)
                .count();
        return (int) ((completed * 100.0) / tasks.size());
    }

    // Metoda equals.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id != null && id.equals(event.id);
    }

    // Metoda hashCode.
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}