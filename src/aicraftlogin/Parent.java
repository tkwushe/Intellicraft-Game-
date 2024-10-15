package aicraftlogin;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;

import static aicraftlogin.Child.getChildByName;
import static aicraftlogin.Child.getChildList;
import static aicraftlogin.LoginSystem.clearScreen;
import static java.lang.System.out;

public class Parent  {

    private final String username;
    private final String password;

    public Parent(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static boolean register(String username, String password) throws IOException {
        if (!isValidUsername(username))
        {return false;}
        if (!isValidPassword(password))
        {out.println("Please include at least 1 number , 1 symbol,a capital letter and enter a password with at least 8 characters");
        return false;}

        String hashedPassword = FileData.getHashedPassword(username);
        if (hashedPassword != null) {
            out.println("Username already exists. Please choose a different username.");
            return false;
        }

        FileData.addUser(username, password);
        new Parent(username, password);
        return true;
    }

    public static boolean loginParent(String username, String password) throws IOException, NoSuchAlgorithmException {
        String hashedPassword = FileData.getHashedPassword(username);
        return hashedPassword != null && hashedPassword.equals(FileData.hashPassword(password));
    }

    public static boolean isValidUsername(String username) {
        return !username.isEmpty();
    }
// method to check if password matches the criteria of a strong password which are that it should be at least 8 characters including at least 1 symbol, 1 capital letter and 1 number
    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(regex);
    }



    public static void viewSTEMProgression() {
        System.out.println("Viewing child(ren) STEM progression...");
        clearScreen();
    }

    public static void changeChildPIN() {
        Scanner input = new Scanner(System.in);
        String childName;
        String newPIN;

        // Print the list of children and their PINs
        List<Child> childList = getChildList();
        System.out.println("Current PINs for children:");
        for (Child c : childList) {
            System.out.println(c.getName() + ": " + c.getPin());
        }

        System.out.println("Enter the name of the child to change PIN for: ");
        childName = input.nextLine();

        // Get the Child object for the specified name
        Child child = getChildByName(childName);

        if (child == null) {
            System.out.println("No child found with name " + childName);
            return;
        }


        clearScreen();
        System.out.print("Enter the new PIN for " + childName + ": ");
        newPIN = input.nextLine();
// this is to check if the child's PIN has successfully been changed in the children.txt file
        boolean success = child.changePIN(child.getPin(), newPIN);

        if (success) {
            System.out.println(childName + "'s PIN has been updated to " + newPIN);
        } else {
            System.out.println("Failed to update " + childName + "'s PIN");
        }

        System.out.println("\nPress any key to return to the parent menu.");
        input.nextLine(); // Wait for user to press any key
        clearScreen();
        System.exit(0);

        input.close();
    }







}
