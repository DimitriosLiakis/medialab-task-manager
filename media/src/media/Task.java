package media;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Η κλάση {@code Task} αντιπροσωπεύει μια εργασία στο σύστημα διαχείρισης εργασιών.
 * Περιέχει πληροφορίες όπως ο τίτλος, η περιγραφή, η κατηγορία, η προτεραιότητα, η προθεσμία,
 * η κατάσταση και μια λίστα υπενθυμίσεων.
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

    /**
     * Κατασκευαστής της κλάσης {@code Task}.
     *
     * @param id          Ο μοναδικός αναγνωριστικός αριθμός της εργασίας.
     * @param title       Ο τίτλος της εργασίας.
     * @param description Η περιγραφή της εργασίας.
     * @param category    Η κατηγορία στην οποία ανήκει η εργασία.
     * @param priority    Η προτεραιότητα της εργασίας.
     * @param deadline    Η προθεσμία ολοκλήρωσης της εργασίας.
     */
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

    /**
     * Επιστρέφει το μοναδικό αναγνωριστικό της εργασίας.
     *
     * @return Το ID της εργασίας.
     */
    public int getId() {
        return id;
    }

    /**
     * Επιστρέφει τον τίτλο της εργασίας.
     *
     * @return Ο τίτλος της εργασίας.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Θέτει τον τίτλο της εργασίας.
     *
     * @param title Ο νέος τίτλος της εργασίας.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Επιστρέφει την περιγραφή της εργασίας.
     *
     * @return Η περιγραφή της εργασίας.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Θέτει την περιγραφή της εργασίας.
     *
     * @param description Η νέα περιγραφή της εργασίας.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Επιστρέφει την κατηγορία της εργασίας.
     *
     * @return Η κατηγορία της εργασίας.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Θέτει την κατηγορία της εργασίας.
     *
     * @param category Η νέα κατηγορία της εργασίας.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Επιστρέφει την προτεραιότητα της εργασίας.
     *
     * @return Η προτεραιότητα της εργασίας.
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Θέτει την προτεραιότητα της εργασίας.
     *
     * @param priority Η νέα προτεραιότητα της εργασίας.
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Επιστρέφει την προθεσμία ολοκλήρωσης της εργασίας.
     *
     * @return Η προθεσμία της εργασίας.
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Θέτει την προθεσμία της εργασίας.
     *
     * @param deadline Η νέα προθεσμία της εργασίας.
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    /**
     * Επιστρέφει την τρέχουσα κατάσταση της εργασίας.
     *
     * @return Η κατάσταση της εργασίας.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Θέτει την κατάσταση της εργασίας.
     *
     * @param status Η νέα κατάσταση της εργασίας.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Επιστρέφει τη λίστα υπενθυμίσεων της εργασίας.
     *
     * @return Η λίστα υπενθυμίσεων.
     */
    public List<Reminder> getReminders() {
        return reminders;
    }

    /**
     * Θέτει τη λίστα υπενθυμίσεων της εργασίας.
     *
     * @param reminders Η νέα λίστα υπενθυμίσεων.
     */
    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    /**
     * Προσθέτει μία υπενθύμιση στην εργασία.
     *
     * @param reminder Η υπενθύμιση που θα προστεθεί.
     */
    public void addReminder(Reminder reminder) {
        this.reminders.add(reminder);
    }

    /**
     * Αφαιρεί μία υπενθύμιση από την εργασία.
     *
     * @param reminder Η υπενθύμιση που θα αφαιρεθεί.
     */
    public void removeReminder(Reminder reminder) {
        this.reminders.remove(reminder);
    }

    /**
     * Καθαρίζει όλες τις υπενθυμίσεις της εργασίας.
     */
    public void clearReminders() {
        this.reminders.clear();
    }
}
