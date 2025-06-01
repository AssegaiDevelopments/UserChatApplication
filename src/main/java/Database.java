import constants.DatabaseConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static Connection getConnection() throws SQLException {
        String url = DatabaseConstants.DB_URL;
        String user = DatabaseConstants.DB_USER;
        String password = DatabaseConstants.DB_PASSWORD;
        return DriverManager.getConnection(url, user, password);
    }
}
