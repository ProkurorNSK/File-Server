package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34522;

    public static void main(String[] args) throws IOException {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
            String command = sc.next();
            switch (command) {
                case "1" -> {
                    System.out.print("Enter filename: ");
                    String name = sc.next();
                    output.writeUTF("GET " + name);
                    System.out.println("The request was sent.");
                    String receivedMsg = input.readUTF();
                    switch (receivedMsg.substring(0, 3)) {
                        case "200" -> System.out.println("The content of the file is: " + receivedMsg.substring(4));
                        case "404" -> System.out.println("The response says that the file was not found!");
                    }
                }
                case "2" -> {
                    System.out.print("Enter filename: ");
                    String name = sc.next();
                    System.out.print("Enter file content: ");
                    String content = sc.next();
                    output.writeUTF("PUT " + name + " " + content);
                    System.out.println("The request was sent.");
                    String receivedMsg = input.readUTF();
                    switch (receivedMsg) {
                        case "200" -> System.out.println("The response says that the file was created!");
                        case "403" -> System.out.println("The response says that creating the file was forbidden!");
                    }
                }
                case "3" -> {
                    System.out.print("Enter filename: ");
                    String name = sc.next();
                    output.writeUTF("DELETE " + name);
                    System.out.println("The request was sent.");
                    String receivedMsg = input.readUTF();
                    switch (receivedMsg) {
                        case "200" -> System.out.println("The response says that the file was successfully deleted!");
                        case "404" -> System.out.println("The response says that the file was not found!");
                    }
                }
                case "exit" -> {
                    output.writeUTF(command);
                    System.out.println("The request was sent.");
                }
            }
        }
    }
}
