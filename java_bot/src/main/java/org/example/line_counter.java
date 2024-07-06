package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class line_counter {
    public int line_counter(String FileName) {
        int count = 0; // счетчик строк

        try (BufferedReader reader = new BufferedReader(new FileReader(FileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                count++;
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
        return count;
    }
}
