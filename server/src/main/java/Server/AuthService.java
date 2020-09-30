package Server;

public interface AuthService {
    String getNickByLoginPassword(String login, String password);

    boolean registrateInfo(String nick, String login, String password);
    boolean changeNick(String oldNick, String newNick);

}
