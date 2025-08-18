package diet.dash.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Macros {
    private double protein;
    private double carbs;
    private double fat;
}
