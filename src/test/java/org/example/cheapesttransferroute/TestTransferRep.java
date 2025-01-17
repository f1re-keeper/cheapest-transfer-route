package org.example.cheapesttransferroute;

import org.example.cheapesttransferroute.Model.Transfer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.example.cheapesttransferroute.Model.Route;

import org.example.cheapesttransferroute.Repository.*;


import java.util.*;

public class TestTransferRep {
    private TransferRep transferRep;
    private Storage storage = new Storage("Initializing Empty Storage");
    private final String testPath = "src/test/java/jsonFiles/dataTest.json";

    @BeforeEach
    public void setUp() {
        storage.setJsonPath(testPath);
        storage.init();
        transferRep = new TransferRep(storage);
    }

    @Test
    void testCreateTransfer() {
        Transfer transfer = new Transfer();
        transferRep.createTransfer(transfer);

        List<Transfer> result = transferRep.getAvailableTransfers();
        assertTrue(result.contains(transfer));
    }

    @Test
    void testGetMaxWeightFromStorage() {
        int maxWeight = transferRep.getMaxWeightFromStorage();
        assertTrue(storage.getData().containsKey(maxWeight));
    }

    @Test
    void testSaveStorage() {
        Route route = new Route();
        route.setMaxWeight(200);
        List<Transfer> transfers = new ArrayList<>();
        Transfer transfer = new Transfer();
        transfer.setWeight(20);
        transfer.setCost(75);
        transfers.add(transfer);
        route.setAvailableTransfers(transfers);

        transferRep.saveStorage(route);
        assertEquals(1, storage.getData().size());
        assertTrue(storage.getData().containsKey(200));
        assertEquals(1, storage.getData().get(200).size());
        assertEquals(1, transferRep.getAllTransfers().size());
    }

    @Test
    void testClearStorage() {
        transferRep.clearStorage();
        assertTrue(storage.getData().isEmpty());
    }

    @Test
    void testMaxWeightWithEmptyStorage(){
        storage.clearData();
        int maxWeight = transferRep.getMaxWeightFromStorage();
        assertEquals(0, maxWeight);
    }
}
