package server;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Map<String, Boolean> files = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
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
        }
    }
}