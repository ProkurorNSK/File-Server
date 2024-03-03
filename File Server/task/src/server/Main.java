package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.util.concurrent.*;

import static java.nio.file.StandardOpenOption.*;

public class Main {
    private static final int PORT = 34523;
    private static final String partOfPath = System.getProperty("user.dir") + "\\src\\server\\data\\";
    private static final String pathMap = System.getProperty("user.dir") + "\\src\\server\\metadata\\map.txt";
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static ConcurrentMap<Integer, String> map;
    private static int id;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pathMap)))) {
            //noinspection unchecked
            map = (ConcurrentHashMap<Integer, String>) ois.readObject();
            id = map.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        } catch (ClassNotFoundException | IOException exception) {
            map = new ConcurrentHashMap<>();
            id = 0;
        }

        try (ServerSocket server = new ServerSocket(PORT)) {
            serverSocket = server;
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket socket = server.accept();
                Session session = new Session(socket);
                executor.submit(session);
            }
        } catch (IOException ignored) {
        } finally {
            executor.shutdown();
        }
    }

    private static synchronized int getNextID() {
        id++;
        return id;
    }

    private static void stopServer() {
        executor.shutdown();
        try (ObjectOutputStream ous = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(pathMap)))) {
            serverSocket.close();
            ous.writeObject(map);
        } catch (IOException ignored) {
        }
        System.exit(0);
    }

    static class Session implements Runnable {
        private final Socket socket;

        public Session(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream input = new DataInputStream(socket.getInputStream()); DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                int command = input.readInt();
                switch (command) {
                    case 2 -> {
                        int nextID = getNextID();
                        int nameLength = input.readInt();
                        byte[] nameBytes = new byte[nameLength];
                        //noinspection ResultOfMethodCallIgnored
                        input.read(nameBytes, 0, nameLength);
                        String name = new String(nameBytes);
                        if (name.isEmpty()) {
                            name = "file" + nextID + ".txt";
                        }
                        int dataLength = input.readInt();
                        byte[] data = new byte[dataLength];
                        //noinspection ResultOfMethodCallIgnored
                        input.read(data, 0, dataLength);
                        try {
                            Files.write(Paths.get(partOfPath + name), data, CREATE_NEW, WRITE);
                            map.put(nextID, name);
                            output.writeInt(200);
                            output.writeInt(nextID);
                        } catch (IOException e) {
                            output.writeInt(403);
                        }
                    }
                    case 1 -> {
                        int type = input.readInt();
                        String name = null;
                        switch (type) {
                            case 1 -> name = input.readUTF();
                            case 2 -> {
                                int id = input.readInt();
                                name = map.get(id);
                            }
                        }
                        try {
                            byte[] message = Files.readAllBytes(Paths.get(partOfPath + name));
                            output.writeInt(200);
                            output.writeInt(message.length);
                            output.write(message);
                        } catch (IOException e) {
                            output.writeInt(404);
                        }
                    }
                    case 3 -> {
                        int type = input.readInt();
                        String name = null;
                        switch (type) {
                            case 1 -> name = input.readUTF();
                            case 2 -> {
                                int id = input.readInt();
                                name = map.get(id);
                            }
                        }
                        if (Files.deleteIfExists(Paths.get(partOfPath + name))) {
                            output.writeInt(200);
                        } else {
                            output.writeInt(404);
                        }
                    }
                    case 0 -> {
                        output.writeInt(200);
                        stopServer();
                    }
                }
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
