package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;

import static java.nio.file.StandardOpenOption.*;

public class Main {

    //    private static final Map<String, Boolean> files = new HashMap<>();
    private static final int PORT = 34522;

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            while (true) {
                try (
                        Socket socket = server.accept();
                        DataInputStream input = new DataInputStream(socket.getInputStream());
                        DataOutputStream output = new DataOutputStream(socket.getOutputStream())
                ) {
                    String[] msg = input.readUTF().split(" ");

                    switch (msg[0]) {
                        case "PUT" -> {
                            String name = msg[1];
                            String content = msg[2];
                            Path path = Paths.get(".\\File Server\\task\\src\\server\\data\\" + name);
                            try {
                                Files.writeString(path, content, CREATE_NEW, WRITE);
                                output.writeUTF("200");
                            } catch (IOException e) {
                                output.writeUTF("403");
                            }
                        }
                        case "GET" -> {
                            String name = msg[1];
                            Path path = Paths.get(".\\File Server\\task\\src\\server\\data\\" + name);
                            try {
                                String content = Files.readString(path);
                                output.writeUTF("200 " + content);
                            } catch (IOException e) {
                                output.writeUTF("404");
                            }
                        }
                        case "DELETE" -> {
                            String name = msg[1];
                            Path path = Paths.get(".\\File Server\\task\\src\\server\\data\\" + name);
                            if (Files.deleteIfExists(path)) {
                                output.writeUTF("200 ");
                            } else {
                                output.writeUTF("404");
                            }
                        }
                        case "exit" -> {
                            return;
                        }
                    }
                }
            }
        }
    }
}