package com.example.finalproject;
import java.io.*;
import java.util.ArrayList;

public class MyFile {
    public static ArrayList<String> readFile(String fileName) {
        ArrayList<String> strings = new ArrayList<String>();
        try {
            File f = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(f));
            String str;
            while((str = br.readLine()) != null) {
                strings.add(str);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strings;
    }

    public static void writeFile(ArrayList<String> strings, String fileName) {
        try {
            File f = new File(fileName);
            PrintWriter pW = new PrintWriter(new FileWriter(f));
            for(String s : strings) {
                pW.println(s);
            }
            pW.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}