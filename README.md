# Rental Home Robot Database System

### What is SQLite JDBC?

**SQLite JDBC** is a JDBC driver: a library that lets a Java program connect to a **SQLite** database file using the standard JDBC API (`DriverManager`, `Connection`, `PreparedStatement`, etc.). 

### Run (PowerShell)
Place `sqlite-jdbc-3.32.3.2.jar` in the `lib` folder (see `.vscode/settings.json` if you use VS Code for referenced libraries).

**Compile:**
```powershell
javac -encoding UTF-8 -cp "lib/sqlite-jdbc-3.32.3.2.jar" -d out src/model/*.java src/db/*.java src/Main.java
```

**Run:**
```powershell
java -cp "out;lib/sqlite-jdbc-3.32.3.2.jar" Main
```