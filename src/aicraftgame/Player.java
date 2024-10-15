package aicraftgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static aicraftgame.GameData.loadPlayerInventory;
import static aicraftlogin.LoginSystem.clearScreen;

public class Player {
    private final String name;
    private int resourcesCollected;
    private List<Resource> inventory;


    private static final String PROGRESS_FILE_NAME = "gameProgress.txt";

    private int level;




    public Player(String name) {
        this(name, 0, new ArrayList<>(), 1);
    }

    /**
     * Constructs a new Player object with the specified name, resources collected, inventory, and level.
     *
     * @param name               the player's name
     * @param resourcesCollected the number of resources collected by the player
     * @param inventory          the list of resources in the player's inventory
     * @param level              the player's level in the game
     */
    public Player(String name, int resourcesCollected, List<Resource> inventory, int level) {
        this.name = name;
        this.resourcesCollected = resourcesCollected;
        this.inventory = inventory;
        this.level = level;
    }



    public List<Resource> getInventory() {
        return inventory;
    }

    /**
     * Returns the player's level.
     *
     * @return the player's level
     */
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be greater than or equal to 1");
        }
        this.level = level;
    }

    /**
     * Increases the level of the player by 1.
     *
     * @param player the player whose level will be increased
     */
    public void increasePlayerLevel(Player player) {
        int currentLevel = player.getLevel();
        player.setLevel(currentLevel + 1);
    }

    public void increasePlayerResourcesCollected(Player player) {
        int currentResourcesCollected = player.getResourcesCollected();
        player.setResourcesCollected(currentResourcesCollected + 1);
    }


    /**
     * Returns the number of resources collected by the player.
     *
     * @return the number of resources collected by the player
     */
    public int getResourcesCollected() {
        return resourcesCollected;
    }

    /**
     * Collects a resource and adds it to the player's inventory.
     *
     * @param resource the resource to collect
     */

    public void collectResource(Resource resource, Player player) {

        inventory.add(resource);
      increasePlayerResourcesCollected(player);
    }


    public  void displayPlayerStatistics(Player player) {
        GameData.loadPlayerData(player, player.getName(), player.getName() + "playerData.txt");
        System.out.println("Player statistics:");

            System.out.println("Name: " + player.getName());
            System.out.println("Level: " + player.getLevel());
            System.out.println("Total Resources Collected: " + player.getResourcesCollected());
            System.out.println();


    }




    public String getName() {
        return name;
    }

    public void setResourcesCollected(int newResourcesCollected) {
        this.resourcesCollected = newResourcesCollected;
    }

    public void setInventory(List<Resource> inventory) {
        this.inventory = new ArrayList<>(inventory);
    }



    /**
     * Displays the contents of the player's inventory and the quantity of each resource.
     * @param player the player whose inventory is being displayed
     */
    public void displayPlayerInventory(Player player) {
        // Load the player's inventory from file
        loadPlayerInventory(player, player.getName() + PROGRESS_FILE_NAME);

        // Print a message indicating that the player's inventory is about to be displayed
        System.out.println("Player inventory:");

        // Get a list of the resources in the player's inventory
        List<Resource> playerInventory = player.getInventory();

        // Create a map to store the quantity of each resource
        Map<String, Integer> resourceQuantities = new HashMap<>();

        // Loop through the resources in the player's inventory and add their quantities to the map
        for (Resource resource : playerInventory) {
            String resourceName = resource.getResourceName();
            int quantity = resource.getQuantity();
            resourceQuantities.put(resourceName, resourceQuantities.getOrDefault(resourceName, 0) + quantity);
        }

        // display resources in the player's inventory
        System.out.println("Inventory size: " + playerInventory.size());

        // If the player's inventory is empty, print a message indicating that there are no resources
        if (playerInventory.isEmpty()) {
            System.out.println("Currently there is nothing in your inventory. Please continue playing the game to be able to get resources to craft");
        } else {
            // loop through the resources in the map and print the name of each resource along with its quantity
            for (Map.Entry<String, Integer> entry : resourceQuantities.entrySet()) {
                System.out.println("- " + entry.getKey() + " (" + entry.getValue() + ")");
            }
        }
        clearScreen();
    }






}
