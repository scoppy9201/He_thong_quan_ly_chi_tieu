/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.User;
/**
 *
 * @author Admin
 */
public class UserSession {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    public static void clear() {
        currentUser = null;
    }
}
