package Server;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private static PreparedStatement psGetNick;
    private static PreparedStatement psRegist;
    private static PreparedStatement psChangeNick;
    private static PreparedStatement psAddMessage;
    private static PreparedStatement psGetMessageForNick;


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
        psChangeNick = connection.prepareStatement("UPDATE catsinchat SET nickname = ? WHERE nickname = ?;");

        psAddMessage = connection.prepareStatement("INSERT INTO chat (sender, receiver, text, date) VALUES (\n" +
                 "(SELECT id FROM catsinchat WHERE nickname=?),\n" +
                 "(SELECT id FROM catsinchat WHERE nickname=?),\n" +
                 "?, ?)");

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
            psChangeNick.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //метод добавления сообщений в БД
    public static boolean addMsg(String sender, String receiver, String text, String date){
        try {
            psAddMessage.setString(1, sender);
            psAddMessage.setString(2, receiver);
            psAddMessage.setString(3, text);
            psAddMessage.setString(4, date);
            psAddMessage.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    //метод вытаскивания сообщений из БД
    public static String getMsgForNick(String nick) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            psGetMessageForNick.setString(1, nick);
            psGetMessageForNick.setString(2, nick);
            ResultSet resultSet = psGetMessageForNick.executeQuery();

            while (resultSet.next()) {
                String sender = resultSet.getString(1);
                String receiver = resultSet.getString(2);
                String text = resultSet.getString(3);
                String deta = resultSet.getString(4);

                if(receiver.equals("null")){
                    stringBuilder.append(String.format(" ",sender,text));
                }else{
                    stringBuilder.append(String.format(" private ", sender,receiver,text));
                }
            }
            resultSet.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static boolean changeNick(String oldNick, String newNick) {
        try {
            psChangeNick.setString(1, newNick);
            psChangeNick.setString(2, oldNick);
            psChangeNick.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }
}
