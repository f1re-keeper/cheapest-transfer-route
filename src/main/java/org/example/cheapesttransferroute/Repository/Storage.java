package org.example.cheapesttransferroute.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.example.cheapesttransferroute.Model.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Storage {
    private final String jsonPath = "src/main/resources/data.json";
    @Getter
    private final Map<Integer, List<Transfer>> data = new HashMap<>();

    public Storage() {
        init();
    }

    public void init() {
        File file = new File(jsonPath);
        if (!file.exists() || file.length() == 0) {
            // File doesn't exist or is empty, initialize with empty data
            data.put(0, new ArrayList<>());
            return;
        }

        try {
            String fileData = new String(Files.readAllBytes(Paths.get(jsonPath)));
            ObjectMapper objMapper = new ObjectMapper();
            objMapper.registerModule(new JavaTimeModule());
            Map<String, Object> jsonData = objMapper.readValue(fileData, new TypeReference<Map<String, Object>>() {});

            int maxWeight = (Integer) jsonData.get("maxWeight");
            List<Transfer> transfers = objMapper.convertValue(jsonData.get("availableTransfers"), new TypeReference<List<Transfer>>() {});

            data.put(maxWeight, transfers);
        } catch (IOException e) {
            e.printStackTrace();
            // If there's an error reading the file, initialize with empty data
            data.put(0, new ArrayList<>());
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