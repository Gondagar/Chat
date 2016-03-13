package client;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by Admin on 11.03.2016.
 */
public class Client {
    String name;
    Scanner scanner;
    String address;
    OutputStreamWriter outputStreamWriter;


    public void run() {
        initScanner();
        label1:
        System.out.print("Введите адресс сервера");
        System.out.println();
        address = scanner.nextLine();

        try (Socket socket = new Socket(address, 777)) {


            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), "utf-8");

            ChatListaner chatListaner = new ChatListaner(socket);
            Thread thread = new Thread(chatListaner);

            thread.start();

            this.name =  sendClientNameForServer();

            String message;

            System.out.println("Введите сообщение. Для  завершения работы введите 'exit'");
            while (true) {

                message = scanner.nextLine();
                if("exit".equals(message.toLowerCase())){

                    outputStreamWriter.write(message + "\n");
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                    scanner.close();
                    chatListaner.isWork = false;
                    break;
                } else {

                    outputStreamWriter.write(message + "\n");
                    outputStreamWriter.flush();
                }

            }
        } catch (IOException e) {
            System.out.println("Подключение не удалось");
            this.run();

        }

    }
    private void initScanner() {
        String property = System.getProperty("os.name");
        char cp = property.charAt(0);
        if (cp == 'W') {
            scanner = new Scanner(System.in, "cp866");
            System.out.println(System.getProperty("os.name"));
            try {
                System.setOut(new PrintStream(System.out, true, "cp866"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (cp == 'L') {
            scanner = new Scanner(System.in, "utf-8");
            System.out.println(System.getProperty("os.name"));
        } else if (cp == 'M') {

            scanner = new Scanner(System.in, "utf-8");
            System.out.println(System.getProperty("os.name"));
        } else {

            scanner = new Scanner(System.in);
            System.out.println("OS не определена. Попытка запуска на страндартых настройках");

        }
    }

    private String sendClientNameForServer() {
        String name;
        System.out.print("Введите Ваше имя ");
        System.out.println();
        name = scanner.nextLine();
        try {
            outputStreamWriter.write(name + "\n");
            outputStreamWriter.flush();
        } catch (IOException e){
            System.out.println("Отправка имени не удалась");
        }



        return name;
    }

    class ChatListaner implements Runnable {

        boolean isWork = true;

        Socket socket;

        public ChatListaner(Socket socket) {
            this.socket = socket;


        }

        @Override
        public void run() {
            try (Scanner scanner = new Scanner(socket.getInputStream(), "utf-8")) {

                while (true) {

                    if(isWork) {
                        try {
                            System.out.println(scanner.nextLine());
                        } catch (NoSuchElementException e){
                            System.out.println("До встречи");
                        }

                    } else {
                        scanner.close();
                        socket.close();
                        break;
                    }

                }

            } catch (IOException e) {
                System.out.println("Упали в потоке получение данных клиентом");
            }

        }
    }
}
