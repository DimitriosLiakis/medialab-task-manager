# Media Application

This is a Java-based media application utilizing **JavaFX** for the graphical user interface and **Gson** for JSON parsing.

## Prerequisites
Ensure you have the following installed on your system:

- **JDK 11+** (Ensure it supports JavaFX)
- **JavaFX SDK** (Tested with /opt/javafx-sdk)
- **Gson Library** (Version 2.8.9)

## Project Structure
```
media/
├── src/
│   ├── media/
│   │   ├── Main.java
│   │   ├── Controller.java
│   │   ├── Model.java
│   ├── module-info.java
├── mods/
│   ├── gson-2.8.9.jar
├── bin/
│   ├── (Compiled .class files)
└── README.md
```

## Building and Running

### Compilation
Run the following command to compile the project:
```sh
javac --module-path /opt/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp mods/gson-2.8.9.jar -d bin $(find src -name "*.java")
```

### Execution
Run the compiled Java application with:
```sh
java --module-path /opt/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp "mods/gson-2.8.9.jar:bin" media.Main
```

## Dependencies
- **JavaFX**: For GUI components.
- **Gson**: For JSON parsing.
