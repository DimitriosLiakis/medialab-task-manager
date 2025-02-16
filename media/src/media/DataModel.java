package media;

import java.util.List;

/**
 * Η κλάση DataModel αντιπροσωπεύει το σύνολο των δεδομένων της εφαρμογής.
 * Περιλαμβάνει λίστες με εργασίες, κατηγορίες και προτεραιότητες.
 */
public class DataModel {
    private List<Task> tasks;
    private List<String> categories;
    private List<String> priorities;

    public DataModel(List<Task> tasks, List<String> categories, List<String> priorities) {
        this.tasks = tasks;
        this.categories = categories;
        this.priorities = priorities;
    }

    // Getters and Setters
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<String> priorities) {
        this.priorities = priorities;
    }
}
