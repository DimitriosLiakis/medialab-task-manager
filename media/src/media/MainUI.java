// File: src/media/MainUI.java
package media;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Η κλάση MainUI διαχειρίζεται την γραφική διεπαφή της εφαρμογής MediaLab Assistant.
 * Παρέχει τις λειτουργίες για τη διαχείριση εργασιών, κατηγοριών, προτεραιοτήτων και υπενθυμίσεων.
 */
public class MainUI {
    private BorderPane root;
    private DataModel dataModel;
    private List<Task> taskList;
    private List<String> categories;
    private List<String> priorities;

    private TableView<Task> taskTable;
    private ObservableList<Task> observableTaskList;
    private FilteredList<Task> filteredData; // FilteredList for search functionality

    // Πληροφορίες στην κορυφή
    private Label totalTasksLabel;
    private Label completedTasksLabel;
    private Label delayedTasksLabel;
    private Label upcomingDeadlinesLabel;

    /**
     * Κατασκευαστής της κλάσης MainUI.
     *
     * @param root      Το κεντρικό BorderPane της γραφικής διεπαφής.
     * @param dataModel Το αντικείμενο DataModel που περιέχει τα δεδομένα της εφαρμογής.
     */
    public MainUI(BorderPane root, DataModel dataModel) {
        this.root = root;
        this.dataModel = dataModel;
        // Assume that dataModel.getTasks(), getCategories(), and getPriorities() return the same list references.
        this.taskList = dataModel.getTasks();
        this.categories = dataModel.getCategories();
        this.priorities = dataModel.getPriorities();
    }

    /**
     * Αρχικοποιεί την γραφική διεπαφή.
     */
    public void initUI() {
        setupMenuBar();
        setupTopInfo();
        setupSearchBar();
        setupTaskTable();
        setupAddTaskForm();
        updateTopInfo();
    }

