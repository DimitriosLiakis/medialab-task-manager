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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Η κλάση MainUI διαχειρίζεται την γραφική διεπαφή της εφαρμογής MediaLab Assistant.
 * Παρέχει λειτουργίες για τη διαχείριση εργασιών, κατηγοριών, προτεραιοτήτων και υπενθυμίσεων.
 */
public class MainUI {
    private BorderPane root;
    private DataModel dataModel;
    private List<Task> taskList;
    private List<String> categories;
    private List<String> priorities;

    private TableView<Task> taskTable;
    private ObservableList<Task> observableTaskList;
    private FilteredList<Task> filteredData; // FilteredList για αναζήτηση

    // Ετικέτες πληροφορίας στην κορυφή
    private Label totalTasksLabel;
    private Label completedTasksLabel;
    private Label delayedTasksLabel;
    private Label upcomingDeadlinesLabel;
    
    // Field for the search box (to update its filters easily)
    private HBox searchBox;
    
    // Active Reminders Panel components
    private TableView<ActiveReminder> activeRemindersTable;
    private Label activeRemindersTitleLabel; // Shows "Active Reminders: X"
    private Label earliestReminderLabel;     // Shows the next expiring reminder

    // Inner class to represent active reminders in aggregated view.
    public static class ActiveReminder {
        private final String taskTitle;
        private final String reminderType;
        private final LocalDate reminderDate;

        public ActiveReminder(String taskTitle, String reminderType, LocalDate reminderDate) {
            this.taskTitle = taskTitle;
            this.reminderType = reminderType;
            this.reminderDate = reminderDate;
        }

        public String getTaskTitle() {
            return taskTitle;
        }

        public String getReminderType() {
            return reminderType;
        }

        public LocalDate getReminderDate() {
            return reminderDate;
        }
    }

    public MainUI(BorderPane root, DataModel dataModel) {
        this.root = root;
        this.dataModel = dataModel;
        this.taskList = dataModel.getTasks();
        this.categories = dataModel.getCategories();
        this.priorities = dataModel.getPriorities();
    }

    public void initUI() {
        setupMenuBar();
        setupTopInfo();
        setupSearchBar();
        setupTaskTable();
        setupAddTaskForm();
        setupActiveRemindersPanel(); // Set up the active reminders panel on the right side
        updateTopInfo();
    }

