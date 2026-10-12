# MediaLab Assistant: Task Manager

A desktop task-management application built with **Java** and **JavaFX**. You can create and organize tasks by category and priority, track deadlines and status, and set reminders. Everything is saved to a local JSON file between sessions.

Individual project for **Multimedia Technology 2024–25** at the School of Electrical & Computer Engineering, National Technical University of Athens (NTUA).

![Java](https://img.shields.io/badge/Java-11%2B-ED8B00?logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-GUI-1572B6)
![Gson](https://img.shields.io/badge/Gson-2.8.9-4285F4)

---

## Features

- **Task management:** create, edit and delete tasks with a title, description, category, priority and deadline.
- **Status tracking:** `Open`, `In Progress`, `Postponed` and `Completed`. When the app starts, any unfinished task whose deadline has passed becomes **`Delayed`**, and you get a warning about it.
- **Dashboard:** live counters for total, completed and delayed tasks, and for deadlines in the next 7 days.
- **Search & filtering:** search by title and filter by category and priority.
- **Custom categories & priorities:** add, rename or delete them. Renames apply to existing tasks. Deleting a category removes its tasks and their reminders, after a confirmation prompt.
- **Reminders:** one day, one week or one month before the deadline, or on a custom date. A side panel lists the active reminders and highlights the next one to fire.
- **Persistence:** the whole state (tasks, reminders, categories, priorities) is saved to JSON with Gson when the app closes and loaded again on startup. It uses a custom `LocalDate` adapter.

---

## Design

| Class | Responsibility |
|---|---|
| [`Main`](media/src/media/Main.java) | JavaFX entry point: loads state, updates delayed tasks, saves on exit |
| [`MainUI`](media/src/media/MainUI.java) | The whole UI: menu bar, dashboard, search bar, task table, forms, dialogs and the reminders panel |
| [`Task`](media/src/media/Task.java), [`Reminder`](media/src/media/Reminder.java) | Domain model |
| [`DataModel`](media/src/media/DataModel.java) | Root object for serialization: tasks, categories, priorities |
| [`StorageManager`](media/src/media/StorageManager.java) | JSON load and save with Gson; creates a default state if no file exists |

The UI uses `ObservableList` / `FilteredList`, so the table, the counters and the reminders panel update immediately after every change.

---

## Running locally

Requirements: JDK 11+ and the [JavaFX SDK](https://openjfx.io/). The Gson jar is included in `media/mods/`.

```sh
cd media

# Compile (set the path to your JavaFX SDK)
javac --module-path /opt/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml \
      -cp mods/gson-2.8.9.jar -d bin $(find src -name "*.java")

# Create the data folder (the app reads and writes medialab/data.json)
mkdir -p medialab
cp assets/data.json medialab/   # optional: start with sample tasks

# Run
java --module-path /opt/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml \
     -cp "mods/gson-2.8.9.jar:bin" media.Main
```

---

*Author: Dimitrios Liakis*
