package aicraftlogin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Child  {

    private static final ArrayList<Child> childList = new ArrayList<>();


    private final String name; // name of the child
    private final int age;// age of child
    private String pin;// stores pin

    public Child(String name , int age , String pin) {
        this.name = name;
        this.age = age;
        this.pin = pin;

    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getPin() {
        return pin;
    }

    public static boolean isValidPIN(String pin) {
        return pin.length() == 4;
    }

    public static boolean isValidName(String name) {
        return name.matches("[a-zA-Z ]+");
    }

    public static boolean isValidAge(int age) {
        // A person's age should be between 0 and 150 (assuming a person cannot live beyond 150 years)
        return age >= 0 && age <= 150;
    }

    public boolean registerChild(String name, int age, String pin) throws IOException {
        if (!isValidName(name) || !isValidAge(age) || !isValidPIN(pin)) {
            return false;
        }
        Child child = new Child(name, age, pin);

        childList.add(child);
        FileData.addChild(name, age, pin);
        return true;
    }

    public boolean loginChild(String name ,String pin) {
        if (!isValidName(name) ||  !isValidPIN(pin)) {
            return false;
        }
        for (Child child : childList) {
            if (child.getPin().equals(pin) && child.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean changePIN(String currentPIN, String newPIN) {
        // Verify that the currentPIN matches the Child's PIN
        if (!currentPIN.equals(this.pin)) {
            return false;
        }

        // Verify that the newPIN is valid
        if (!isValidPIN(newPIN)) {
            return false;
        }

        // Update the Child's PIN
        this.pin = newPIN;

        // Update the PIN in the data file
        try {
            FileData.updateChildPIN(this.name, newPIN);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }





    public static void populateChildList() throws IOException {
        try (
                BufferedReader in = new BufferedReader(new FileReader(FileData.CHILDREN_FILE))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] fields = line.split(",");
                String name = fields[0];
                int age = Integer.parseInt(fields[1]);
                String pin = fields[2];
                Child child = new Child(name, age, pin);

                childList.add(child);
            }
        }
    }


   public static Child getChildByName(String name) {
       // Get the childList using the public getter method
       List<Child> childList = Child.getChildList();
       // Loop through the childList to find the child with the given name
       for (Child child : childList) {
           if (child.getName().equals(name)) {
               return child;
           }
       }
       // Return null if no child is found with the given name
       return null;
   }

    public static List<Child> getChildList() {
        return childList;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

}
