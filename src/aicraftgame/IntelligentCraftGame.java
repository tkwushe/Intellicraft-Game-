package aicraftgame;

import java.util.*;

import static aicraftlogin.LoginSystem.clearScreen;




public class IntelligentCraftGame {
    private static final String PROGRESS_FILE_NAME = "gameProgress.txt";
    private static final String INVALID_INPUT_MSG = "Invalid input. Please try again.";

    private static final int MAX_QUESTIONS = 20;
    private static final int MAX_TRIES_PER_QUESTION = 3;


    private static final int MAX_RESOURCE_QUANTITY = 5 ;
    private static final int MAX_AREAS = 3;
    private static final String[] AREANAMES = {"Cave", "Volcano", "Beach", "Space" , "Jungle" , "Castle" };
    private static final String[] RESOURCES = {"Aluminum sheets",
            "Screws",
            "Glue",
            "Hydrogen",
            "Oxygen",
            "Nozzle",
            "Titanium plates",
            "Copper wiring",
            "Carbon Fiber",
            "Aluminium Sheets",
            "Titanium",
            "Carbon",
            "Nitrogen"};



    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();





    public static class Area {
        private final List<Resource> resources;
        private final String areaName;

        public Area(String areaName, List<Resource> initialResources) {
            this.areaName = areaName;
            this.resources = initialResources;
        }

        public void printResources(Player player) {
            System.out.println("Resources in " + areaName + ":");
            System.out.println("Select a resource to collect:");
            int numResourcesShown = 0;
            int selectedResourceIndex = -1;
            while (selectedResourceIndex < 0 || selectedResourceIndex > numResourcesShown) {
                try {
                    numResourcesShown = 0;
                    for (int i = 0; i < resources.size(); i++) {
                        Resource resource = resources.get(i);
                        if (numResourcesShown >= 3) {
                            break; // limit the number of resources shown to 3
                        }
                        System.out.println((i + 1) + ". " + resource.getQuantity() + " " + resource.getResourceName());
                        numResourcesShown++;
                    }
                    selectedResourceIndex = scanner.nextInt() - 1;
                    if (selectedResourceIndex < 0 || selectedResourceIndex >= numResourcesShown) {
                        System.out.println(INVALID_INPUT_MSG + numResourcesShown + ".");
                    }
                } catch (InputMismatchException e) {
                    scanner.next(); // clear invalid input from scanner
                    System.out.println(INVALID_INPUT_MSG + numResourcesShown + ".");
                }
            }
            Resource selectedResource = resources.get(selectedResourceIndex);
            clearScreen();
            System.out.println("You have selected: " + selectedResource.getQuantity() + " " + selectedResource.getResourceName());
            player.collectResource(selectedResource, player);
            this.resources.remove(selectedResource);
            clearScreen();
        }

        // Getter method for area name
        public String getAreaName() {
            return areaName;
        }

    }


