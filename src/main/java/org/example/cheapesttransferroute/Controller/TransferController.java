package org.example.cheapesttransferroute.Controller;

import jakarta.validation.Valid;
import org.example.cheapesttransferroute.ErrorHandlers.ValidationExceptionHandler;
import org.example.cheapesttransferroute.Model.*;
import org.example.cheapesttransferroute.Service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api")
@Validated
public class TransferController {
    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);
    private final TransferService transferService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping("/getBestRoute")
    public ResponseEntity<CheapestRoute> getBestRoute() {
        logger.info("Getting the cheapest route");
        return ResponseEntity.ok(transferService.findCheapestRoute());
    }

    @PostMapping(value = "/requestInput", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> calculatedRoute(@Valid @RequestBody Route request) {
        try {
            logger.info("Saving request");
            transferService.saveData(request);
            logger.info("Processing request");
            transferService.processRequest(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error processing request", e);
            transferService.clearData();
            throw e;
        }
    }
}
