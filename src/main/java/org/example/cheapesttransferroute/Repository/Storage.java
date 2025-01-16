package org.example.cheapesttransferroute.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.example.cheapesttransferroute.ErrorHandlers.ValidationExceptionHandler;
import org.example.cheapesttransferroute.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class Storage {
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);
    private final String jsonPath = "src/main/resources/data.json";
    @Getter
    private final Map<Integer, List<Transfer>> data = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();


    public Storage() {
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
            ObjectMapper objMapper = new ObjectMapper();
            objMapper.registerModule(new JavaTimeModule());
            Map<String, Object> jsonData = objMapper.readValue(fileData, new TypeReference<Map<String, Object>>() {});

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
        data.clear();
        data.put(route.getMaxWeight(), route.getAvailableTransfers());
        saveToFile(route);
    }

    private void saveToFile(Route route) {
        try {
            ObjectMapper objMapper = new ObjectMapper();
            objMapper.registerModule(new JavaTimeModule());
            objMapper.writeValue(new File(jsonPath), route);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}