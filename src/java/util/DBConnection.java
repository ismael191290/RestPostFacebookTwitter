package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBConnection {

    private static String ipAddress; 
    private static String dbName;
    private static String user;
    private static String password;
    private static String service;
    private static ResourceBundle properties;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(properties == null){
            properties = ResourceBundle.getBundle("propiedades");
            ipAddress = properties.getString("ipAddress");
            dbName = properties.getString("dbName");
            user = properties.getString("user");
            password = properties.getString("password");
            service = properties.getString("service");
        }
        String url = "jdbc:sqlserver://" + ipAddress + ":" + service + ";databaseName=" + dbName;
        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) throws SQLException, InterruptedException {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            System.out.println("Conexión Exitosa");
        }else{
            System.out.println("Error de Conexión");
        }
    }
}