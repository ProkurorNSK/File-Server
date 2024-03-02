package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    //    private static final Map<String, Boolean> files = new HashMap<>();
    private static final int PORT = 34522;

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            try (
                    Socket socket = server.accept();
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream())
            ) {
                String msg = input.readUTF();
                System.out.println("Received: " + msg);
                String answer = "All files were sent!";
                output.writeUTF(answer);
                System.out.println("Sent: " + answer);
            }
        }
        /*Scanner sc = new Scanner(System.in);
        for (int i = 1; i < 11; i++) {
            files.put("file" + i, false);
        }

        while (true) {
            String[] command = sc.nextLine().split(" ");
            switch (command[0]) {
                case "add" -> {
                    if (files.get(command[1]) != null && !files.get(command[1])) {
                        files.put(command[1], true);
                        System.out.printf("The file %s added successfully\n", command[1]);
                    } else {
                        System.out.printf("Cannot add the file %s\n", command[1]);
                    }
                }
                case "get" -> {
                    if (files.get(command[1]) != null && files.get(command[1])) {
                        System.out.printf("The file %s was sent\n", command[1]);
                    } else {
                        System.out.printf("The file %s not found\n", command[1]);
                    }
                }
                case "delete" -> {
                    if (files.get(command[1]) != null && files.get(command[1])) {
                        files.put(command[1], false);
                        System.out.printf("The file %s was deleted\n", command[1]);
                    } else {
                        System.out.printf("The file %s not found\n", command[1]);
                    }
                }
                case "exit" -> {
                    return;
                }
            }
        }*/
    }
}