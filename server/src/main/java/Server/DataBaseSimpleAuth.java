package Server;

public class DataBaseSimpleAuth implements AuthService {
    @Override
    public String getNickByLoginPassword(String login, String password) {
        return SQLHandler.getNicknameByLoginAndPassword(login,password);
    }

    @Override
    public boolean registrateInfo(String nick, String login, String password) {
        return false;
    }

    @Override
    public boolean changeNick(String oldNick, String newNick) {
        return SQLHandler.changeNick(oldNick,newNick);
    }

}
