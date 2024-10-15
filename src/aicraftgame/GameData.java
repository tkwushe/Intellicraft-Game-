package aicraftgame;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameData  {
    private final ArrayList<String> questions;
    private final ArrayList<String> answers;

    /**
     * @param filename = the name of the file to load
     */
    // Load questions and answers from file
    protected GameData(String filename) {
        questions = new ArrayList<>();
        answers = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(new File(filename));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                questions.add(parts[0]);
                answers.add(parts[1]);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + filename + " file not found.");
        }
    }

    /**
     * @return an ArrayList of questions
     */
    public ArrayList<String> getQuestions() {
        return questions;
    }

    /**
     * @return an ArrayList of answers
     */
    public ArrayList<String> getAnswers() {
        return answers;
    }

    public static void savePlayerInventory(Player player, String filePath) {
        if (player == null || player.getInventory().isEmpty()) {
            System.out.println("Cannot save player inventory with invalid player or empty inventory.");
            return;
        }
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("Cannot save player inventory to invalid file path.");
            return;
        }

        File file = new File(filePath);
        try {
            if (!file.exists() && !file.createNewFile()) {
                System.out.println("Error creating file: " + filePath);
                return;
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + filePath);
            e.printStackTrace();
            return;
        }


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            List<String> inventoryData = player.getInventory().stream()
                    .map(resource -> resource.getResourceName() + ":" + resource.getQuantity())
                    .collect(Collectors.toList());
            String inventoryString = "|" + String.join("|", inventoryData) + "|"; // Add pipes at the beginning and end of inventory string
            writer.write(inventoryString);

            System.out.println("Player inventory saved successfully to file: " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving player inventory to file: " + filePath);
            e.printStackTrace();
        }
    }


    public static void loadPlayerInventory(Player player, String filePath) {
        if (player == null) {
            System.out.println("Cannot load player inventory with invalid player.");
            return;
        }
        if (filePath == null ) {
            System.out.println("Cannot load player inventory from invalid file path.");
            return;
        }

        File file = new File(filePath);
        try {
            if (!file.exists() && !file.createNewFile()) {
                System.out.println("Error creating file: " + filePath);
                return;
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + filePath);
            e.printStackTrace();
            return;
        }



        // create a new inventory for the player and clear the old one
        List<Resource> inventory = new ArrayList<>();


        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String inventoryString = reader.readLine();
            if (inventoryString != null && !inventoryString.isEmpty()) {
                String[] inventoryData = inventoryString.split("\\|");
                for (String data : inventoryData) {
                    if (!data.isEmpty()) {
                        String[] parts = data.split(":");
                        if (parts.length == 2) {
                            String resourceName = parts[0];
                            int quantity = Integer.parseInt(parts[1]);
                            inventory.add(new Resource(resourceName, quantity));
                        }
                    }
                }
                player.setInventory(inventory);
                System.out.println("Player inventory loaded successfully from file: " + filePath);
            } else {
                System.out.println("Player inventory file is empty: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("Error loading player inventory from file: " + filePath);
            e.printStackTrace();
        }
    }
    /**
     * Deletes the file at the specified file path.
     * @param filePath the path of the file to be deleted
     *                 this is going to be used to rest / start a new game with new progress
     */
    public static void deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("Cannot delete file at invalid file path.");
            return;
        }

        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
                System.out.println("File deleted successfully: " + filePath);
            } catch (IOException e) {
                System.out.println("Error deleting file: " + filePath + ". " + e.getMessage());
            }
        } else {
            System.out.println("File does not exist at path: " + filePath);
        }
    }

    public static void saveCraftedItem(Player player, Craft selectedCraft, String filePath) {
        if (player == null || selectedCraft == null) {
            System.out.println("Cannot save crafted item with invalid player or craft.");
            return;
        }
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("Cannot save crafted item to invalid file path.");
            return;
        }

        // update player's inventory
        List<Resource> inventory = player.getInventory();
        List<Resource> requiredResources = selectedCraft.getRequiredResources();
        for (Resource resource : requiredResources) {
            for (Resource inventoryResource : inventory) {
                if (inventoryResource.getResourceName().equals(resource.getResourceName())) {
                    int newQuantity = inventoryResource.getQuantity() - resource.getQuantity();
                    if (newQuantity < 0) {
                        newQuantity = 0;
                    }
                    inventoryResource.setQuantity(newQuantity);
                    break;
                }
            }
        }
        inventory.add(new Resource(selectedCraft.getName(), 1)); // add crafted item to inventory

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            List<String> inventoryData = inventory.stream()
                    .map(resource -> resource.getResourceName() + ":" + resource.getQuantity())
                    .collect(Collectors.toList());
            String inventoryString = "|" + String.join("|", inventoryData) + "|"; // Add delimiter at the beginning and end of inventory string
            writer.write(inventoryString);

            System.out.println("Crafted item saved successfully to file: " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving crafted item to file: " + filePath);
            e.printStackTrace();
        }
    }


    public static void savePlayerData(Player player, String playerName, String filePath) {
        if (player == null ){
            System.out.println("Cannot save player data with invalid player.");
            return;
        }
        if (playerName == null || playerName.isBlank()) {
            System.out.println("Cannot save player data with invalid player name.");
            return;
        }
        if (filePath == null || filePath.isBlank()) {
            System.out.println("Cannot save player data to invalid file path.");
            return;
        }

        File file = new File(filePath);
        try {
            if (!file.exists() && !file.createNewFile()) {
                System.out.println("Error creating file: " + filePath);
                return;
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + filePath);
            e.printStackTrace();
            return;
        }


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            if (player.getName().equals(playerName)) {
                // Write player level, name, and resources collected to file
                writer.write("Name:" + player.getName() + "\n");
                writer.write("Level:" + player.getLevel() + "\n");
                writer.write("Resources Collected:" + player.getResourcesCollected() + "\n");
                System.out.println("Player data saved successfully to file: " + filePath);
            } else {
                // If player with specified name not found, display error message
                System.out.println("Player data not found for player with name: " + playerName);
            }
        } catch (IOException e) {
            System.out.println("Error saving player data to file: " + filePath);
            e.printStackTrace();
        }
    }

    public static void loadPlayerData(Player player, String playerName, String filePath) {
        if (player == null) {
            System.out.println("Cannot load player data with invalid player.");
            return;
        }
        if (playerName == null || playerName.isBlank()) {
            System.out.println("Cannot load player data with invalid player name.");
            return;
        }
        if (filePath == null || filePath.isBlank()) {
            System.out.println("Cannot load player data from invalid file path.");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Name:")) {
                    String name = line.substring(5);
                    int playerLevel = 1;
                    int resourcesCollected = 0;
                    line = reader.readLine();
                    if (line != null && line.startsWith("Level:")) {
                        playerLevel = Integer.parseInt(line.substring(6));
                    }
                    line = reader.readLine();
                    if (line != null && line.startsWith("Resources Collected:")) {
                        resourcesCollected = Integer.parseInt(line.substring(20));
                    }
                    if (player.getName().equals(name)) {
                        player.setLevel(playerLevel);
                        player.setResourcesCollected(resourcesCollected);
                        System.out.println("Player data loaded successfully from file: " + filePath);
                        return;
                    }
                    System.out.println("Player data not found for player with name: " + name);
                    return;
                }
            }
            System.out.println("No player data found in file: " + filePath);
        } catch (IOException e) {
            System.out.println("Error loading player data from file: " + filePath);
            e.printStackTrace();
        }
    }



}

