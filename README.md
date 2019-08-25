## Development
### Prerequisites

- JDK 11
- Maven
- Git

### IDE

Use Intellij IDEA with:
- [google-java-format](https://github.com/google/google-java-format) (Use Ctrl-Alt-L to auto-format)
- maven
Use the "Open" option on the welcome screen to open the project.

## Exporting "site-interne"-ready CSVs:

- Place all your CSVs in one directory (preferrably containing nothing else)
- Open project in IntelliJ, install maven dependencies
- Run Synchronizer.class and then enter aforementionned directory's path
- Confirm export
- Carefully check your exported files for inconsistencies & errors
- Trace back and correct errors' origin
- Repeat until all CSV clean and no duplicates in task summary file (Récapitulatif des tâches)
