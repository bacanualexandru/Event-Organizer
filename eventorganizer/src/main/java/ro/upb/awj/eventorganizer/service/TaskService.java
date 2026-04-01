/**
 * Clasa pentru gestionarea operatiilor asupra taskurilor.
 * Permite adaugare, modificare, stergere si schimbarea starii (done) a unui task.
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.upb.awj.eventorganizer.model.Event;
import ro.upb.awj.eventorganizer.model.Task;
import ro.upb.awj.eventorganizer.repository.EventRepository;
import ro.upb.awj.eventorganizer.repository.TaskRepository;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    // Constructor pentru repository-urilor si a serviciului de evenimente.
    public TaskService(TaskRepository taskRepository,
                       EventRepository eventRepository,
                       EventService eventService) {
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    // Returneaza toate taskurile asociate unui eveniment.
    public List<Task> findByEventId(Long eventId) {
        return taskRepository.findByEventId(eventId);
    }

    // Adauga un task nou unui eveniment si recalculeaza statusul evenimentului.
    @Transactional
    public Task addTaskToEvent(Long eventId, Task task) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event inexistent: id=" + eventId));

        task.setEvent(event);
        Task saved = taskRepository.save(task);

        // Dupa adaugare task, recalculam statusul eventului (in functie de progres)
        eventService.recomputeStatus(eventId);

        return saved;
    }

    // Actualizeaza un task existent si recalculeaza statusul evenimentului.
    @Transactional
    public Task updateTask(Long taskId, Task updated) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task inexistent: id=" + taskId));

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setDone(updated.isDone());
        existing.setPriority(updated.getPriority());

        Task saved = taskRepository.save(existing);

        Long eventId = saved.getEvent().getId();
        eventService.recomputeStatus(eventId);

        return saved;
    }

    // Sterge un task si recalculeaza statusul evenimentului.
    @Transactional
    public void deleteTask(Long taskId) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task inexistent: id=" + taskId));

        Event event = existing.getEvent();
        Long eventId = event.getId();

        event.getTasks().remove(existing);

        taskRepository.delete(existing);

        eventService.recomputeStatus(eventId);
    }

    @Transactional
    public Task toggleDone(Long taskId) {
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task inexistent: id=" + taskId));

        existing.setDone(!existing.isDone());
        Task saved = taskRepository.save(existing);

        Long eventId = saved.getEvent().getId();
        eventService.recomputeStatus(eventId);

        return saved;
    }
}