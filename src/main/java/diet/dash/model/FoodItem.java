package diet.dash.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {
    private String product;
    private String amount;
    private int calories;
    private double protein;
    private double carbs;
    private double fat;
}
