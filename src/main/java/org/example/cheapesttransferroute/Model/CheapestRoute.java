package org.example.cheapesttransferroute.Model;

import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheapestRoute {
    private List<Transfer> selected;
    private int totalCost;
    private int totalWeight;
}
