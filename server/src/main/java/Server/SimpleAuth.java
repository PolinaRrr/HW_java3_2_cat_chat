package Server;

import java.util.ArrayList;
import java.util.List;

public class SimpleAuth implements AuthService {
    private class UserData {
        String login;
        String password;
        String nick;

        public UserData(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }

    private List<UserData> users;

    public SimpleAuth() {
        this.users = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            users.add(new UserData("user" + i, "pass" + i, "cat" + i));
        }
    }

    @Override
    public String getNickByLoginPassword(String login, String password) {
        for (UserData c : users) {
            if (c.login.equals(login) && c.password.equals(password)) {
                return c.nick;
            }
        }
        return null;
    }

    @Override
    public boolean registrateInfo(String nick, String login, String password) {
        for (UserData o:users){
            if (o.login.equals(login)){
                return false;
            }
        }
        users.add(new UserData(nick, login, password));
        return true;
    }

    @Override
    public boolean changeNick(String oldNick, String newNick) {
        return false;
    }


    public String getLogin(String login) {
        for (UserData c : users) {
            if (c.login.equals(login)) {
                return c.login;
            }
        }
        return null;
    }

}
