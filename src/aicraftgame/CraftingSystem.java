package aicraftgame;

import java.util.*;
import java.util.stream.Collectors;

public class CraftingSystem{
    public static final  ArrayList<Craft> availableCrafts = new ArrayList<>();

    private static final String PROGRESS_FILE_NAME = "gameProgress.txt";

    private static final Scanner scanner = new Scanner(System.in);


    private int rocketProgress;


    public CraftingSystem() {
    }



    public  void addCrafts() {
        // Create an array of Craft objects for the new crafts
        Craft[] newCrafts = {
                new Craft("Rocket body",  2, new Resource[] {
                        new Resource("Aluminum Sheets", 6),
                        new Resource("Screws", 20)
                }),
                new Craft("Nose cone", 3, new Resource[] {
                        new Resource("Aluminum Sheets", 2),
                        new Resource("Screws", 12),
                        new Resource("Glue", 2)
                }),
                new Craft("Engine", 3, new Resource[] {
                        new Resource("Oxygen", 3),
                        new Resource("Glue", 2),
                        new Resource("Nozzle", 1)
                }),
                new Craft("Fuel tank", 4, new Resource[] {
                        new Resource("Aluminum Sheets", 6),
                        new Resource("Titanium plates", 3),
                        new Resource("Copper wiring", 20),
                        new Resource("Hydrogen",2)
                }),


                new Craft("Fins", 4, new Resource[] {
                        new Resource("Aluminium Sheets", 2),
                        new Resource("Titanium plates", 2),
                        new Resource("Carbon Fiber", 4),
                        new Resource("Glue", 2)
                }),

                new Craft("Payload", 4, new Resource[] {
                        new Resource("Aluminum Sheets", 1),
                        new Resource("Titanium plates", 1),
                        new Resource("Carbon", 1),
                        new Resource("Glue", 1)
                }),
                new Craft("Oxidizer", 4, new Resource[] {
                new Resource(" Oxygen", 3),
                new Resource("Nitrogen", 2),
                new Resource("Aluminum Sheets", 1),
                new Resource("Glue", 1)
        })

        };

        // Add each new craft to the available crafts list
        Collections.addAll(availableCrafts, newCrafts);
    }
    public void increaseRocketProgress(int amount) {
        rocketProgress += amount;
    }

    public int getRocketProgress() {
        return rocketProgress;
    }



    public void displayRocketProgress(Player player) {
GameData.loadPlayerInventory(player , player.getName() + PROGRESS_FILE_NAME);
        List<Resource> inventory = player.getInventory();
        // Iterate through the inventory to check if a craft name is present
        for (Resource item : inventory) {
            for (Craft craft : availableCrafts) {
                if (item.getResourceName().equals(craft.getName())) {
                    increaseRocketProgress(1);
                    break;
                }
            }
        }

        int progressPercentage = (int) (((double) getRocketProgress() / 7) * 100);
        if (progressPercentage == 100) {
            System.out.println("Congratulations " + player.getName() + "! You have successfully completed the game.");
        }

        System.out.println("Rocket progress: " + progressPercentage + "%");
    }


    public void craftItem(Player player) {
        GameData.loadPlayerInventory(player, player.getName() + PROGRESS_FILE_NAME);
        GameData.loadPlayerData(player, player.getName(), player.getName() + "playerData.txt");

        // check if available crafts have been successfully added to the list
        if (availableCrafts.isEmpty()) {
            System.out.println("There are no crafts available.");
            return;
        }

        // show the player the available crafts
        displayAvailableCrafts();

        System.out.print("Enter the index of the craft you want to make: ");
        int craftIndex = scanner.nextInt();

        // Validation for Menu
        if (craftIndex < 1 || craftIndex > availableCrafts.size()) {
            System.out.println("Invalid craft index.");
            return;
        }

        Craft selectedCraft = availableCrafts.get(craftIndex - 1);

        // check to see if player matches the required criteria to craft item
        List<Resource> requiredResources = selectedCraft.getRequiredResources();


if (selectedCraft.getResourcesRequired() > player.getResourcesCollected())
{ System.out.println("Player has not collected enough resources to craft");
    return;}


        // group the player's inventory by resource name
        Map<String, List<Resource>> inventoryGrouped = player.getInventory().stream()
                .collect(Collectors.groupingBy(Resource::getResourceName));

            List<Resource> missingResources = new ArrayList<>();
        for (Resource requiredResource : requiredResources) {
            String resourceName = requiredResource.getResourceName();
            int requiredQuantity = requiredResource.getQuantity();

            if (!inventoryGrouped.containsKey(resourceName)) {
                missingResources.add(requiredResource);
                continue;
            }
            // Group resources collected resources by player
            int availableQuantity = inventoryGrouped.get(resourceName).stream()
                    .mapToInt(Resource::getQuantity)
                    .sum();
            // check if player has enough resources if not add to missing resources list
            if (availableQuantity < requiredQuantity) {
                missingResources.add(new Resource(resourceName, requiredQuantity - availableQuantity));
            }
        }

        if (!missingResources.isEmpty()) {
            System.out.print("You are missing the following resources to craft this item: ");
            // loop over missing resources to show player what they need to collect
            for (Resource missingResource : missingResources) {
                System.out.print(missingResource.getQuantity() + " " + missingResource.getResourceName() + ", ");
            }
            System.out.println();
            return;
        }

        // increase player's level after they have successfully crafted a part to the rocket
        player.increasePlayerLevel(player);

        // increase player rocket  progress
        increaseRocketProgress(1);

        // Remove the selected craft from the available crafts list
        availableCrafts.remove(selectedCraft);


        // Save updated inventory
        GameData.saveCraftedItem(player, selectedCraft, player.getName() + PROGRESS_FILE_NAME);

        GameData.savePlayerData(player, player.getName(), player.getName() + "playerData");
        System.out.println("Successfully crafted " + selectedCraft.getName() + "!");
        displayRocketProgress(player);
    }











     // Displays the available crafts and the resources required for each craft.

    public void displayAvailableCrafts() {

        System.out.println("Available crafts:");
        // Loop through the available crafts and display each one along with its required resources
        for (int i = 0; i < availableCrafts.size(); i++) {
            // Get the  craft from the list of available crafts
            Craft craft = availableCrafts.get(i);

            // Convert the list of required resources for this craft to an array of strings
            Resource[] requiredResources = craft.getRequiredResources().toArray(new Resource[0]);
            String[] requiredResourceStrings = new String[requiredResources.length];
            for (int j = 0; j < requiredResources.length; j++) {
                requiredResourceStrings[j] = requiredResources[j].toString();
            }

            // Display the name of the craft, its position in the list, and its required resources
            System.out.println("- " + (i + 1) + ". " + craft.getName()  + "\n Resources required: " + Arrays.toString(requiredResourceStrings));
        }
    }

}






