package org.example.parsers;

import java.util.Scanner;

public final class InputParser {

    public String parse() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter text to encrypt: ");
        String text = scanner.nextLine();

        return text;
    }
}
