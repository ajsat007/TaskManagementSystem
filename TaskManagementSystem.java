import java.sql.*;
import java.util.Scanner;

public class TaskManagementSystem {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3308/task_manager";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "ROOT";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            createTableIfNotExists(connection);

            Scanner sc = new Scanner(System.in);
            int choice;

            do {
                System.out.println("1. Add Task");
                System.out.println("2. Update Task");
                System.out.println("3. View Tasks");
                System.out.println("4. Delete Task");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        addTask(connection, sc);
                        break;
                    case 2:
                        updateTask(connection, sc);
                        break;
                    case 3:
                        viewTasks(connection);
                        break;
                    case 4:
                        deleteTask(connection, sc);
                        break;
                    case 0:
                        System.out.println("Exiting Task Management System");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

            } while (choice != 0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableIfNotExists(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS tasks (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "task_name VARCHAR(255) NOT NULL," +
                "deadline DATE" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        }
    }

    private static void addTask(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Task Name: ");
        String taskName = scanner.next();
        System.out.print("Enter Deadline (YYYY-MM-DD): ");
        String deadlineInput = scanner.next();

        Date deadline;
        try {
            deadline = Date.valueOf(deadlineInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please enter the date in the format YYYY-MM-DD.");
            return;
        }

        String insertSQL = "INSERT INTO tasks (task_name, deadline) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, taskName);
            preparedStatement.setDate(2, deadline);
            preparedStatement.executeUpdate();
            System.out.println("Task added successfully!");
        }
    }

    private static void updateTask(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Task ID to Update: ");
        int taskId = scanner.nextInt();
        System.out.print("Enter New Task Name: ");
        String newTaskName = scanner.next();
        System.out.print("Enter New Deadline (YYYY-MM-DD): ");

        String newDeadlineInput = scanner.next();

        // Convert input string to java.sql.Date
        Date newDeadline;
        try {
            newDeadline = Date.valueOf(newDeadlineInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please enter the date in the format YYYY-MM-DD.");
            return;
        }

        String updateSQL = "UPDATE tasks SET task_name = ?, deadline = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, newTaskName);
            preparedStatement.setDate(2, newDeadline);
            preparedStatement.setInt(3, taskId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Task updated successfully!");
            } else {
                System.out.println("No task found with the given ID.");
            }
        }
    }

    private static void viewTasks(Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM tasks";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String taskName = resultSet.getString("task_name");
                Date deadline = resultSet.getDate("deadline");
                System.out.println("Task ID: " + id + ", Task Name: " + taskName + ", Deadline: " + deadline);
            }
        }
    }

    private static void deleteTask(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Task ID to Delete: ");
        int taskId = scanner.nextInt();

        String deleteSQL = "DELETE FROM tasks WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, taskId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Task deleted successfully!");
            } else {
                System.out.println("No task found with the given ID.");
            }
        }
    }
}
