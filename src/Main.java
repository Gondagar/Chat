
import java.io.IOException;
import java.util.Scanner;
import  server.Server;
import  client.Client;

public class Main {

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Введите 1 для запуска сервера или 2 для запуска клиента ");

        String choise = reader.nextLine();

        if (choise.equals("1")){


                new Server().run();





        } else if(choise.equals("2")){
            new Client().run();

        }


    }
}
