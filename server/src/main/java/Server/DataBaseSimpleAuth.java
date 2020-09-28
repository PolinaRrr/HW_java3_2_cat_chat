package Server;

public class DataBaseSimpleAuth implements AuthService {
    @Override
    public String getNickByLoginPassword(String login, String password) {
        return null;
    }

    @Override
    public boolean registrateInfo(String nick, String login, String password) {
        return false;
    }

}
