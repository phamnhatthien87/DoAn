package Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDB {

    public static Connection getConnection() {

        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            String url =
                    "jdbc:sqlserver://localhost:1433;" +
                            "databaseName=quanlythuvien;" +
                            "encrypt=true;" +
                            "trustServerCertificate=true";

            String user = "sa";
            String password = "123456789";

            Connection conn =
                    DriverManager.getConnection(url, user, password);

//            System.out.println("Kết nối thành công!");

            return conn;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
