package diet.dash.service;

import diet.dash.model.DietPlan;
import diet.dash.model.DietPlanRequest;

public interface DietPlanService {
    DietPlan generateDietPlan(DietPlanRequest request);
}
