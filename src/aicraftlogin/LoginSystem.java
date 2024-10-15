package aicraftlogin;

import aicraftgame.IntelligentCraftGame;
import aicraftgame.Player;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;

import static aicraftlogin.Child.getChildByName;
import static aicraftlogin.Child.getChildList;
import static aicraftlogin.Parent.changeChildPIN;
import static aicraftlogin.Parent.loginParent;
import static java.lang.System.out;

public class LoginSystem {
/*
As java does not have a clear screen function I had to use ANSI escape code to perform this action.
This method will move the cursor of the console and clear the screen .
How to clear screen in Java - Javatpoint (no date) www.javatpoint.com.
Available at: https://www.javatpoint.com/how-to-clear-screen-in-java#ANSI-escape-Code (Accessed: April 21, 2023).
 */
    public static void clearScreen() {

        System.out.print("\033[H\033[2J");

        System.out.flush();

    }
    public void displayLoginMenu() throws IOException, NoSuchAlgorithmException {

        final String VALID_OPTION_MSG = "Please enter a valid option!";
        try (Scanner scanner = new Scanner(System.in)) {
            // Ask if user is a child
            out.println("Welcome to the IntelliCraft Game !");
            out.println("Are you a Child? (yes or no)");
            String userRole = scanner.nextLine().toLowerCase();

            // If user is not a child
            if (userRole.equals("no")) {
                // Ask child whether they want to register or login
                out.println("What would you like to do? (1 = register, 2 = login)");
                int choice = scanner.nextInt();
                scanner.nextLine();

                // Validate choice
                while (choice != 1 && choice != 2) {
                    out.println(VALID_OPTION_MSG);
                    choice = scanner.nextInt();
                    scanner.nextLine();
                }

                // Perform action based on choice
                // Register parent
                if (choice == 1) {
                    out.println("Please Register below:");
                    out.println("Please enter your username:");
                    String username = scanner.nextLine();
                    out.println("Please enter your password:");

                    String password = scanner.nextLine();
                    boolean isRegistered = Parent.register(username, password);
                    if (isRegistered) {
                        out.println("Registration successful!");
                        displayParentMenu(username);
                    } else {
                        out.println("Registration failed. Please enter a valid username and password that meets the requirements.");
                    }

                    // Login parent
                } else {
                    out.println("Please Log in below:");
                    out.println("Please enter your username:");
                    String username = scanner.nextLine();
                    out.println("Please enter your password:");
                    String password = scanner.nextLine();
                    boolean isLoggedIn = loginParent(username, password);
                    if (isLoggedIn) {
                        out.println("Login successful!");
                        displayParentMenu(username);
                    } else {
                        out.println("Login failed. Please check your username and password and try again.");
                    }
                }
            }

            // If user is a child
            else if (userRole.equals("yes")) {
                // Ask for register or login
                out.println("What would you like to do? (1 = register, 2 = login)");
                int choice = scanner.nextInt();
                scanner.nextLine();

                // Validate choice
                while (choice != 1 && choice != 2) {
                    out.println(VALID_OPTION_MSG);
                    choice = scanner.nextInt();
                    scanner.nextLine();
                }

                // Perform action based on choice
                switch (choice) {
                    // Register child
                    case 1 -> {
                        out.println("Please Register below:");
                        out.println("Please enter your name:");
                        String name = scanner.nextLine().toLowerCase().trim();
                        out.println("Please enter your age:");
                        int age = scanner.nextInt();
                        scanner.nextLine();
                        out.println("Please enter your PIN:");
                        String pin = scanner.nextLine();
                        // Create new child object with user inputs
                        Child child = new Child(name, age, pin);
                        //check to see if child information has been successfully saved in text file
                        boolean isRegistered = child.registerChild(name, age, pin);
                        if (isRegistered) {
                            out.println("Registration successful!");
                            clearScreen();
                            displayChildMenu(name);
                        } else {
                            out.println("Registration failed. Please enter a valid name, age and 4-digit PIN that meets the requirements.");
                        }

                    }
                    case 2 -> {
                        List<Child> childList = Child.getChildList();
                        if (childList.isEmpty()) {
                            out.println("There are no children registered. Please register a child first.");
                            clearScreen();
                            break;
                        }
                        out.println("Please choose a child to login:");
                        StringBuilder childNames = new StringBuilder();
                        for (Child child : childList) {
                            childNames.append(child.getName()).append(", ");
                        }
                        childNames = new StringBuilder(childNames.substring(0, childNames.length() - 2)); // remove last comma and space
                        out.println(childNames);
                        String name = scanner.nextLine().toLowerCase().trim();
                        boolean validChildName = false;
                        for (Child child : childList) {
                            if (child.getName().equalsIgnoreCase(name)) {
                                validChildName = true;
                                out.println("Please enter your PIN:");
                                String pin = scanner.nextLine();
                                boolean isLoggedIn = child.loginChild(name, pin);

                                if (isLoggedIn) {
                                    out.println("Login successful!");
                                    clearScreen();
                                    displayChildMenu(name);

                                } else {
                                    out.println("Login failed. Please check your name and PIN and try again.");
                                }
                                break;
                            }
                        }
                        if (!validChildName) {
                            out.println("Invalid child name. Please try again.");
                        }


                    }
                    default -> throw new IllegalStateException("Unexpected value: " + choice);
                }
            }
        }
    }

