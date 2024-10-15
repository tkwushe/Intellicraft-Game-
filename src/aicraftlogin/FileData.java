package aicraftlogin;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class FileData extends LoginSystem {

    private static final String PARENTS_FILE = "parent.txt";
    static final String CHILDREN_FILE = "children.txt";

    public FileData() {
        // Define the paths to the users and children files using constants
        String usersFilePath = PARENTS_FILE;
        String childrenFilePath = CHILDREN_FILE;

        // Create the file objects using the paths
        File usersFile = new File(usersFilePath);
        File childrenFile = new File(childrenFilePath);
// Check if the files already exist
        if (!usersFile.exists() || !childrenFile.exists()) {
            try {
                // Create the users file if it doesn't exist
                if (!usersFile.exists() && !usersFile.createNewFile()) {
                    throw new RuntimeException("Failed to create file: " + usersFilePath);
                }
                // Create the children file if it doesn't exist
                if (!childrenFile.exists() && !childrenFile.createNewFile()) {
                    throw new RuntimeException("Failed to create file: " + childrenFilePath);
                }
            } catch (IOException e) {
                // Throw a runtime exception if the file creation fails
                throw new RuntimeException("Failed to create file(s): " + usersFilePath + ", " + childrenFilePath, e);
            }
        }

        // Populate the childList ArrayList with data from the children.txt file
        try {
            Child.populateChildList();
        } catch (IOException e) {
            // Throw a runtime exception if there's an error reading the children file
            throw new RuntimeException("Failed to read children file: " + childrenFilePath, e);
        }

    }

    // Method to add a user
    public static void addUser(String username, String password) throws IOException {
        String hashedPassword;
        try {
            hashedPassword = hashPassword(password);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(PARENTS_FILE, true)))){
            out.println(username + "," + hashedPassword);
        }
    }

    // Method to add a child
    public static void addChild(String name, int age, String pin) throws IOException {
        Child child = new Child(name, age, pin);
        // get the childList using the public getter method
        List<Child> childList = Child.getChildList();
        childList.add(child);

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(CHILDREN_FILE, true)))) {
            out.println(name + "," + age + "," + pin);
        }
    }

    // Method to get the hashed password for a given username
    public static String getHashedPassword(String username) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(PARENTS_FILE))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields[0].equals(username)) {
                    return fields[1];
                }
            }
        }
        return null;
    }
    public static void updateChildPIN(String childName, String newPIN) throws IOException {
        // Get the childList using the public getter method
        List<Child> childList = Child.getChildList();

        // Loop through each child in the childList
        for (Child child : childList) {
            // If the child's name matches the given name, update the PIN
            if (child.getName().equals(childName)) {
                child.setPin(newPIN);
                break;
            }
        }

        // Write the updated childList to the children file
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(CHILDREN_FILE)))) {
            for (Child child : childList) {
                out.println(child.getName() + "," + child.getAge() + "," + child.getPin());
            }
        }
    }

    // Method to hash a password using SHA-256 algorithm
    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] byteData = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : byteData) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}


