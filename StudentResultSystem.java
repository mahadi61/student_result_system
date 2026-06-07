import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class StudentResultSystem {

    // Database Configuration
    private static final String URL = "jdbc:mysql://localhost:3306/StudentDB";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {

        String[] names = { "Mahadi Hasan", "Rohim Mia", "Anik" };

        // Using LinkedHashMap ensures insertion order matches the names array index
        Map<Integer, Integer[]> studentMarksMap = new LinkedHashMap<>();
        studentMarksMap.put(101, new Integer[] { 85, 90, 78 });
        studentMarksMap.put(102, new Integer[] { 55, 60, 58 });
        studentMarksMap.put(103, new Integer[] { 92, 95, 88 });

        // Database Persistence Flow
        try (Connection dbConnection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to the database successfully!\n");

            String insertSQL = "INSERT INTO student_results (student_name, marks_sub1, marks_sub2, marks_sub3, total_marks, grade) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement insertStatement = dbConnection.prepareStatement(insertSQL)) {
                int index = 0;

                for (Map.Entry<Integer, Integer[]> entry : studentMarksMap.entrySet()) {
                    Integer[] marks = entry.getValue();
                    String name = names[index];

                    int total = marks[0] + marks[1] + marks[2];
                    double average = total / 3.0;
                    String grade;

                    if (average >= 85)
                        grade = "A";
                    else if (average >= 70)
                        grade = "B";
                    else if (average >= 50)
                        grade = "C";
                    else
                        grade = "F";

                    insertStatement.setString(1, name);
                    insertStatement.setInt(2, marks[0]);
                    insertStatement.setInt(3, marks[1]);
                    insertStatement.setInt(4, marks[2]);
                    insertStatement.setInt(5, total);
                    insertStatement.setString(6, grade);

                    insertStatement.executeUpdate();
                    index++;
                }
                System.out.println("All student records inserted successfully.");
            }

            displayAllRecords(dbConnection);

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves and renders the complete student results table from the database.
     */
    public static void displayAllRecords(Connection dbConnection) {
        String selectSQL = "SELECT * FROM student_results";
        System.out.println("\n--- Fetching Student Records from Database ---");
        System.out.printf("%-5s | %-15s | %-5s | %-5s | %-5s | %-6s | %-5s\n", "ID", "Name", "Sub1", "Sub2", "Sub3",
                "Total", "Grade");
        System.out.println("-----------------------------------------------------------------");

        try (Statement fetchStatement = dbConnection.createStatement();
                ResultSet databaseResults = fetchStatement.executeQuery(selectSQL)) {

            while (databaseResults.next()) {
                int id = databaseResults.getInt("student_id");
                String name = databaseResults.getString("student_name");
                int s1 = databaseResults.getInt("marks_sub1");
                int s2 = databaseResults.getInt("marks_sub2");
                int s3 = databaseResults.getInt("marks_sub3");
                int total = databaseResults.getInt("total_marks");
                String grade = databaseResults.getString("grade");

                System.out.printf("%-5d | %-15s | %-5d | %-5d | %-5d | %-6d | %-5s\n", id, name, s1, s2, s3, total,
                        grade);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching data: " + e.getMessage());
        }
    }
}