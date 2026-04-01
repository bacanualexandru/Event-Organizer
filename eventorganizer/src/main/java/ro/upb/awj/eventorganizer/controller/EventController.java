/**
 * Clasa pentru gestionarea operatiilor asupra evenimentelor
 * (listare, filtrare, sortare, adaugare, editare, stergere si cos).
 *
 * @author Băcanu Alexandru-Mihai
 * @version 08 Ianuarie 2026
 */
package ro.upb.awj.eventorganizer.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.upb.awj.eventorganizer.model.Event;
import ro.upb.awj.eventorganizer.model.EventCategory;
import ro.upb.awj.eventorganizer.model.EventStatus;
import ro.upb.awj.eventorganizer.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    // Constructor pentru controller-ul de evenimente.
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Afiseaza lista de evenimente active, cu filtrare si sortare dupa data + filtru perioada.
    @GetMapping({"", "/"})
    public String list(@RequestParam(name = "category", required = false) String category,
                       @RequestParam(name = "order", required = false, defaultValue = "asc") String order,
                       @RequestParam(name = "range", required = false) String range,
                       Model model) {

        addStats(model);

        List<Event> events;

        boolean hasCategory = category != null && !category.isBlank();
        boolean desc = "desc".equalsIgnoreCase(order);

        // 1) Luam lista de baza (active + optional category + sort)
        if (hasCategory) {
            EventCategory enumCategory = EventCategory.valueOf(category);

            if (desc) {
                events = eventService.findActiveByCategorySortedByDateDesc(enumCategory);
            } else {
                events = eventService.findActiveByCategorySortedByDateAsc(enumCategory);
            }

            model.addAttribute("selectedCategory", category);
        } else {
            if (desc) {
                events = eventService.findAllActiveSortedByDateDesc();
            } else {
                events = eventService.findAllActiveSortedByDateAsc();
            }

            model.addAttribute("selectedCategory", "");
        }

        // 2) Aplicam filtru de perioada (urmatoarele 7 zile / 30 zile / 365 zile)
        if (range != null && !range.isBlank()) {
            int days =
                    "7".equals(range) ? 7 :
                            "30".equals(range) ? 30 :
                                    "365".equals(range) ? 365 : 0;

            if (days > 0) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime end = now.plusDays(days);

                events = events.stream()
                        .filter(e -> e.getDateTime() != null)
                        .filter(e -> !e.getDateTime().isBefore(now) && !e.getDateTime().isAfter(end))
                        .toList();
            }

            model.addAttribute("selectedRange", range);
        } else {
            model.addAttribute("selectedRange", "");
        }

        model.addAttribute("events", events);
        model.addAttribute("shownCount", events.size());
        model.addAttribute("order", desc ? "desc" : "asc");

        return "events";
    }

    // Afiseaza formularul pentru adaugarea unui eveniment nou (cu status default).
    @GetMapping("/new")
    public String createForm(Model model) {
        Event event = new Event();
        event.setStatus(EventStatus.PLANIFICAT);

        model.addAttribute("event", event);
        model.addAttribute("categories", EventCategory.values());
        return "event-form";
    }

    // Salveaza un eveniment nou dupa validare si afiseaza un mesaj de succes.
    @PostMapping
    public String save(@Valid @ModelAttribute("event") Event event,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", EventCategory.values());
            return "event-form";
        }

        eventService.save(event);
        redirectAttributes.addFlashAttribute("success", "Eveniment salvat cu succes");
        return "redirect:/events";
    }

    // Afiseaza detaliile unui eveniment si lista de taskuri asociate.
    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Event event = eventService.findById(id);

        model.addAttribute("event", event);
        model.addAttribute("task", new ro.upb.awj.eventorganizer.model.Task());
        model.addAttribute("priorities", ro.upb.awj.eventorganizer.model.TaskPriority.values());

        return "event-details";
    }

    // Afiseaza formularul de editare pentru un eveniment existent.
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Event event = eventService.findById(id);

        model.addAttribute("event", event);
        model.addAttribute("categories", EventCategory.values());

        return "event-form";
    }

    // Actualizeaza un eveniment existent dupa validare si afiseaza un mesaj de succes.
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("event") Event event,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", EventCategory.values());
            return "event-form";
        }

        eventService.update(id, event);
        redirectAttributes.addFlashAttribute("success", "Eveniment actualizat cu succes");
        return "redirect:/events";
    }

    // Sterge logic un eveniment (soft delete) si il muta in cos.
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        eventService.softDelete(id);
        ra.addFlashAttribute("success", "Eveniment mutat in cos");
        return "redirect:/events";
    }

    // Afiseaza lista evenimentelor sterse (cos).
    @GetMapping("/trash")
    public String trash(Model model) {
        model.addAttribute("trashEvents", eventService.findAllTrash());
        return "events-trash";
    }

    // Restaureaza un eveniment din cos inapoi in lista activa.
    @GetMapping("/{id}/restore")
    public String restore(@PathVariable Long id, RedirectAttributes ra) {
        eventService.restore(id);
        ra.addFlashAttribute("success", "Eveniment restaurat");
        return "redirect:/events/trash";
    }

    // Sterge definitiv un eveniment din cos (hard delete).
    @GetMapping("/{id}/deletePermanent")
    public String deletePermanent(@PathVariable Long id, RedirectAttributes ra) {
        eventService.deletePermanently(id);
        ra.addFlashAttribute("success", "Eveniment sters definitiv");
        return "redirect:/events/trash";
    }

    // Adauga in model statisticile globale pentru evenimentele active.
    private void addStats(Model model) {
        model.addAttribute("totalCount", eventService.countAll());
        model.addAttribute("futureCount", eventService.countFutureEvents());
        model.addAttribute("inProgressCount", eventService.countInProgressEvents());
        model.addAttribute("finishedCount", eventService.countFinishedEvents());
        model.addAttribute("importantCount", eventService.countImportant());
    }
}
