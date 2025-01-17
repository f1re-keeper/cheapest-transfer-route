package org.example.cheapesttransferroute.Service;

import org.example.cheapesttransferroute.Repository.TransferRep;
import org.example.cheapesttransferroute.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);
    private TransferRep transferRep;

    public TransferService(TransferRep transferRep) {
        this.transferRep = transferRep;
    }

    public void processRequest(Route req){
        List<Transfer> available = req.getAvailableTransfers();
        for(Transfer transfer : available){
            transferRep.createTransfer(transfer);
        }
        transferRep.setMaxWeight(req.getMaxWeight());
        logger.info("Processing complete");
    }

    public void saveData(Route req){
        transferRep.saveStorage(req);
        logger.info("Saving complete");
    }

    public void clearData() {
        transferRep.clearStorage();
        logger.info("Clearing complete");
    }

    public CheapestRoute findCheapestRoute() {
        logger.info("Using Knapsack Algorithm to find the cheapest route");
        int maxWeight = transferRep.getMaxWeightFromStorage();
        List<Transfer> transferList = transferRep.getAllTransfers();

        CheapestRoute ans = new CheapestRoute();
        List<Transfer> selected = new ArrayList<>();
        int totalWeight = 0;
        int totalCost = 0;


        //Knapsack Algorithm
        int n = transferList.size();
        int[][] knapsack = new int[n+1][maxWeight+1];

        for (int i = 1; i <= n; i++) {
            Transfer transfer = transferList.get(i - 1);
            for (int w = 0; w <= maxWeight; w++) {
                if (transfer.getWeight() > w) {
                    knapsack[i][w] = knapsack[i - 1][w];
                } else {
                    knapsack[i][w] = Math.max(
                            knapsack[i - 1][w],
                            knapsack[i - 1][w - transfer.getWeight()] + transfer.getCost()
                    );
                }
            }
        }

        totalCost = knapsack[n][maxWeight];
        int tempW = maxWeight;
        for (int i = n; i > 0 && tempW > 0; i--) {
            if (knapsack[i][tempW] != knapsack[i - 1][tempW]) {
                Transfer transfer = transferList.get(i - 1);
                selected.add(transfer);
                tempW -= transfer.getWeight();
                totalWeight += transfer.getWeight();
            }
        }

        ans.setSelected(selected);
        ans.setTotalCost(totalCost);
        ans.setTotalWeight(totalWeight);

        logger.info("Cheapest route: " + ans.toString());
        return ans;
    }
}
