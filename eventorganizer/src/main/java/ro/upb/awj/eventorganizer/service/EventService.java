/**
 * Clasa pentru gestionarea operatiilor asupra evenimentelor.
 * Include CRUD, filtrare/sortare, cos, statistici si status automat.
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ro.upb.awj.eventorganizer.model.Event;
import ro.upb.awj.eventorganizer.model.EventCategory;
import ro.upb.awj.eventorganizer.model.EventStatus;
import ro.upb.awj.eventorganizer.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    // Constructor pentru repository-ului de evenimente.
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // Returneaza toate evenimentele.
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    // Returneaza un eveniment dupa ID sau arunca exceptie daca nu exista.
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event inexistent: id=" + id));
    }

    // Salveaza un eveniment nou si aplica status automat in functie de progres.
    public Event save(Event event) {
        applyAutoStatus(event);
        return eventRepository.save(event);
    }

    // Returneaza doar evenimentele active.
    public List<Event> findAllActive() {
        return eventRepository.findAllByDeletedFalse();
    }

    // Returneaza evenimentele din cos.
    public List<Event> findAllTrash() {
        return eventRepository.findAllByDeletedTrue();
    }

    // Returneaza evenimentele active sortate crescator dupa data.
    public List<Event> findAllActiveSortedByDateAsc() {
        return eventRepository.findAllByDeletedFalse(Sort.by(Sort.Direction.ASC, "dateTime"));
    }

    // Returneaza evenimentele active sortate descrescator dupa data.
    public List<Event> findAllActiveSortedByDateDesc() {
        return eventRepository.findAllByDeletedFalse(Sort.by(Sort.Direction.DESC, "dateTime"));
    }

    // Returneaza evenimentele active dintr-o categorie, sortate crescator dupa data.
    public List<Event> findActiveByCategorySortedByDateAsc(EventCategory category) {
        return eventRepository.findByCategoryAndDeletedFalse(category, Sort.by(Sort.Direction.ASC, "dateTime"));
    }

    // Returneaza evenimentele active dintr-o categorie, sortate descrescator dupa data.
    public List<Event> findActiveByCategorySortedByDateDesc(EventCategory category) {
        return eventRepository.findByCategoryAndDeletedFalse(category, Sort.by(Sort.Direction.DESC, "dateTime"));
    }

    // Marcheaza un eveniment ca sters si il muta in cos.
    public void softDelete(Long id) {
        Event e = findById(id);
        e.setDeleted(true);
        eventRepository.save(e);
    }

    // Restaureaza un eveniment din cos in lista activa.
    public void restore(Long id) {
        Event e = findById(id);
        e.setDeleted(false);
        eventRepository.save(e);
    }

    // Sterge definitiv un eveniment (hard delete).
    public void deletePermanently(Long id) {
        eventRepository.deleteById(id);
    }

    // Aplica status automat in functie de progresul taskurilor.
    private void applyAutoStatus(Event event) {
        int p = event.getProgress();

        if (p == 100) {
            event.setStatus(EventStatus.FINALIZAT);
        } else if (p == 0) {
            event.setStatus(EventStatus.PLANIFICAT);
        } else {
            event.setStatus(EventStatus.IN_DESFASURARE);
        }
    }

    // Actualizeaza un eveniment existent si recalculeaza statusul automat.
    public Event update(Long id, Event updated) {
        Event existing = findById(id);

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setDateTime(updated.getDateTime());
        existing.setLocation(updated.getLocation());
        existing.setCategory(updated.getCategory());
        existing.setImportant(updated.isImportant());

        // Statusul nu se ia din form, se calculeaza automat
        applyAutoStatus(existing);

        return eventRepository.save(existing);
    }

    // Returneaza evenimentele dintr-o categorie, sortate crescator dupa data.
    public List<Event> findByCategorySortedByDateAsc(EventCategory category) {
        return eventRepository.findByCategory(
                category,
                Sort.by(Sort.Direction.ASC, "dateTime")
        );
    }

    // Returneaza evenimentele dintr-o categorie, sortate descrescator dupa data.
    public List<Event> findByCategorySortedByDateDesc(EventCategory category) {
        return eventRepository.findByCategory(
                category,
                Sort.by(Sort.Direction.DESC, "dateTime")
        );
    }

    // Recalculeaza statusul unui eveniment (folosit dupa modificarea taskurilor).
    public void recomputeStatus(Long eventId) {
        Event event = findById(eventId);
        applyAutoStatus(event);
        eventRepository.save(event);
    }

    // Sterge definitiv un eveniment dupa ID.
    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

    // Returneaza evenimentele dintr-o anumita categorie.
    public List<Event> findByCategory(EventCategory category) {
        return eventRepository.findByCategory(category);
    }

    // Returneaza toate evenimentele sortate crescator dupa data.
    public List<Event> findAllSortedByDateAsc() {
        return eventRepository.findAll(Sort.by(Sort.Direction.ASC, "dateTime"));
    }

    // Returneaza toate evenimentele sortate descrescator dupa data.
    public List<Event> findAllSortedByDateDesc() {
        return eventRepository.findAll(Sort.by(Sort.Direction.DESC, "dateTime"));
    }

    // Returneaza numarul total de evenimente active.
    public long countAll() {
        return eventRepository.findAllByDeletedFalse().size();
    }

    // Returneaza numarul de evenimente active marcate ca importante.
    public long countImportant() {
        return eventRepository.findAllByDeletedFalse().stream()
                .filter(Event::isImportant)
                .count();
    }

    // Returneaza numarul de evenimente viitoare (progres 0 si data in viitor).
    public long countFutureEvents() {
        LocalDateTime now = LocalDateTime.now();

        return eventRepository.findAllByDeletedFalse().stream()
                .filter(e -> e.getDateTime() != null && e.getDateTime().isAfter(now))
                .filter(e -> e.getProgress() == 0)
                .count();
    }

    // Returneaza numarul de evenimente in desfasurare (progres intre 1 si 99).
    public long countInProgressEvents() {
        return eventRepository.findAllByDeletedFalse().stream()
                .filter(e -> e.getProgress() > 0 && e.getProgress() < 100)
                .count();
    }

    // Returneaza numarul de evenimente finalizate (progres 100).
    public long countFinishedEvents() {
        return eventRepository.findAllByDeletedFalse().stream()
                .filter(e -> e.getProgress() == 100)
                .count();
    }

    // Returneaza evenimentele din urmatoarele N zile.
    public List<Event> upcomingDays(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusDays(days);
        return eventRepository.findByDateTimeBetween(now, end);
    }
}
