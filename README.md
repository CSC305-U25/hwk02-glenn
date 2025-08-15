[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/H_BeDOr7)

# Lab 03 - File Grid Display

**Authors:** Glenn Anciado, Oscar Chau  
**Version:** 3.0

## Description

This Java application creates a visual grid display of files from a user-selected directory. The program uses a GUI file chooser to let users select a folder, then displays all files in that folder arranged in an optimal grid layout using Java Swing.

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
2. A file chooser dialog will appear
3. Select a directory containing files
4. The application will:
   - Count the files in the directory
   - Calculate the optimal grid layout
   - Display the files in a GUI grid