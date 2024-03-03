package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34523;
    private static final String partOfPath = System.getProperty("user.dir") + "\\src\\client\\data\\";

    public static void main(String[] args){
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException ignored) {
        }

        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
            String command = sc.nextLine();
            switch (command) {
                case "1" -> {
                    System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
                    String type = sc.nextLine();
                    output.writeInt(1);
                    output.writeInt(Integer.parseInt(type));
                    switch (type) {
                        case "1" -> {
                            System.out.print("Enter filename: ");
                            String name = sc.nextLine();
                            output.writeUTF(name);
                        }
                        case "2" -> {
                            System.out.print("Enter id: ");
                            String id = sc.nextLine();
                            output.writeInt(Integer.parseInt(id));
                        }
                    }
                    System.out.println("The request was sent.");
                    int status = input.readInt();
                    switch (status) {
                        case 200 -> {
                            int dataLength = input.readInt();
                            byte[] data = new byte[dataLength];
                            //noinspection ResultOfMethodCallIgnored
                            input.read(data, 0, dataLength);
                            System.out.print("The file was downloaded! Specify a name for it: ");
                            String name = sc.nextLine();
                            Files.write(Paths.get(partOfPath + name), data);
                            System.out.println("File saved on the hard drive!");
                        }
                        case 404 -> System.out.println("The response says that this file is not found!");
                    }
                }
                case "2" -> {
                    System.out.print("Enter name of the file: ");
                    String name = sc.nextLine();
                    System.out.print("Enter name of the file to be saved on server: ");
                    String nameServer = sc.nextLine();
                    byte[] nameServerBytes = nameServer.getBytes();
                    byte[] message = Files.readAllBytes(Paths.get(partOfPath + name));
                    output.writeInt(2);
                    output.writeInt(nameServerBytes.length);
                    output.write(nameServerBytes);
                    output.writeInt(message.length);
                    output.write(message);
                    System.out.println("The request was sent.");
                    int status = input.readInt();
                    switch (status) {
                        case 200 -> System.out.println("Response says that file is saved! ID = " + input.readInt());
                        case 403 -> System.out.println("The response says that creating the file was forbidden!");
                    }
                }
                case "3" -> {
                    System.out.print("Do you want to delete the file by name or by id (1 - name, 2 - id): ");
                    String type = sc.nextLine();
                    output.writeInt(3);
                    output.writeInt(Integer.parseInt(type));
                    switch (type) {
                        case "1" -> {
                            System.out.print("Enter filename: ");
                            String name = sc.nextLine();
                            output.writeUTF(name);
                        }
                        case "2" -> {
                            System.out.print("Enter id: ");
                            String id = sc.nextLine();
                            output.writeInt(Integer.parseInt(id));
                        }
                    }
                    System.out.println("The request was sent.");
                    int status = input.readInt();
                    switch (status) {
                        case 200 -> System.out.println("The response says that this file was deleted successfully!");
                        case 404 -> System.out.println("The response says that this file is not found!");
                    }
                }
                case "exit" -> {
                    output.writeInt(0);
                    System.out.println("The request was sent.");
                    input.readInt();
                }
            }
        } catch (Exception ignored) {
        }
    }
}
