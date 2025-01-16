package org.example.cheapesttransferroute.Repository;


import lombok.Setter;
import org.example.cheapesttransferroute.Model.Route;
import org.example.cheapesttransferroute.Model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository
public class TransferRep{
    @Setter
    private int maxWeight;
    private List<Transfer> availableTransfers = new ArrayList<>();

    @Autowired
    private Storage storage;
    public void TransferRep(Storage storage) {
        this.storage = storage;
    }

    public void createTransfer(Transfer transfer){
        availableTransfers.add(transfer);
    }

    public List<Transfer> getAllTransfers(){
        Map<Integer, List<Transfer>> data = storage.getData();
        for(List<Transfer> value : data.values()){
            availableTransfers.addAll(value); //Map "data" only has one entry
        }
        return availableTransfers;
    }

    public int getMaxWeight(){
        Map<Integer, List<Transfer>> data = storage.getData();
        Iterator<Integer> it = data.keySet().iterator();
        if (data.isEmpty()) {
            return 0;
        }
        maxWeight = it.next();
        return maxWeight;
    }

    public void saveStorage(Route req){
        storage.updateData(req);
    }

    public void clearStorage() {
        storage.clearData();
    }
}
