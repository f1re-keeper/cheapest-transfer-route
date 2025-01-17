package org.example.cheapesttransferroute.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import org.example.cheapesttransferroute.ErrorHandlers.ValidationExceptionHandler;
import org.example.cheapesttransferroute.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
@Validated(ValidationExceptionHandler.class)
public class Storage {
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);
    @Setter //this setter is for tests, but for the application jsonPath should always have the initial value
    private String jsonPath = "src/main/resources/data.json";
    @Getter //this getter is for tests
    private final Map<Integer, List<Transfer>> data = new HashMap<>();
    @Setter //this setter is also for tests
    private ObjectMapper objectMapper = new ObjectMapper();


    public Storage(String typeOfStorage){
        //this string can be anything. the goal of this constructor is to initialize
        //an empty Storage object. This is used for tests
        objectMapper.registerModule(new JavaTimeModule());
    }

    public Storage() {
        objectMapper.registerModule(new JavaTimeModule());
        init();
    }

    public void init() {
        File file = new File(jsonPath);
        if (!file.exists() || file.length() == 0) {
            data.put(0, new ArrayList<>());
            return;
        }

        try {
            String fileData = new String(Files.readAllBytes(Paths.get(jsonPath)));
            Map<String, Object> jsonData = objectMapper.readValue(fileData, new TypeReference<Map<String, Object>>() {});

            Integer maxWeight = (Integer) jsonData.get("maxWeight");
            if (maxWeight == null) {
                logger.warn("maxWeight not found in JSON data. Using default value 0.");
                maxWeight = 0;
            }

            List<Transfer> transfers = objectMapper.convertValue(jsonData.get("availableTransfers"), new TypeReference<List<Transfer>>() {});
            if (transfers == null) {
                logger.warn("availableTransfers not found in JSON data. Using empty list.");
                transfers = new ArrayList<>();
            }

            data.put(maxWeight, transfers);
        } catch (IOException e) {
            e.printStackTrace();
            data.put(0, new ArrayList<>());
        }
    }

    public void clearData() {
        if(!data.isEmpty()){
            logger.info("Clearing data");
            data.clear();
            clearFileContent();
        }
    }

    //Makes the file content empty if the input is invalid
    private void clearFileContent() {
        try {
            objectMapper.writeValue(new File(jsonPath), new HashMap<>());
            logger.info("JSON file content cleared");
        } catch (IOException e) {
            logger.error("Error clearing JSON file content", e);
        }
    }

    public void updateData(Route route) {
        logger.info("Updating data");
        data.clear();
        data.put(route.getMaxWeight(), route.getAvailableTransfers());
        saveToFile(route);
    }

    private void saveToFile(Route route) {
        try {
            objectMapper.writeValue(new File(jsonPath), route);
            logger.info("Update complete");
        } catch (IOException e) {
            logger.info("Error saving JSON file", e);
            e.printStackTrace();
        }
    }
}