    public void displayChildMenu(String name) {


        //create game
        IntelligentCraftGame intelligentCraftGame = new IntelligentCraftGame();
        try (Scanner scanner = new Scanner(System.in)) {

            out.println("Welcome to the IntelliCraft Game,!");
            out.println("What would you like to do," + name + "?\n1 = play\n2 = exit");

            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice == 1) {
                clearScreen();
                intelligentCraftGame.displayGameMenu(name);

            } else if (choice == 2) System.exit(0);

            while (choice != 1 && choice != 2) {
                out.println("Please enter a valid option!");
                choice = scanner.nextInt();
                scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void viewChildStatistics() {
        Scanner input = new Scanner(System.in);
        String childName;

        // Print the list of children
        List<Child> childList = getChildList();
        System.out.println("Registered children:");
        for (Child c : childList) {
            System.out.println(c.getName());
        }

        System.out.println("Please enter the name of the child that you want to view the statistics of ");

        childName = input.nextLine().toLowerCase();

        // Get the Child object for the specified name
        Child child = getChildByName(childName);

        if (child == null) {
            System.out.println("No child found with name " + childName);
            input.close();
            return;
        }
        clearScreen();
        Player player = new Player(childName);
        player.displayPlayerStatistics(player);
        clearScreen();
        System.exit(0);

        input.close(); // Close the scanner when we're done with it
    }

    public void displayParentMenu(String username) {
        Scanner input = new Scanner(System.in);
        int choice = 0;

        while (choice != 4) {
            out.println("Welcome to the IntelliCraft Parent menu, " + username + "!");
            System.out.println("Please select an option:");
            System.out.println("1. View Child(ren) STEM Progression");
            System.out.println("2. Change Child PIN");
            System.out.println("3. View Child Statistics");
            System.out.println("4. Quit Menu\n");
            System.out.print("Enter your choice: ");
            // Validate input to ensure it's an integer between 1 and 4
            while (!input.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                input.nextLine();
            }
            choice = input.nextInt();
            input.nextLine(); // Consume the newline character

            switch (choice) {
                case 1 -> Parent.viewSTEMProgression();
                case 2 -> changeChildPIN();
                case 3 -> viewChildStatistics();
                case 4 -> System.out.println("Quitting menu...");
                default -> System.out.println("Invalid choice. Please select a number between 1 and 4.");
            }
        }

        input.close(); // Close the scanner when we're done with it
    }

}



