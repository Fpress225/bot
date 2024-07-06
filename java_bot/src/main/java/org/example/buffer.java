package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class buffer {
    public static ArrayList<String> buffer(String FileName) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(FileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        line_counter lineCounter = new line_counter();
        int line_count = lineCounter.line_counter(FileName);
        ArrayList<String> buf = new ArrayList<>();
        for (int i = 0; i < line_count; i++){
            try {
                buf.add(reader.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return buf;
    }
}


