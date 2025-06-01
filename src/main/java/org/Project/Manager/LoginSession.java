package org.Project.Manager;

import java.io.*;

public class LoginSession {
    private static final String SESSION_FILE = "session.txt";

    public static void saveSession(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE))) {
            writer.write(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadSession() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void clearSession() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}
