# 305 Final Project

**Authors:** Glenn Anciado, Oscar Chau  
**Version:** 5.0

## Description

This Java application provides a visual interface for exploring the contents of a GitHub repository or a local directory. Users can enter a GitHub folder URL, and the program will fetch and display all files in that repository. The files are shown in an optimal grid layout using Java Swing, with each file represented as a colored square based on its type and line count. The application also visualizes class relationships, generates UML diagrams, and displays software metrics. Errors and status messages are shown in a dedicated error bar at the bottom of the window.

## How to Run

### Prerequisites

- Java 17 or higher
- Maven

### Compilation and Execution

1. **Using Maven:**

   ```bash
   mvn compile exec:java -Dexec.mainClass="assignment.Driver"
   ```

## Usage

1. Run the application
2. Enter a GitHub folder URL or select a local directory when prompted
3. The application will:
   - Fetch and display files in a GUI grid
   - Visualize class relationships and UML diagrams
   - Show software metrics for the project
   - Display error messages in the bottom panel if any issues occur
