package org.example;

import java.util.Scanner;

public class InputHandler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter text to encrypt: ");
        String text = scanner.nextLine();
        System.out.println("Text received: " + text);
        scanner.close();
    }
}
