package org.bee.controllers;

import org.bee.utils.InfoUpdaters.UpdaterBase;
import org.bee.utils.JSONHelper;
import org.bee.utils.JSONReadable;
import org.bee.utils.JSONWritable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for controllers that handle loading and saving data to JSON files.
 * @param <T> The type of entity managed by this controller (must be both JSONReadable and JSONWritable)
 */
public abstract class BaseController<T extends JSONReadable & JSONWritable> {

    protected static final String DATABASE_DIR = System.getProperty("database.dir", "database");
    protected final List<T> items = new ArrayList<>();

    /**
     * Protected constructor to enforce singleton pattern in subclasses
     */
    protected BaseController() {
        init();
    }

    /**
     * Initializes the controller by ensuring the database directory exists
     * and loading or generating data as needed.
     */
    protected void init() {
        File directory = new File(DATABASE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File dataFile = new File(getDataFilePath());
        if (dataFile.exists()) {
            loadData();
        } else {
            System.out.println("First run detected, data file not found");
            generateInitialData();
            saveData();
        }
    }

    /**
     * Gets the path to the data file for this controller.
     *
     * @return The file path as a string
     */
    protected abstract String getDataFilePath();


    /**
     * Loads data from the JSON file into the items list.
     */
    public void loadData() {
        try {
            List<T> loadedItems = JSONHelper.loadListFromJsonFile(getDataFilePath(), getEntityClass());
            items.clear();
            items.addAll(loadedItems);
            System.out.println("Loaded " + items.size() + " items from " + getDataFilePath());
        } catch (IOException e) {
            System.err.println("Error loading data from file: " + e.getMessage());
            items.clear();
        }
    }

    /**
     * Saves the items list to the JSON file.
     */
    public void saveData() {
        try {
            JSONHelper.saveToJsonFile(items, getDataFilePath());
            System.out.println("Saved " + items.size() + " items to " + getDataFilePath());
        } catch (IOException e) {
            System.err.println("Error saving data to file: " + e.getMessage());
        }
    }

    /**
     * Adds an item to the controller and saves to the JSON file.
     *
     * @param item The item to add
     */
    public void addItem(T item) {
        items.add(item);
        saveData();
    }


    /**
     * Generic update method for any entity type
     *
     * @param <Z> The type of entity
     * @param entity The entity to update
     * @param updater The updater containing the fields to update
     * @return true if the entity was updated successfully
     */
    public <Z> boolean updateEntity(Z entity, UpdaterBase<Z, ?> updater) {
        if (entity != null) {
            updater.applyTo(entity);
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Gets all items managed by this controller.
     *
     * @return A new list containing all items
     */
    public List<T> getAllItems() {
        return new ArrayList<>(items);
    }

    /**
     * Generates initial data for the controller.
     * This method should be implemented by subclasses.
     */
    protected abstract void generateInitialData();

    /**
     * Gets the class of the entity managed by this controller.
     *
     * @return The class of T
     */
    protected abstract Class<T> getEntityClass();
}