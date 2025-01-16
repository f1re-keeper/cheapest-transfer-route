package org.example.cheapesttransferroute.Controller;

import org.example.cheapesttransferroute.Model.*;
import org.example.cheapesttransferroute.Service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

@RestController
public class TransferController {
    private final TransferService transferService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/getBestRoute")
    public ResponseEntity<CheapestRoute> getBestRoute() {
        System.out.println("getBestRoute");
        return ResponseEntity.ok(transferService.findCheapestRoute());
    }

    @PostMapping("/input")
    public ResponseEntity<Void> chosenRoute(@RequestBody Route request) {
        try {
            objectMapper.writeValue(new File("src/main/resources/data.json"), request);
            transferService.processRequest(request);
            return ResponseEntity.ok().build();
        }catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
