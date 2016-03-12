package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Admin on 11.03.2016.
 */
public class Server {
    int count;

    // ArrayList<Connection> connectionsArrayList = new ArrayList<Connection>();
    BlockingQueue<Connection> connectionsArrayList = new LinkedBlockingQueue<Connection>();

  public void run()  {
      ServerSocket serverSocket;
    try {
        serverSocket = new ServerSocket(777);
        System.out.println("Серверный сокет создан");

        while (true) {
            Socket newSocket = serverSocket.accept();
            Runnable runnable = new Connection(newSocket);
            Thread thread = new Thread(runnable);
            thread.start();

        }


        } catch (IOException e){
        System.out.println("Сервер не запустился на нужному порту");
    }

  }

    class Connection implements Runnable {

        Socket socket;

        String name;
        public Connection(Socket socket) {
            this.socket = socket;
            connectionsArrayList.add(this);
        }

        @Override
        public void run() {

           try(InputStream inputStream = socket.getInputStream()) {

               Scanner scanner = new Scanner(inputStream,"utf-8");
               this.name = scanner.nextLine();
               for (Connection connection : connectionsArrayList){


                   Writer writer =  new OutputStreamWriter(connection.socket.getOutputStream(),"utf-8");
                   writer.write("К нам присоеденился " + this.name + " \n");
                   writer.flush();


               }

               while (socket.isConnected()){
                    if (socket.isClosed()){
                        connectionsArrayList.remove(this);
                        System.out.println(this.name + " покинул нас. ");
                        break;
                    }
                   String messeng = scanner.nextLine();

                   System.out.println("Получено новое сообщение от клиента -" + messeng);
                   for (Connection connection : connectionsArrayList){


                       Writer writer =  new OutputStreamWriter(connection.socket.getOutputStream(),"utf-8");
                       writer.write(this.name + " - " + messeng + "\n");
                       writer.flush();


                   }
                   System.out.println("По идеи должно быть отправлено всем клиентам ");

               }

           } catch (IOException e) {
               System.out.println(Thread.currentThread().toString() + " не инициализировался ");
           }


        }
    }
}
