package aicraftgame;

import java.util.List;

public class Craft {
    private final String name;

    private final int resourcesRequired;
    private final Resource[] requiredResources;
//Constructor for Crafting options
    public Craft(String name, int resourcesRequired, Resource[] requiredResources) {
        this.name = name;
        this.resourcesRequired = resourcesRequired;
        this.requiredResources = requiredResources;
    }

    public String getName() {
        return name;
    }


    public int getResourcesRequired() {
        return resourcesRequired;
    }

    public List<Resource> getRequiredResources() {
        return List.of(requiredResources);
    }
}

