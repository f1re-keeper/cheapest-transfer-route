package org.example.cheapesttransferroute.Controller;

import org.example.cheapesttransferroute.Model.*;
import org.example.cheapesttransferroute.Service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController {
    private final TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/getBestRoute")
    public ResponseEntity<CheapestRoute> getBestRoute() {
        return ResponseEntity.ok(transferService.findCheapestRoute());
    }

    @PostMapping("/input")
    public ResponseEntity<Void> chosenRoute(@RequestBody Route request) {
        transferService.processRequest(request);
        return ResponseEntity.ok().build();
    }
}
