package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server {
    private final List<ClientHandler> clients;
    private final AuthService authService;
    final int PORT = 8290;
    ServerSocket server = null;
    Socket socket;

    public Server() {
        clients = new Vector<>();

        if (!SQLHandler.connect()) {
            throw new RuntimeException("Не удалось подключиться к БД");
        }
        authService = new DataBaseSimpleAuth();


        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен!");


            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //метод вывода всех сообщений от всех клиентов в текстарея
    public void broadCastMsg(String nick, String msg) {
        for (ClientHandler c : clients) {
            c.sendMsg(nick + ": " + msg);
        }
    }

    //метод передачи приватных сообщений
    public void privateMsg(ClientHandler sender, String receiver, String msg) {
        String message = String.format("[ %s ] private [ %s ] : %s", sender.getNick(), receiver, msg);
        for (ClientHandler c : clients) {
            if (c.getNick().equals(receiver)){
                c.sendMsg(message);
                if (!sender.getNick().equals(receiver)) {
                    sender.sendMsg(message);
                }
                return;
            }
        }

        sender.sendMsg("Пользователь " + receiver + " не обнаружен");
    }

    //метод подключения клиента
    public void subscrible(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadCastClientList();
    }

    // метод отключения клиента
    public void unSubscrible(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadCastClientList();
    }

    public AuthService getAuthService() {
        return authService;
    }
    public boolean isLoginAuth(String login){
        for (ClientHandler c : clients) {
            if (c.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }
    public void broadCastClientList() {
        StringBuilder stringBuilder=new StringBuilder("/client ");
        for (ClientHandler c : clients) {
            stringBuilder.append(c.getNick()).append(" ");
        }
        String msg = stringBuilder.toString();
        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }
}