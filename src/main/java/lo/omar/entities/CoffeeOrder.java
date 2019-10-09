package lo.omar.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor @NoArgsConstructor
public class CoffeeOrder {

    private String coffeeId;
    private Instant dateOrder;
}