    public void displayGameMenu(String name) {

        Scanner inputScanner = new Scanner(System.in);
        boolean continuePlaying = true; // add boolean variable to track game status


        CraftingSystem craftingSystem = new CraftingSystem();

        //Create Crafting options
        craftingSystem.addCrafts();

        do {
            // Display menu
            System.out.println("Welcome to the Game Menu, " + name + "!");
            System.out.println("What would you like to do ?");
            System.out.println("1. Start new game");
            System.out.println("2. Continue game");
            System.out.println("3. Craft Components/View Inventory") ;
            System.out.println("4. Log out and exit game");
            System.out.print("Please enter your choice: ");

            // Handle user input
            try {
                int choice = inputScanner.nextInt();
                switch (choice) {
                    case 1 -> startNewGame(name);
                    case 2 -> startGame(name);
                    case 3 -> displayCraftMenu(name);
                    case 4 -> {
                        // Log out and exit game
                        System.out.println("Goodbye!");
                        continuePlaying = false; // set boolean variable to false. This is  to end the loop
                    }
                    default -> System.out.println("Invalid input. Please enter a number between 1 and 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                inputScanner.nextLine(); // Clear input buffer
            }
        } while (continuePlaying); // use boolean variable as end condition
    }


    private List<Area> createAreas() {
        List<Area> areas = new ArrayList<>();

        for (int i = 0; i < MAX_AREAS; i++) {
            int areaIndex = random.nextInt(AREANAMES.length);
            List<Resource> resources = new ArrayList<>();
            int numResources = 4;
            int resourceQuantity = random.nextInt(MAX_RESOURCE_QUANTITY) + 1;
            for (int j = 0; j < numResources; j++) {
                int resourceIndex = random.nextInt(RESOURCES.length);
                Resource resource = new Resource(RESOURCES[resourceIndex], resourceQuantity);
                resources.add(resource);

            }
            Area area = new Area(AREANAMES[areaIndex], resources);
            areas.add(area);
        }

        return areas;
    }
    // Print available areas
    private static void printAvailableAreas(List<Area> areas) {
        System.out.println("Available areas:");
        for (int i = 0; i < areas.size(); i++) {
            Area area = areas.get(i);
            System.out.println((i+1) + ". " + area.getAreaName());
        }
    }


    /**
     * Allows the player to select an area from a list of available areas.
     *
     * @param areas A list of available areas to choose from.
     * @return The selected area, or null if the player cancels the selection.
     */
    public Area selectArea(List<Area> areas) {


        while (true) {
            System.out.print("Enter the number of the area you want to select (or 0 to cancel): ");
            int selection = scanner.nextInt();

            if (selection == 0) {
                System.out.println("Selection cancelled.");
                System.exit(0);
            }

            if (selection < 1 || selection > areas.size()) {
                System.out.println("Invalid selection. Please enter a number between 1 and " + areas.size() + ".");
                continue;
            }

            return areas.get(selection - 1);
        }
    }



    private static boolean askQuestions(Area selectedArea, Player player, GameData gameData) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        int numQuestions = random.nextInt(MAX_QUESTIONS) + 1;
        for (int i = 0; i < numQuestions; i++) {
            int questionIndex = random.nextInt(gameData.getQuestions().size());
            String question = gameData.getQuestions().get(questionIndex);
            String answer = gameData.getAnswers().get(questionIndex);

            int numTries = 0;
            int hintGiverCount = 0;
            boolean answeredCorrectly = false;
            while (numTries < MAX_TRIES_PER_QUESTION && !answeredCorrectly) {
                System.out.print(question + " (attempt " + (numTries + 1) + "/" + MAX_TRIES_PER_QUESTION + "): ");
                String playerAnswer = scanner.next().trim();
                if (playerAnswer.isEmpty()) {
                    System.out.println("You must enter an answer.");
                } else if (playerAnswer.equalsIgnoreCase(answer)) {
                    System.out.println("Correct!");
                    selectedArea.printResources(player);
                    player.increasePlayerLevel(player);



                    System.out.println("Do you want to continue (y/n)?");
                    String continueAnswer = scanner.next().trim();
                    while (!continueAnswer.equalsIgnoreCase("y") && !continueAnswer.equalsIgnoreCase("n")) {
                        System.out.println("Invalid input. Please enter 'y' or 'n'.");
                        continueAnswer = scanner.next().trim();
                    }
                    if (continueAnswer.equalsIgnoreCase("n")) {
                        System.out.println("Game over!");
                        return false; // exit the method and end the game
                    }

                    answeredCorrectly = true;
                } else {
                    System.out.println("Incorrect!");
                    numTries++;
                    if (numTries < MAX_TRIES_PER_QUESTION) {
                        // Provide a hint with more characters as the child gets it wrong
                        hintGiverCount++;
                        int numChars = hintGiverCount + 1; // increase the number of characters in the hint
                        if (numChars > answer.length()) {
                            numChars = answer.length(); // cap the number of characters at the length of the answer
                        }
                        String hint = answer.substring(0, numChars);
                        System.out.println("Hint: The first " + numChars + " letters of the answer are '" + hint + "'");
                    }
                }
            }


            if (!answeredCorrectly) {
                System.out.println("You ran out of attempts for this question. The answer was " + answer);
                System.out.println("Do you want to continue (y/n)?");
                String continueAnswer = scanner.next().trim();
                while (!continueAnswer.equalsIgnoreCase("y") && !continueAnswer.equalsIgnoreCase("n")) {
                    System.out.println("Invalid input. Please enter 'y' or 'n'.");
                    continueAnswer = scanner.next().trim();
                }
                if (continueAnswer.equalsIgnoreCase("n")) {
                    System.out.println("Game over!");
                    return false; // exit the method and end the game
                }
            }
        }
        return true;
    }

    private void startNewGame(String name) {


        // Reset player data
        Player player = new Player(name);
        player.setResourcesCollected(0);
        player.setInventory(new ArrayList<>());
        player.setLevel(1);

        // If there's a file with saved inventory this deletes that so that the player can start afresh
        GameData.deleteFile(player.getName() + PROGRESS_FILE_NAME);
        // Save player progress to file
        GameData.savePlayerInventory(player, player.getName() + PROGRESS_FILE_NAME);
        GameData.savePlayerData(player, name, player.getName()+ "playerData.txt");

        // Start the game
        startGame(name);
    }


    public void startGame(String name) {
        // Initialize game data
        GameData gameData = new GameData("questions.txt");


        // Create areas
        List<Area> areasList = createAreas();



        // Create player
        Player player = new Player(name);

        // initialise player data
        GameData.loadPlayerData(player,name,player.getName() +"playerData.txt") ;



        // Start game loop
        while (true) {
            // Print available areas
            printAvailableAreas(areasList);
            // Ask player to select an area
            Area selectedArea = selectArea(areasList);
            clearScreen();

            // Ask questions to collect resources
            boolean continueExploring = askQuestions(selectedArea, player, gameData);
            clearScreen();

             if (!continueExploring) {
                 GameData.savePlayerData(player, name,name + "PlayerData.txt");
                break;
            }
        }


    }




    public void displayCraftMenu(String name ) {
        // Create a new Player object with the given name
        Player player = new Player(name);

        // Create a new CraftingSystem object
        CraftingSystem craftingSystem = new CraftingSystem();

        // Loop through the menu until the user chooses to exit
        while (true) {
            System.out.println("Craft Menu");
            System.out.println("----------");
            System.out.println("1. Display available crafts");
            System.out.println("2. Display player inventory");
            System.out.println("3. Craft item");
            System.out.println("4. Go back to game ");
            System.out.println("5. View Player Stats ");
            System.out.println("6. Return to main menu");

            // Read the user's input
            String choice = scanner.nextLine();

            // Execute the appropriate action based on the user's input
            switch (choice) {
                case "1" -> craftingSystem.displayAvailableCrafts();
                case "2" -> player.displayPlayerInventory(player);
                case "3" -> craftingSystem.craftItem(player);
                case "4" -> startGame(name);
                case "5" -> player.displayPlayerStatistics(player);
                case "6"-> {
                    // Exit the game
                    System.exit(0);
                    // Print a message to let the user know the game is exiting
                    System.out.println("Exiting game...");
                    // Return from the method
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }


}