    private void setupMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        // Modified Exit menu item:
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
            // Save the current state to the JSON file before exiting.
            StorageManager.saveData(dataModel);
            System.exit(0);
        });
        fileMenu.getItems().add(exitItem);

        // Manage Menu for Categories and Priorities
        Menu manageMenu = new Menu("Manage");

        Menu manageCategories = new Menu("Categories");
        MenuItem addCategory = new MenuItem("Add Category");
        addCategory.setOnAction(e -> showAddCategoryDialog());
        MenuItem editCategory = new MenuItem("Edit Category");
        editCategory.setOnAction(e -> showEditCategoryDialog());
        MenuItem deleteCategory = new MenuItem("Delete Category");
        deleteCategory.setOnAction(e -> showDeleteCategoryDialog());
        manageCategories.getItems().addAll(addCategory, editCategory, deleteCategory);

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

    private void setupSearchBar() {
        searchBox = new HBox(10);
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
            filteredData.setPredicate(task -> true);
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

    private void setupTaskTable() {
        taskTable = new TableView<>();
        observableTaskList = FXCollections.observableList(taskList);
        filteredData = new FilteredList<>(observableTaskList, p -> true);
        taskTable.setItems(filteredData);

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

    private void setupAddTaskForm() {
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(10));
        formBox.setStyle("-fx-background-color: #f9f9f9;");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");

        // ComboBox για κατηγορία με custom button cell ώστε να εμφανίζεται το prompt text όταν δεν υπάρχει επιλογή
        ComboBox<String> categoryComboBox = new ComboBox<>(FXCollections.observableArrayList(categories));
        categoryComboBox.setPromptText("Category");
        categoryComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(categoryComboBox.getPromptText());
                } else {
                    setText(item);
                }
            }
        });

        // ComboBox για προτεραιότητα με custom button cell ώστε να εμφανίζεται το prompt text όταν δεν υπάρχει επιλογή
        ComboBox<String> priorityComboBox = new ComboBox<>(FXCollections.observableArrayList(priorities));
        priorityComboBox.setPromptText("Priority");
        priorityComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(priorityComboBox.getPromptText());
                } else {
                    setText(item);
                }
            }
        });

        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Deadline (YYYY-MM-DD)");

        // Κατάσταση "Open" για νέα tasks (επιλογή απενεργοποιημένη)
        ComboBox<String> statusComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "Open", "In Progress", "Postponed", "Completed", "Delayed"));
        statusComboBox.setValue("Open");
        statusComboBox.setDisable(true);

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
            observableTaskList.add(newTask);

            titleField.clear();
            descriptionField.clear();
            categoryComboBox.setValue(null);
            priorityComboBox.setValue(null);
            deadlinePicker.setValue(null);

            updateTopInfo();
            updateFilterComboBoxes(); // Ενημέρωση των φίλτρων αναζήτησης
        });

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

    private void showEditTaskDialog(Task task) {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Edit Task");
        dialog.setHeaderText("Modify the task details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

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

                task.setTitle(title);
                task.setDescription(description);
                task.setCategory(category);
                task.setPriority(priority);
                task.setDeadline(deadline);
                task.setStatus(status);

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
            refreshActiveReminders(); // Update active reminders
        }
    }

    private void showManageRemindersDialog(Task task) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Reminders for: " + task.getTitle());
        dialog.setHeaderText("Manage Reminders");

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        TableView<Reminder> reminderTable = new TableView<>();
        ObservableList<Reminder> reminderList = FXCollections.observableArrayList(task.getReminders());
        reminderTable.setItems(reminderList);

        TableColumn<Reminder, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setPrefWidth(150);

        TableColumn<Reminder, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(150);

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
                    refreshActiveReminders();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        reminderTable.getColumns().addAll(typeColumn, dateColumn, actionCol);

        Button addReminderButton = new Button("Add Reminder");
        if ("Completed".equals(task.getStatus())) {
            addReminderButton.setDisable(true);
            addReminderButton.setText("Task Completed");
        } else {
            addReminderButton.setOnAction(e -> showAddReminderDialog(task, reminderList, reminderTable));
        }

        content.getChildren().addAll(reminderTable, addReminderButton);
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait();
    }

    private void showAddReminderDialog(Task task, ObservableList<Reminder> reminderList, TableView<Reminder> reminderTable) {
        Dialog<Reminder> dialog = new Dialog<>();
        dialog.setTitle("Add Reminder");
        dialog.setHeaderText("Set a New Reminder for: " + task.getTitle());

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

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

        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

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
            refreshActiveReminders();
        });
    }

    private void showEditReminderDialog(Task task, Reminder reminder, ObservableList<Reminder> reminderList, TableView<Reminder> reminderTable) {
        Dialog<Reminder> dialog = new Dialog<>();
        dialog.setTitle("Edit Reminder");
        dialog.setHeaderText("Modify Reminder for: " + task.getTitle());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

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
            refreshActiveReminders();
        });
    }

    // Methods for managing categories
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
            categories.add(name);
            updateCategoryComboBoxes();
            updateFilterComboBoxes(); // Update search filters when a new category is added
            taskTable.refresh();
            updateTopInfo();
        });
    }

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
                refreshActiveReminders();
            }
        });
    }

    // Methods for managing priorities
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
            priorities.add(name);
            updatePriorityComboBoxes();
            updateFilterComboBoxes(); // Update search filters when a new priority is added
            taskTable.refresh();
            updateTopInfo();
        });
    }

    private void showEditPriorityDialog() {
        if (priorities.size() <= 1) {
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

    private void showDeletePriorityDialog() {
        if (priorities.size() <= 1) {
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

    // Methods for updating ComboBoxes in the Add Task form
    private void updateCategoryComboBoxes() {
        VBox formBox = (VBox) root.getBottom();
        if (formBox.getChildren().size() >= 7) {
            // Το ComboBox της κατηγορίας βρίσκεται στο index 3.
            ComboBox<String> categoryComboBox = (ComboBox<String>) formBox.getChildren().get(3);
            categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        }
    }

    private void updatePriorityComboBoxes() {
        VBox formBox = (VBox) root.getBottom();
        if (formBox.getChildren().size() >= 7) {
            // Το ComboBox της προτεραιότητας βρίσκεται στο index 4.
            ComboBox<String> priorityComboBox = (ComboBox<String>) formBox.getChildren().get(4);
            priorityComboBox.setItems(FXCollections.observableArrayList(priorities));
        }
    }

    // Method for updating the search filter ComboBoxes
    private void updateFilterComboBoxes() {
        if (searchBox != null) {
            // Στο searchBox η σειρά των στοιχείων είναι:
            // 0: Label "Search:"
            // 1: searchField
            // 2: Label "Category:"
            // 3: categoryFilter
            // 4: Label "Priority:"
            // 5: priorityFilter
            // 6: searchButton
            // 7: clearButton
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

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About MediaLab");
        alert.setHeaderText("MediaLab Assistant");
        alert.setContentText("MediaLab Assistant\nVersion 1.0\nDeveloped by Dimitrios Liakis");
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // --- Active Reminders Panel methods ---
    
    /**
     * Sets up the active reminders panel on the right side of the UI.
     */
    private void setupActiveRemindersPanel() {
        activeRemindersTable = new TableView<>();
        // Create labels for the title (with count) and the next expiring reminder.
        activeRemindersTitleLabel = new Label("Active Reminders: 0");
        earliestReminderLabel = new Label("Next Expiring: None");
        
        refreshActiveReminders(); // Update table and labels
        
        TableColumn<ActiveReminder, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("taskTitle"));
        
        TableColumn<ActiveReminder, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("reminderType"));
        
        TableColumn<ActiveReminder, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("reminderDate"));
        
        activeRemindersTable.getColumns().addAll(taskCol, typeCol, dateCol);
        
        VBox remindersPanel = new VBox(10, activeRemindersTitleLabel, earliestReminderLabel, activeRemindersTable);
        remindersPanel.setPadding(new Insets(10));
        remindersPanel.setPrefWidth(400); // Increased width for more space
        root.setRight(remindersPanel);
    }
    
    /**
     * Refreshes the active reminders list by scanning all tasks and including
     * only those reminders whose date is today or in the future.
     * Also updates the title with count and shows the next expiring reminder.
     */
    private void refreshActiveReminders() {
        ObservableList<ActiveReminder> updatedList = FXCollections.observableArrayList();
        LocalDate today = LocalDate.now();
        for (Task task : taskList) {
            for (Reminder reminder : task.getReminders()) {
                if (!reminder.getDate().isBefore(today)) {
                    updatedList.add(new ActiveReminder(task.getTitle(), reminder.getType(), reminder.getDate()));
                }
            }
        }
        activeRemindersTable.setItems(updatedList);
        // Update the active reminders title with count
        activeRemindersTitleLabel.setText("Active Reminders: " + updatedList.size());
        
        // Determine the next expiring reminder, if any
        Optional<ActiveReminder> nextExpiring = updatedList.stream()
                .min(Comparator.comparing(ActiveReminder::getReminderDate));
        if (nextExpiring.isPresent()) {
            ActiveReminder next = nextExpiring.get();
            earliestReminderLabel.setText("Next Expiring: " + next.getTaskTitle() + " on " + next.getReminderDate());
        } else {
            earliestReminderLabel.setText("Next Expiring: None");
        }
    }
}
