package Server;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private static PreparedStatement psGetNick;
    private static PreparedStatement psRegist;
    private static PreparedStatement psNewNick;


    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:catinchat.db");
            prepareAllStatements();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void prepareAllStatements() throws SQLException {
        psGetNick = connection.prepareStatement("SELECT nickname FROM catsinchat WHERE login = ? AND password = ?;");
        psRegist = connection.prepareStatement("INSERT INTO catsinchat (login, password, nickname) VALUES (? ,? ,? );");

    }

    public static String getNicknameByLoginAndPassword(String login, String password) {
        String nick = null;
        try {
            psGetNick.setString(1, login);
            psGetNick.setString(2, password);
            ResultSet rs = psGetNick.executeQuery();
            if (rs.next()) {
                nick = rs.getString(1);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nick;
    }

    public static boolean registration(String login, String password, String nickname) {
        try {
            psRegist.setString(1, login);
            psRegist.setString(2, password);
            psRegist.setString(3, nickname);
            psRegist.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void disconnect() {
        try {
            psRegist.close();
            psGetNick.close();
            psNewNick.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
