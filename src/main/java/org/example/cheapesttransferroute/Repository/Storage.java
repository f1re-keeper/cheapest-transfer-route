package org.example.cheapesttransferroute.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import org.example.cheapesttransferroute.Model.*;
import org.springframework.stereotype.Component;

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
    }

    public void init() {
        try {
            String fileData = new String(Files.readAllBytes(Paths.get(jsonPath)));
            ObjectMapper objMapper = new ObjectMapper();
            objMapper.registerModule(new JavaTimeModule());
            Map<String, List<Object>> jsonData = objMapper.readValue(fileData, new TypeReference<Map<String, List<Object>>>() {});

            int maxWeight = objMapper.convertValue(jsonData.get("maxWeight"), new TypeReference<Integer>() {});
            data.put(maxWeight, new ArrayList<>());
            List<Transfer> transfers = objMapper.convertValue(jsonData.get("availableTransfers"), new TypeReference<List<Transfer>>() {});
            transfers.forEach(transfer -> data.get(maxWeight).add(transfer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
