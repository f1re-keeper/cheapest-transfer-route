package org.example.cheapesttransferroute.Model;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transfer {
    @NotNull(message = "Weight must not be null")
    @Min(value = 1, message = "Weight must be at least 1")
    private int weight;

    @NotNull(message = "Cost must not be null")
    @Min(value = 1, message = "Cost must be at least 1")
    private int cost;
}
