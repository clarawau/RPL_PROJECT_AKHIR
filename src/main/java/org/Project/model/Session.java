package org.Project.model;

public class Session {
    private static int userId;
    private static String username;

    // Setter
    public static void setSession(int id, String user) {
        userId = id;
        username = user;
    }

    // Getter
    public static int getUserId() {
        return userId;
    }

    public static String getUsername() {
        return username;
    }

    // Clear session (misalnya saat logout)
    public static void clearSession() {
        userId = 0;
        username = null;
    }
}
