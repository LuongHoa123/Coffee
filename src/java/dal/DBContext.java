package dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBContext {

    private final ResourceBundle bundle;

    public DBContext() {
        // Initialize the ResourceBundle
        this.bundle = ResourceBundle.getBundle("applications");
    }

    public Connection getConnection() {
        try {
            // Load MySQL JDBC driver
            Class.forName(bundle.getString("drivername"));

            // Database connection details
            String url = bundle.getString("url");
            String username = bundle.getString("username");
            String password = bundle.getString("password");

            // Get the connection
            Connection connection = DriverManager.getConnection(url, username, password);
            return connection;
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Error: " + ex.getMessage());

        }
        return null;
    }

    public static void main(String[] args) {
        DBContext db = new DBContext();
        Connection conn = db.getConnection();
        if (conn != null) {
            try {
                System.out.println("Connection established successfully.");
                System.out.println("Catalog: " + conn.getCatalog());
                conn.close(); // Always close the connection
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to establish connection.");
        }
    }

}
