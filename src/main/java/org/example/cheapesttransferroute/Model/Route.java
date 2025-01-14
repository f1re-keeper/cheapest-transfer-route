package org.example.cheapesttransferroute.Model;

import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Route {
    private int maxWeight;
    private List<Transfer> transfers;
}
