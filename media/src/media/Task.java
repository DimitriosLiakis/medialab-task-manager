// File: src/media/Task.java
package media;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Η κλάση Task αντιπροσωπεύει μια εργασία στο σύστημα διαχείρισης εργασιών.
 * Περιλαμβάνει πληροφορίες όπως τίτλος, περιγραφή, κατηγορία, προτεραιότητα, προθεσμία, κατάσταση και υπενθυμίσεις.
 */
public class Task {
    private int id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private LocalDate deadline;
    private String status;
    private List<Reminder> reminders; // Λίστα υπενθυμίσεων

    public Task(int id, String title, String description, String category, String priority, LocalDate deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.deadline = deadline;
        this.status = "Open"; // Προκαθορισμένη κατάσταση
        this.reminders = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Reminder> getReminders() { return reminders; }
    public void setReminders(List<Reminder> reminders) { this.reminders = reminders; }

    // Reminder management
    public void addReminder(Reminder reminder) {
        this.reminders.add(reminder);
    }

    public void removeReminder(Reminder reminder) {
        this.reminders.remove(reminder);
    }

    public void clearReminders() {
        this.reminders.clear();
    }
}
