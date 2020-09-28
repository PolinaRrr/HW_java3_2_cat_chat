package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;
    private String login;


    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    socket.setSoTimeout(100000);
                    //цикл авторизации
                    while (true) {
                        String str = in.readUTF();

                        System.out.println(str);

                        if(str.startsWith("/")){
                            if(str.startsWith("/reg ")){

                                String[] token = str.split(" ");
                                if (token.length < 4) {
                                    continue;
                                }
                                boolean successfull = server.getAuthService().registrateInfo(token[1],token[2],token[3]);
                                if(successfull){
                                    sendMsg("/regok");
                                }else{
                                    sendMsg("/regno");
                                }

                            }
                            if (str.startsWith("/auth ")) {
                                String[] token = str.split(" ");
                                if (token.length < 3) {
                                    continue;
                                }

                                System.out.println("token.length " + token.length);
                                String newNick = server.getAuthService().getNickByLoginPassword(token[1], token[2]);

                                login = token[1];

                                if (newNick != null) {

                                    if(!server.isLoginAuth(login)){
                                        nick = newNick;
                                        sendMsg("/authok " + newNick);
                                        server.subscrible(this);
                                        System.out.println("Клиент: " + nick + " подключен");
                                        socket.setSoTimeout(0);
                                        break;
                                    }else{
                                        sendMsg("Данный логин уже авторизован");
                                    }
                                } else {
                                    sendMsg("Неверный логин или пароль");
                                }
                            }
                        }

                    }
                    //цикл работы
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                sendMsg("/end");
                                break;
                            }
                            if ((str.startsWith("/w "))) {
                                String[] token = str.split(" ", 3);
                                if (token.length < 3) {
                                    continue;
                                }
                                server.privateMsg(this, token[1], token[2]);
                            }
                        } else {
                            server.broadCastMsg(nick, str);
                        }
                    }
                }
                catch (SocketTimeoutException e) {
                    sendMsg("/end ");
                    System.out.println("Rлиент отключился по таймауту");
                }
                catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unSubscrible(this);
                    System.out.println("Клиент отключился");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }

    public String getLogin() {
        return login;
    }
}
