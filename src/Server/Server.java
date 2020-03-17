package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Vector;

public class Server {

    private Vector<ClientHandler> clients;

    public Server() throws SQLException {
        ServerSocket server = null;
        Socket socket = null;
        clients = new Vector<>();

        try {
            AuthService.connect();

            // System.out.println(AuthService.getNickByLoginAndPass("login12", "pass1"));

            server = new ServerSocket(8189);
            System.out.println("Сервер запущен");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(socket, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }

    // подписываем клиента на рассылку
    public void subscribe(ClientHandler client) {
        clients.add(client);
        System.out.println("Клиент подписан");
    }

    // отписываем клиента от рассылки сообщений
    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
    }

    // проверка пользователя в списке
    public boolean checkNickIsOnline(String nick) {
        for (ClientHandler c : clients) {
            if (c.getNick().equals(nick)) return true;
        }
        return false;
    }

    //отправка сообщений всем клиентам
    public void broadcastMsg(String msg) {
        for (ClientHandler c : clients) {
            c.sendMsg(msg);
        }
    }

    //отправка личных сообщений
    public boolean whisperMsg(String sender, String nick, String msg) {
        for (ClientHandler c : clients) {
            if (c.getNick().equals(nick)) {
                c.sendMsg(sender + ": " + msg);
                return true;
            }
        }
        return false;
    }


}
