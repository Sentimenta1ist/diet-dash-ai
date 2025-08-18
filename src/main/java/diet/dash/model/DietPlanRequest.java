package diet.dash.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietPlanRequest {
    private String gender;
    private int age;
    private int height_cm;
    private int weight_kg;
    private String weightGoal;
    private Target target;
    private int mealsCount;
    private String comments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Target {
        private int calories;
        private int protein_g;
        private int carbs_g;
        private int fat_g;
    }
}
