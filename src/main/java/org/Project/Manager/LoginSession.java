package org.Project.Manager;

import java.io.*;

public class LoginSession {
    private static final String SESSION_FILE_PATH = System.getProperty("user.home") + File.separator + ".projectapp_session";

    // Menyimpan username ke file session
    public static void saveSession(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE_PATH))) {
            writer.write(username);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan session: " + e.getMessage());
        }
    }

    // Membaca username dari file session
    public static String loadSession() {
        File file = new File(SESSION_FILE_PATH);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String username = reader.readLine();
                if (username != null && !username.trim().isEmpty()) {
                    return username.trim();
                }
            } catch (IOException e) {
                System.err.println("Gagal membaca session: " + e.getMessage());
            }
        }
        return null;
    }

    // Menghapus file session (logout)
    public static void clearSession() {
        File file = new File(SESSION_FILE_PATH);
        if (file.exists() && !file.delete()) {
            System.err.println("Gagal menghapus file session.");
        }
    }
}
