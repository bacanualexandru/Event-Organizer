/**
 * Clasa pentru gestionarea operatiilor asupra taskurilor (adaugare, modificare stare, stergere).
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ro.upb.awj.eventorganizer.model.Task;
import ro.upb.awj.eventorganizer.service.TaskService;

@Controller
public class TaskController {

    private final TaskService taskService;

    // Constructor pentru taskuri.
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Adauga un task nou unui eveniment.
    @PostMapping("/events/{eventId}/tasks")
    public String addTask(@PathVariable Long eventId,
                          @Valid @ModelAttribute("task") Task task,
                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "redirect:/events/" + eventId;
        }

        taskService.addTaskToEvent(eventId, task);
        return "redirect:/events/" + eventId;
    }

    //Schimba starea unui task (indeplinit / neindeplinit).
    @GetMapping("/tasks/{taskId}/toggle")
    public String toggle(@PathVariable Long taskId,
                         @RequestParam Long eventId) {
        taskService.toggleDone(taskId);
        return "redirect:/events/" + eventId;
    }

    // Sterge un task asociat unui eveniment.
    @GetMapping("/tasks/{taskId}/delete")
    public String delete(@PathVariable Long taskId,
                         @RequestParam Long eventId) {
        taskService.deleteTask(taskId);
        return "redirect:/events/" + eventId;
    }
}