    /**
     * Ρυθμίζει το μενού της εφαρμογής.
     */
    private void setupMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);

        // Manage Menu
        Menu manageMenu = new Menu("Manage");

        // Categories Submenu
        Menu manageCategories = new Menu("Categories");
        MenuItem addCategory = new MenuItem("Add Category");
        addCategory.setOnAction(e -> showAddCategoryDialog());
        MenuItem editCategory = new MenuItem("Edit Category");
        editCategory.setOnAction(e -> showEditCategoryDialog());
        MenuItem deleteCategory = new MenuItem("Delete Category");
        deleteCategory.setOnAction(e -> showDeleteCategoryDialog());
        manageCategories.getItems().addAll(addCategory, editCategory, deleteCategory);

        // Priorities Submenu
        Menu managePriorities = new Menu("Priorities");
        MenuItem addPriority = new MenuItem("Add Priority");
        addPriority.setOnAction(e -> showAddPriorityDialog());
        MenuItem editPriority = new MenuItem("Edit Priority");
        editPriority.setOnAction(e -> showEditPriorityDialog());
        MenuItem deletePriority = new MenuItem("Delete Priority");
        deletePriority.setOnAction(e -> showDeletePriorityDialog());
        managePriorities.getItems().addAll(addPriority, editPriority, deletePriority);

        manageMenu.getItems().addAll(manageCategories, managePriorities);

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, manageMenu, helpMenu);
        root.setTop(menuBar);
    }

    /**
     * Ρυθμίζει τις συγκεντρωτικές πληροφορίες στην κορυφή της οθόνης.
     */
    private void setupTopInfo() {
        HBox infoBox = new HBox(20);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-background-color: #f0f0f0;");

        totalTasksLabel = new Label();
        completedTasksLabel = new Label();
        delayedTasksLabel = new Label();
        upcomingDeadlinesLabel = new Label();

        infoBox.getChildren().addAll(
                totalTasksLabel,
                completedTasksLabel,
                delayedTasksLabel,
                upcomingDeadlinesLabel
        );

        VBox topContainer = new VBox(root.getTop(), infoBox);
        root.setTop(topContainer);
    }

    /**
     * Ενημερώνει τις συγκεντρωτικές πληροφορίες.
     */
    private void updateTopInfo() {
        long totalTasks = taskList.size();
        long completedTasks = taskList.stream().filter(task -> "Completed".equals(task.getStatus())).count();
        long delayedTasks = taskList.stream().filter(task -> "Delayed".equals(task.getStatus())).count();
        long upcomingDeadlines = taskList.stream()
                .filter(task -> task.getDeadline().isAfter(LocalDate.now()) &&
                        !task.getDeadline().isAfter(LocalDate.now().plusDays(7)))
                .count();

        totalTasksLabel.setText("Total Tasks: " + totalTasks);
        completedTasksLabel.setText("Completed: " + completedTasks);
        delayedTasksLabel.setText("Delayed: " + delayedTasks);
        upcomingDeadlinesLabel.setText("Deadlines in 7 Days: " + upcomingDeadlines);
    }

    /**
     * Ρυθμίζει τη γραμμή αναζήτησης.
     */
    private void setupSearchBar() {
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Title");

        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Filter by Category");
        List<String> categoryItems = new ArrayList<>();
        categoryItems.add("All");
        categoryItems.addAll(categories);
        categoryFilter.setItems(FXCollections.observableArrayList(categoryItems));
        categoryFilter.setValue("All");

        ComboBox<String> priorityFilter = new ComboBox<>();
        priorityFilter.setPromptText("Filter by Priority");
        List<String> priorityItems = new ArrayList<>();
        priorityItems.add("All");
        priorityItems.addAll(priorities);
        priorityFilter.setItems(FXCollections.observableArrayList(priorityItems));
        priorityFilter.setValue("All");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String title = searchField.getText().trim().toLowerCase();
            String category = categoryFilter.getValue();
            String priority = priorityFilter.getValue();

            // Update the predicate of the FilteredList instead of replacing the items
            filteredData.setPredicate(task -> {
                boolean matchesTitle = title.isEmpty() || task.getTitle().toLowerCase().contains(title);
                boolean matchesCategory = category.equals("All") || task.getCategory().equals(category);
                boolean matchesPriority = priority.equals("All") || task.getPriority().equals(priority);
                return matchesTitle && matchesCategory && matchesPriority;
            });

            updateTopInfo();
        });

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> {
            searchField.clear();
            categoryFilter.setValue("All");
            priorityFilter.setValue("All");
            filteredData.setPredicate(task -> true);  // Show all tasks
            updateTopInfo();
        });

        searchBox.getChildren().addAll(
                new Label("Search:"), searchField,
                new Label("Category:"), categoryFilter,
                new Label("Priority:"), priorityFilter,
                searchButton, clearButton
        );

        VBox topContainer = (VBox) root.getTop();
        topContainer.getChildren().add(searchBox);
    }

    /**
     * Ρυθμίζει τον πίνακα εργασιών.
     */
    private void setupTaskTable() {
        taskTable = new TableView<>();
        // Create an observable list that wraps the existing taskList so that any change is reflected
        observableTaskList = FXCollections.observableList(taskList);
        filteredData = new FilteredList<>(observableTaskList, p -> true);
        taskTable.setItems(filteredData);

        // Define Columns
        TableColumn<Task, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(50);

        TableColumn<Task, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setPrefWidth(150);

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(250);

        TableColumn<Task, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setPrefWidth(100);

        TableColumn<Task, String> priorityColumn = new TableColumn<>("Priority");
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityColumn.setPrefWidth(100);

        TableColumn<Task, LocalDate> deadlineColumn = new TableColumn<>("Deadline");
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        deadlineColumn.setPrefWidth(100);

        TableColumn<Task, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(100);

        // Action Column for Edit, Delete, Reminders
        TableColumn<Task, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setPrefWidth(250);
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button manageRemindersButton = new Button("Reminders");
            private final HBox pane = new HBox(5, editButton, deleteButton, manageRemindersButton);

            {
                editButton.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    showEditTaskDialog(task);
                });

                deleteButton.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    deleteTask(task);
                });

                manageRemindersButton.setOnAction(e -> {
                    Task task = getTableView().getItems().get(getIndex());
                    showManageRemindersDialog(task);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        taskTable.getColumns().addAll(idColumn, titleColumn, descriptionColumn, categoryColumn,
                priorityColumn, deadlineColumn, statusColumn, actionColumn);

        root.setCenter(taskTable);
    }

    /**
     * Ρυθμίζει τη φόρμα προσθήκης νέας εργασίας.
     */
    private void setupAddTaskForm() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-background-color: #f9f9f9;");

        // Title
        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        // Description
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        // Category (this will be at index 3)
        ComboBox<String> categoryComboBox = new ComboBox<>(FXCollections.observableArrayList(categories));
        categoryComboBox.setPromptText("Category");

        // Priority (this will be at index 4)
        ComboBox<String> priorityComboBox = new ComboBox<>(FXCollections.observableArrayList(priorities));
        priorityComboBox.setPromptText("Priority");

        // Deadline
        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Deadline (YYYY-MM-DD)");

        // Status (disabled for new tasks; always "Open")
        ComboBox<String> statusComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Open", "In Progress", "Postponed", "Completed", "Delayed"));
        statusComboBox.setValue("Open");
        statusComboBox.setDisable(true);

        // Add Button – note that we now add the task only once to the observable list.
        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String description = descriptionField.getText().trim();
            String category = categoryComboBox.getValue();
            String priority = priorityComboBox.getValue();
            LocalDate deadline = deadlinePicker.getValue();

            if (title.isEmpty() || category == null || priority == null || deadline == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter all required fields.");
                return;
            }

            int newId = taskList.stream().mapToInt(Task::getId).max().orElse(0) + 1;
            Task newTask = new Task(newId, title, description, category, priority, deadline);
            // Add the task only once.
            observableTaskList.add(newTask);

            // Clear Fields
            titleField.clear();
            descriptionField.clear();
            categoryComboBox.setValue(null);
            priorityComboBox.setValue(null);
            deadlinePicker.setValue(null);

            updateTopInfo();
        });

        // Layout: note the indices—categoryComboBox is at index 3, priorityComboBox at index 4.
        HBox buttonsBox = new HBox(10, addButton);
        formBox.getChildren().addAll(
                new Label("Add New Task:"),
                titleField,
                descriptionField,
                categoryComboBox,    // Index 3
                priorityComboBox,    // Index 4
                deadlinePicker,
                buttonsBox
        );

        root.setBottom(formBox);
    }

    /**
     * Εμφανίζει το παράθυρο διαλόγου για την επεξεργασία μιας εργασίας.
     *
     * @param task Η εργασία που θα επεξεργαστεί.
     */
    private void showEditTaskDialog(Task task) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Modify the task details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create Fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(task.getTitle());
        TextField descriptionField = new TextField(task.getDescription());
        ComboBox<String> categoryComboBox = new ComboBox<>(FXCollections.observableArrayList(categories));
        categoryComboBox.setValue(task.getCategory());
        ComboBox<String> priorityComboBox = new ComboBox<>(FXCollections.observableArrayList(priorities));
        priorityComboBox.setValue(task.getPriority());
        DatePicker deadlinePicker = new DatePicker(task.getDeadline());
        ComboBox<String> statusComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Open", "In Progress", "Postponed", "Completed", "Delayed"));
        statusComboBox.setValue(task.getStatus());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryComboBox, 1, 2);
        grid.add(new Label("Priority:"), 0, 3);
        grid.add(priorityComboBox, 1, 3);
        grid.add(new Label("Deadline:"), 0, 4);
        grid.add(deadlinePicker, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusComboBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable save button based on input
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(false);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String title = titleField.getText().trim();
                String description = descriptionField.getText().trim();
                String category = categoryComboBox.getValue();
                String priority = priorityComboBox.getValue();
                LocalDate deadline = deadlinePicker.getValue();
                String status = statusComboBox.getValue();

                if (title.isEmpty() || category == null || priority == null || deadline == null || status == null) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter all required fields.");
                    return null;
                }

                // Update task fields
                task.setTitle(title);
                task.setDescription(description);
                task.setCategory(category);
                task.setPriority(priority);
                task.setDeadline(deadline);
                task.setStatus(status);

                // If status is set to "Completed", clear all reminders
                if ("Completed".equals(status)) {
                    task.clearReminders();
                }

                return task;
            }
            return null;
        });

        Optional<Task> result = dialog.showAndWait();

        result.ifPresent(updatedTask -> {
            taskTable.refresh();
            updateTopInfo();
        });
    }

    /**
     * Διαγράφει μια εργασία μετά από επιβεβαίωση από τον χρήστη.
     *
     * @param task Η εργασία που θα διαγραφεί.
     */
    private void deleteTask(Task task) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Task");
        alert.setHeaderText("Are you sure you want to delete the task: " + task.getTitle() + "?");
        alert.setContentText("This will also delete all associated reminders.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            observableTaskList.remove(task);
            taskTable.refresh();
            updateTopInfo();
        }
    }

    /**
     * Εμφανίζει το παράθυρο διαλόγου για τη διαχείριση υπενθυμίσεων μιας εργασίας.
     *
     * @param task Η εργασία για την οποία θα διαχειριστούμε τις υπενθυμίσεις.
     */
    private void showManageRemindersDialog(Task task) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Reminders for: " + task.getTitle());
        dialog.setHeaderText("Manage Reminders");

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Table of Reminders
        TableView<Reminder> reminderTable = new TableView<>();
        ObservableList<Reminder> reminderList = FXCollections.observableArrayList(task.getReminders());
        reminderTable.setItems(reminderList);

        TableColumn<Reminder, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setPrefWidth(150);

        TableColumn<Reminder, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(150);

        // Action Column
        TableColumn<Reminder, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(150);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(e -> {
                    Reminder reminder = getTableView().getItems().get(getIndex());
                    showEditReminderDialog(task, reminder, reminderList, reminderTable);
                });

                deleteButton.setOnAction(e -> {
                    Reminder reminder = getTableView().getItems().get(getIndex());
                    task.removeReminder(reminder);
                    reminderList.remove(reminder);
                    reminderTable.refresh();
                    updateTopInfo();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        reminderTable.getColumns().addAll(typeColumn, dateColumn, actionCol);

        // Add Reminder Button
        Button addReminderButton = new Button("Add Reminder");
        addReminderButton.setOnAction(e -> showAddReminderDialog(task, reminderList, reminderTable));

        content.getChildren().addAll(reminderTable, addReminderButton);
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait();
    }

    /**
     * Εμφανίζει το παράθυρο διαλόγου για την προσθήκη νέας υπενθύμισης.
     *
     * @param task          Η εργασία για την οποία θα προσθέσουμε υπενθύμιση.
     * @param reminderList  Η λίστα υπενθυμίσεων της εργασίας.
     * @param reminderTable Ο πίνακας υπενθυμίσεων.
     */
    private void showAddReminderDialog(Task task, ObservableList<Reminder> reminderList, TableView<Reminder> reminderTable) {
        Dialog<Reminder> dialog = new Dialog<>();
        dialog.setTitle("Add Reminder");
        dialog.setHeaderText("Set a New Reminder for: " + task.getTitle());

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Create Fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> typeComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "1 day before", "1 week before", "1 month before", "Custom Date"));
        typeComboBox.setPromptText("Type");

        DatePicker customDatePicker = new DatePicker();
        customDatePicker.setPromptText("Select Date");
        customDatePicker.setDisable(true);

        typeComboBox.setOnAction(e -> {
            String selected = typeComboBox.getValue();
            if ("Custom Date".equals(selected)) {
                customDatePicker.setDisable(false);
            } else {
                customDatePicker.setDisable(true);
                customDatePicker.setValue(null);
            }
        });

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeComboBox, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(customDatePicker, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable add button based on input
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        // Validation listeners
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if ("Custom Date".equals(newVal)) {
                    addButton.setDisable(customDatePicker.getValue() == null);
                } else {
                    addButton.setDisable(false);
                }
            } else {
                addButton.setDisable(true);
            }
        });
        customDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Custom Date".equals(typeComboBox.getValue())) {
                addButton.setDisable(newVal == null);
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String type = typeComboBox.getValue();
                LocalDate date = null;
                if ("Custom Date".equals(type)) {
                    date = customDatePicker.getValue();
                    if (date == null) {
                        showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a date.");
                        return null;
                    }
                    if (date.isAfter(task.getDeadline())) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Date", "Reminder date cannot be after the task deadline.");
                        return null;
                    }
                } else {
                    switch (type) {
                        case "1 day before":
                            date = task.getDeadline().minusDays(1);
                            break;
                        case "1 week before":
                            date = task.getDeadline().minusWeeks(1);
                            break;
                        case "1 month before":
                            date = task.getDeadline().minusMonths(1);
                            break;
                    }
                    if (date.isBefore(LocalDate.now())) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Reminder", "The reminder date has already passed.");
                        return null;
                    }
                }
                return new Reminder(type, date);
            }
            return null;
        });

        Optional<Reminder> result = dialog.showAndWait();
        result.ifPresent(reminder -> {
            task.addReminder(reminder);
            reminderList.add(reminder);
            reminderTable.refresh();
            taskTable.refresh();
            updateTopInfo();
        });
    }

    /**
     * Εμφανίζει το παράθυρο διαλόγου για την επεξεργασία μιας υπενθύμισης.
     *
     * @param task          Η εργασία που περιέχει την υπενθύμιση.
     * @param reminder      Η υπενθύμιση που θα επεξεργαστεί.
     * @param reminderList  Η λίστα υπενθυμίσεων της εργασίας.
     * @param reminderTable Ο πίνακας υπενθυμίσεων.
     */
    private void showEditReminderDialog(Task task, Reminder reminder, ObservableList<Reminder> reminderList, TableView<Reminder> reminderTable) {
        Dialog<Reminder> dialog = new Dialog<>();
        dialog.setTitle("Edit Reminder");
        dialog.setHeaderText("Modify Reminder for: " + task.getTitle());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create Fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> typeComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "1 day before", "1 week before", "1 month before", "Custom Date"));
        typeComboBox.setValue(reminder.getType());

        DatePicker customDatePicker = new DatePicker(reminder.getDate());
        customDatePicker.setPromptText("Select Date");
        customDatePicker.setDisable(!"Custom Date".equals(reminder.getType()));

        typeComboBox.setOnAction(e -> {
            String selected = typeComboBox.getValue();
            if ("Custom Date".equals(selected)) {
                customDatePicker.setDisable(false);
            } else {
                customDatePicker.setDisable(true);
                customDatePicker.setValue(null);
            }
        });

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeComboBox, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(customDatePicker, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable save button based on input
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(false);

        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if ("Custom Date".equals(newVal)) {
                    saveButton.setDisable(customDatePicker.getValue() == null);
                } else {
                    saveButton.setDisable(false);
                }
            } else {
                saveButton.setDisable(true);
            }
        });
        customDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Custom Date".equals(typeComboBox.getValue())) {
                saveButton.setDisable(newVal == null);
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String type = typeComboBox.getValue();
                LocalDate date = null;
                if ("Custom Date".equals(type)) {
                    date = customDatePicker.getValue();
                    if (date == null) {
                        showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a date.");
                        return null;
                    }
                    if (date.isAfter(task.getDeadline())) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Date", "Reminder date cannot be after the task deadline.");
                        return null;
                    }
                } else {
                    switch (type) {
                        case "1 day before":
                            date = task.getDeadline().minusDays(1);
                            break;
                        case "1 week before":
                            date = task.getDeadline().minusWeeks(1);
                            break;
                        case "1 month before":
                            date = task.getDeadline().minusMonths(1);
                            break;
                    }
                    if (date.isBefore(LocalDate.now())) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Reminder", "The reminder date has already passed.");
                        return null;
                    }
                }
                reminder.setType(type);
                reminder.setDate(date);
                return reminder;
            }
            return null;
        });

        Optional<Reminder> result = dialog.showAndWait();
        result.ifPresent(updatedReminder -> {
            reminderTable.refresh();
            taskTable.refresh();
            updateTopInfo();
        });
    }

    /**
     * Εμφανίζει το παράθυρο διαλόγου για την προσθήκη νέας κατηγορίας.
     */
    private void showAddCategoryDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Category");
        dialog.setHeaderText("Add a New Category");
        dialog.setContentText("Category Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Category name cannot be empty.");
                return;
            }
            if (categories.contains(name)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Category already exists.");
                return;
            }
            // Add the new category only once (categories and dataModel.getCategories() reference the same list)
            categories.add(name);
            updateCategoryComboBoxes();
            updateFilterComboBoxes();
            taskTable.refresh();
            updateTopInfo();
        });
    }

    /**
     * Εμφανίζει το παράθυρο διαλόγου για την επεξεργασία μιας κατηγορίας.
     */
    private void showEditCategoryDialog() {
        if (categories.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No categories available to edit.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(categories.get(0), categories);
        dialog.setTitle("Edit Category");
        dialog.setHeaderText("Select Category to Edit");
        dialog.setContentText("Category:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedCategory -> {
            TextInputDialog editDialog = new TextInputDialog(selectedCategory);
            editDialog.setTitle("Edit Category");
            editDialog.setHeaderText("Modify Category Name");
            editDialog.setContentText("New Name:");

            Optional<String> newName = editDialog.showAndWait();
            newName.ifPresent(name -> {
                if (name.trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Category name cannot be empty.");
                    return;
                }
                if (categories.contains(name)) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Category already exists.");
                    return;
                }
                int index = categories.indexOf(selectedCategory);
                categories.set(index, name);

                for (Task task : taskList) {
                    if (task.getCategory().equals(selectedCategory)) {
                        task.setCategory(name);
                    }
                }
                taskTable.refresh();
                updateCategoryComboBoxes();
                updateFilterComboBoxes();
                updateTopInfo();
            });
        });
    }

    /**
     * Εμφανίζει το παράθυρο διαλόγου για τη διαγραφή μιας κατηγορίας.
     */
    private void showDeleteCategoryDialog() {
        if (categories.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No categories available to delete.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(categories.get(0), categories);
        dialog.setTitle("Delete Category");
        dialog.setHeaderText("Select Category to Delete");
        dialog.setContentText("Category:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedCategory -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Category");
            confirmation.setHeaderText("Are you sure you want to delete the category: " + selectedCategory + "?");
            confirmation.setContentText("This will also delete all tasks under this category and their reminders.");

            Optional<ButtonType> confirmResult = confirmation.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                categories.remove(selectedCategory);

                taskList.removeIf(task -> task.getCategory().equals(selectedCategory));
                taskTable.refresh();
                updateCategoryComboBoxes();
                updateFilterComboBoxes();
                updateTopInfo();
            }
        });
    }

    /**
     * Εμφανίζει το παράθυρο διαλόγου για την προσθήκη νέας προτεραιότητας.
     */
    private void showAddPriorityDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Priority");
        dialog.setHeaderText("Add a New Priority");
        dialog.setContentText("Priority Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Priority name cannot be empty.");
                return;
            }
            if (priorities.contains(name)) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Priority already exists.");
                return;
            }
            // Add the new priority only once.
            priorities.add(name);
            updatePriorityComboBoxes();
            updateFilterComboBoxes();
            taskTable.refresh();
            updateTopInfo();
        });
    }

    /**
     * Εμφανίζει το παράθυρο διαλόγου για την επεξεργασία μιας προτεραιότητας.
     */
    private void showEditPriorityDialog() {
        if (priorities.size() <= 1) { // Only "Default" exists
            showAlert(Alert.AlertType.ERROR, "Error", "No priorities available to edit.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, priorities);
        dialog.setTitle("Edit Priority");
        dialog.setHeaderText("Select Priority to Edit");
        dialog.setContentText("Priority:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedPriority -> {
            if ("Default".equals(selectedPriority)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Cannot edit the default priority.");
                return;
            }
            TextInputDialog editDialog = new TextInputDialog(selectedPriority);
            editDialog.setTitle("Edit Priority");
            editDialog.setHeaderText("Modify Priority Name");
            editDialog.setContentText("New Name:");

            Optional<String> newName = editDialog.showAndWait();
            newName.ifPresent(name -> {
                if (name.trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Priority name cannot be empty.");
                    return;
                }
                if (priorities.contains(name)) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Priority already exists.");
                    return;
                }
                int index = priorities.indexOf(selectedPriority);
                priorities.set(index, name);

                for (Task task : taskList) {
                    if (task.getPriority().equals(selectedPriority)) {
                        task.setPriority(name);
                    }
                }
                taskTable.refresh();
                updatePriorityComboBoxes();
                updateFilterComboBoxes();
                updateTopInfo();
            });
        });
    }

    /**
     * Εμφανίζει το παράθυρο διαλόγου για τη διαγραφή μιας προτεραιότητας.
     */
    private void showDeletePriorityDialog() {
        if (priorities.size() <= 1) { // Only "Default" exists
            showAlert(Alert.AlertType.ERROR, "Error", "No priorities available to delete.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, priorities);
        dialog.setTitle("Delete Priority");
        dialog.setHeaderText("Select Priority to Delete");
        dialog.setContentText("Priority:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedPriority -> {
            if ("Default".equals(selectedPriority)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Cannot delete the default priority.");
                return;
            }
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Priority");
            confirmation.setHeaderText("Are you sure you want to delete the priority: " + selectedPriority + "?");
            confirmation.setContentText("All tasks with this priority will be set to 'Default'.");

            Optional<ButtonType> confirmResult = confirmation.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                priorities.remove(selectedPriority);

                for (Task task : taskList) {
                    if (task.getPriority().equals(selectedPriority)) {
                        task.setPriority("Default");
                    }
                }
                taskTable.refresh();
                updatePriorityComboBoxes();
                updateFilterComboBoxes();
                updateTopInfo();
            }
        });
    }

    /**
     * Ενημερώνει τα ComboBoxes των κατηγοριών στην φόρμα προσθήκης εργασίας.
     */
    private void updateCategoryComboBoxes() {
        // In the Add Task form, the category ComboBox is at index 3.
        VBox formBox = (VBox) root.getBottom();
        if (formBox.getChildren().size() >= 7) {
            ComboBox<String> categoryComboBox = (ComboBox<String>) formBox.getChildren().get(3);
            categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        }
    }

    /**
     * Ενημερώνει τα ComboBoxes των προτεραιοτήτων στην φόρμα προσθήκης εργασίας.
     */
    private void updatePriorityComboBoxes() {
        // In the Add Task form, the priority ComboBox is at index 4.
        VBox formBox = (VBox) root.getBottom();
        if (formBox.getChildren().size() >= 7) {
            ComboBox<String> priorityComboBox = (ComboBox<String>) formBox.getChildren().get(4);
            priorityComboBox.setItems(FXCollections.observableArrayList(priorities));
        }
    }

    /**
     * Ενημερώνει τα ComboBoxes των φίλτρων αναζήτησης.
     */
    private void updateFilterComboBoxes() {
        VBox topBox = (VBox) root.getTop();
        if (topBox.getChildren().size() >= 2) {
            HBox searchBox = (HBox) topBox.getChildren().get(1);
            ComboBox<String> categoryFilter = (ComboBox<String>) searchBox.getChildren().get(3);
            ComboBox<String> priorityFilter = (ComboBox<String>) searchBox.getChildren().get(5);

            List<String> categoryItems = new ArrayList<>();
            categoryItems.add("All");
            categoryItems.addAll(categories);
            categoryFilter.setItems(FXCollections.observableArrayList(categoryItems));
            categoryFilter.setValue("All");

            List<String> priorityItems = new ArrayList<>();
            priorityItems.add("All");
            priorityItems.addAll(priorities);
            priorityFilter.setItems(FXCollections.observableArrayList(priorityItems));
            priorityFilter.setValue("All");
        }
    }

    /**
     * Εμφανίζει το παράθυρο διαλόγου "About".
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About MediaLab");
        alert.setHeaderText("MediaLab Assistant");
        alert.setContentText("MediaLab Assistant\nVersion 1.0\nDeveloped by Your Name");
        alert.showAndWait();
    }

    /**
     * Εμφανίζει ένα alert με το συγκεκριμένο μήνυμα.
     *
     * @param type    Ο τύπος του alert.
     * @param title   Ο τίτλος του alert.
     * @param message Το μήνυμα του alert.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
