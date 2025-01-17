package org.example.cheapesttransferroute;

import org.example.cheapesttransferroute.Model.*;
import org.example.cheapesttransferroute.Repository.*;
import org.example.cheapesttransferroute.Service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestTransferService {
    private TransferRep transferRep;
    private TransferService transferService;
    private Storage storage = new Storage("Initializing Empty Storage");
    private final String testPath = "src/test/java/jsonFiles/dataTest.json";

    @BeforeEach
    public void setUp() {
        transferRep = new TransferRep(storage);
        transferService = new TransferService(transferRep);
    }

    @Test
    public void testProcessRequest() {
        Route route = new Route();
        route.setMaxWeight(50);

        List<Transfer> transfers = new ArrayList<>();
        transfers.add(new Transfer(10, 100));
        transfers.add(new Transfer(20, 150));
        route.setAvailableTransfers(transfers);

        transferService.processRequest(route);

        assertEquals(2, transferRep.getAvailableTransfers().size());
        assertEquals(50, transferRep.getMaxWeight());
    }

    @Test
    public void testSaveData() {
        storage.setJsonPath(testPath);
        storage.init();

        Route route = new Route();

        route.setMaxWeight(50);
        List<Transfer> transfers = new ArrayList<>();
        transfers.add(new Transfer(10, 100));
        route.setAvailableTransfers(transfers);

        transferService.saveData(route);

        assertEquals(1, transferRep.getAllTransfers().size());
        assertEquals(50, transferRep.getMaxWeightFromStorage());
    }

    @Test
    public void testClearData() {
        storage.setJsonPath(testPath);
        storage.init();

        Route route = new Route();

        route.setMaxWeight(50);
        List<Transfer> transfers = new ArrayList<>();
        transfers.add(new Transfer(10, 100));
        route.setAvailableTransfers(transfers);

        transferService.processRequest(route);
        transferService.saveData(route);
        transferService.clearData();

        assertEquals(0, transferRep.getAllTransfers().size());
    }

    @Test
    public void testFindCheapestRouteValid() {
        storage.setJsonPath("src/test/java/jsonFiles/ValidCase.json");
        storage.init();

        CheapestRoute cheapestRoute = transferService.findCheapestRoute();

        assertNotNull(cheapestRoute);
        assertEquals(3, cheapestRoute.getSelected().size());
        assertEquals(23, cheapestRoute.getTotalWeight());
        assertEquals(45, cheapestRoute.getTotalCost());
    }

    @Test
    public void testFindCheapestRouteEmpty() {
        storage.setJsonPath("src/test/java/jsonFiles/Empty.json");
        storage.init();

        CheapestRoute cheapestRoute = transferService.findCheapestRoute();

        assertNotNull(cheapestRoute);
        assertEquals(0, cheapestRoute.getSelected().size());
        assertEquals(0, cheapestRoute.getTotalWeight());
        assertEquals(0, cheapestRoute.getTotalCost());
    }

    @Test
    public void testFindCheapestRouteEmptyTransfers() {
        storage.setJsonPath("src/test/java/jsonFiles/EmptyTransfers.json");
        storage.init();

        CheapestRoute cheapestRoute = transferService.findCheapestRoute();

        assertNotNull(cheapestRoute);
        assertEquals(0, cheapestRoute.getSelected().size());
        assertEquals(0, cheapestRoute.getTotalWeight());
        assertEquals(0, cheapestRoute.getTotalCost());
    }

    @Test
    public void testFindCheapestRouteNoAnswer() {
        storage.setJsonPath("src/test/java/jsonFiles/NoAnswer.json");
        storage.init();

        CheapestRoute cheapestRoute = transferService.findCheapestRoute();

        assertNotNull(cheapestRoute);
        assertEquals(0, cheapestRoute.getSelected().size());
        assertEquals(0, cheapestRoute.getTotalWeight());
        assertEquals(0, cheapestRoute.getTotalCost());
    }

}

