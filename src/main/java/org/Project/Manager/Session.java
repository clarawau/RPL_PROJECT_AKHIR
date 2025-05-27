package org.Project.Manager;

public class Session {
    private static Session instance;
    private int userId;
    private String username;

    private Session() {
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setSession(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void clearSession() {
        userId = 0;
        username = null;
    }
}