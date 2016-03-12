package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Admin on 11.03.2016.
 */
public class Client {

   Scanner  scanner;
    String address;



  public   void run(){
            initScanner();
            System.out.print("Введите адресс сервера");
            System.out.println();
            address = scanner.nextLine();

        try(Socket socket = new Socket(address,777)) {



            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), "utf-8");

            Thread thread = new Thread(new ChatListaner(socket));

            thread.start();

            String message = getName();

            System.out.println("Введите сообщение. Для  завершения работы введите 'exit'");
            while (!"exit".equals(message = scanner.nextLine())){

                outputStreamWriter.write(message + "\n");
                outputStreamWriter.flush();

            }
        } catch (IOException e) {
            System.out.println("Подключение не удалось");
        }

    }

    private void initScanner()  {
        String property = System.getProperty("os.name");
        char cp = property.charAt(0);
        if (cp == 'W'){
            scanner = new Scanner(System.in,"cp866");
            System.out.println(System.getProperty("os.name"));
            try {
                System.setOut(new PrintStream(System.out, true, "cp866"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (cp == 'L' ){
            scanner = new Scanner(System.in,"utf-8");
            System.out.println(System.getProperty("os.name"));
        } else if(cp =='M'){

            scanner = new Scanner(System.in,"utf-8");
            System.out.println(System.getProperty("os.name"));
        } else {

            scanner = new Scanner(System.in);
            System.out.println("OS не определена. Попытка запуска на страндартых настройках");

        }
    }

    private String getName() {
        String name;
        System.out.print("Введите Ваше имя ");
        System.out.println();
        name = scanner.nextLine();


        return  name;
    }

    class ChatListaner implements Runnable{



        Socket socket;

        public ChatListaner(Socket socket) {
            this.socket  = socket;


        }

        @Override
        public void run() {
            try ( Scanner scanner =  new Scanner(socket.getInputStream(),"utf-8")) {

                while (true){
                    System.out.println(scanner.nextLine());

                    Thread.sleep(100);
                }

            } catch (IOException e) {
                System.out.println("Упали в потоке получение данных клиентом");
            } catch (InterruptedException e) {
                System.out.println("Досведос");
            }

        }
    }





}
