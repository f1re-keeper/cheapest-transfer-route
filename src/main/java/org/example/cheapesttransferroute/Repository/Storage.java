package org.example.cheapesttransferroute.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.cheapesttransferroute.Model.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Storage {
    private final String jsonPath = "src/main/resources/data.json";
    private final Map<String, Object> data = new HashMap<>();

    public Storage() {
        data.put("availableTransfers", new Object());
    }

    public void init() {
        try {
            String fileData = new String(Files.readAllBytes(Paths.get(jsonPath)));
            ObjectMapper objMapper = new ObjectMapper();
            objMapper.registerModule(new JavaTimeModule());
            Map<String, List<Object>> jsonData = objMapper.readValue(fileData, new TypeReference<Map<String, List<Object>>>() {});

            List<Transfer> transfers = objMapper.convertValue(jsonData.get("availableTransfers"), new TypeReference<List<Transfer>>() {});
            transfers.forEach(transfer -> data.put("transfer", transfer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
