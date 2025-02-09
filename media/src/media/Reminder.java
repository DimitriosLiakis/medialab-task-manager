// File: src/media/Reminder.java
package media;

import java.time.LocalDate;

/**
 * Η κλάση Reminder αντιπροσωπεύει μια υπενθύμιση για μια εργασία.
 * Περιλαμβάνει τον τύπο της υπενθύμισης και την ημερομηνία της.
 */
public class Reminder {
    private String type; // "1 day before", "1 week before", "1 month before", "Custom Date"
    private LocalDate date; // Για "Custom Date"

    public Reminder(String type, LocalDate date) {
        this.type = type;
        this.date = date;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
