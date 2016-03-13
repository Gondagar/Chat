package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Server {
    int count;

    // ArrayList<Connection> connectionsArrayList = new ArrayList<Connection>();
    BlockingQueue<Connection> connectionsArrayList = new LinkedBlockingQueue<Connection>();

    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(777);
            System.out.println("Серверный сокет создан");

            do {
                Socket newSocket = serverSocket.accept();
                Connection connection = new Connection(newSocket);
                Thread thread = new Thread(connection);
                thread.start();
                connectionsArrayList.add(connection);

            } while (!connectionsArrayList.isEmpty());
            serverSocket.close();
            System.out.println("Сервер настроет на автоотлючение при  потере последнего клиента");

        } catch (IOException e) {
            System.out.println("Сервер не запустился на нужному порту");
        }

    }

    class Connection implements Runnable {

        Socket socket;
        Writer writer;
        String name;

        public Connection(Socket socket) {
            this.socket = socket;

        }

        @Override
        public void run() {

            try (InputStream inputStream = socket.getInputStream()) {

                Scanner scanner = new Scanner(inputStream, "utf-8");
                String message;
                this.name = scanner.nextLine();
                message = "Всем привет :)";
                sendMessage(message);

                while (socket.isConnected()) {

                    message = scanner.nextLine();
                    if ("exit".equals(message)) {
                        System.out.println(this.name + " покидает нас.");
                        message =  "отключился.";
                        sendMessage(message);
                        connectionsArrayList.remove(this);
                        break;
                    }
                    System.out.println("Новое сообщение от " + this.name + ": " + message);
                    sendMessage(message);

                }

            } catch (IOException e) {
                System.out.println(Thread.currentThread().toString() + " не инициализировался ");
            }

            System.out.println(Thread.currentThread() + " Потерял своего кліента и это последняя строчка которую он выполняет");
        }

        private void sendMessage(String messeng) throws IOException {
            for (Connection connection : connectionsArrayList) {

                if (connection.equals(this)) continue;

                if (connection.socket.isConnected()) {
                    Writer writer = new OutputStreamWriter(connection.socket.getOutputStream(), "utf-8");
                    writer.write(this.name + ": " + messeng + "\n");
                    writer.flush();
                } else {
                    System.out.println("Клиент " + connection.name + " не доступен");
                }

            }
            System.out.println("По идеи должно быть отправлено всем клиентам ");
        }
    }
}
