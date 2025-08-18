package diet.dash.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietPlan {
    private int dailyCalories;
    private Macros macros;
    private List<Meal> meals;
    private String notes;
    private double estimatedCostEUR;
}
