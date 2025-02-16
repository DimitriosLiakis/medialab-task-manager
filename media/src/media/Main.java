package media;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Η κλάση Main είναι η κύρια κλάση της εφαρμογής MediaLab Assistant.
 * Φορτώνει τα δεδομένα, αρχικοποιεί την γραφική διεπαφή και διαχειρίζεται την αποθήκευση των δεδομένων κατά την εκκίνηση και τερματισμό της εφαρμογής.
 */
public class Main extends Application {
    private DataModel dataModel;
    private List<Task> taskList;
    private List<String> categories;
    private List<String> priorities;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MediaLab Assistant");

        loadApplicationState();
        checkAndUpdateTaskStatuses();

        // Ensure "Default" category and priority exist
        if (!categories.contains("Default")) {
            categories.add("Default");
            dataModel.getCategories().add("Default");
        }
        if (!priorities.contains("Default")) {
            priorities.add("Default");
            dataModel.getPriorities().add("Default");
        }

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1200, 700);

        MainUI mainUI = new MainUI(root, dataModel); 
        mainUI.initUI();

        primaryStage.setScene(scene);

        // Popup για καθυστερημένες εργασίες
        long delayedCount = taskList.stream().filter(task -> "Delayed".equals(task.getStatus())).count();
        if (delayedCount > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Delayed Tasks");
            alert.setHeaderText("You have " + delayedCount + " delayed task(s).");
            alert.setContentText("Please review your delayed tasks.");
            alert.showAndWait();
        }

        primaryStage.setOnCloseRequest(e -> saveApplicationState());

        primaryStage.show();
    }

    /**
     * Φορτώνει την κατάσταση της εφαρμογής από το αρχείο JSON.
     */
    private void loadApplicationState() {
        dataModel = StorageManager.loadData();
        taskList = dataModel.getTasks();
        categories = dataModel.getCategories();
        priorities = dataModel.getPriorities();
    }

    /**
     * Ελέγχει και ενημερώνει τις καταστάσεις των εργασιών που έχουν περάσει την προθεσμία.
     */
    private void checkAndUpdateTaskStatuses() {
        LocalDate today = LocalDate.now();
        for (Task task : taskList) {
            if (!"Completed".equals(task.getStatus()) && !"Delayed".equals(task.getStatus())) {
                LocalDate deadline = task.getDeadline();
                if (deadline.isBefore(today)) {
                    task.setStatus("Delayed");
                }
            }
        }
    }

    /**
     * Αποθηκεύει την κατάσταση της εφαρμογής στο αρχείο JSON.
     */
    private void saveApplicationState() {
        StorageManager.saveData(dataModel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
