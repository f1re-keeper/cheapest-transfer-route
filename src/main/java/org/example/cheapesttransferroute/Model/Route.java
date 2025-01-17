package org.example.cheapesttransferroute.Model;



import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    @NotNull(message = "Max weight must not be null")
    @Min(value = 1, message = "Max weight has to be at least 1")
    private Integer maxWeight;

    @NotNull(message = "Available transfers must not be null")
    @Size(min = 1, message = "Available transfers must not be empty")
    private List<@Valid Transfer> availableTransfers;
}
