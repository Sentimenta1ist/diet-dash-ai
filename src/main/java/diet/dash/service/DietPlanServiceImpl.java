package diet.dash.service;

import diet.dash.model.DietPlan;
import diet.dash.model.DietPlanRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DietPlanServiceImpl implements DietPlanService {

    private final ExternalDietPlanClient externalDietPlanClient;

    @Override
    public DietPlan generateDietPlan(DietPlanRequest request) {
        log.info("Generating diet plan for user: gender={}, age={}, height={}cm, weight={}kg, goal={}, meals={}",
                request.getGender(), request.getAge(), request.getHeight_cm(), 
                request.getWeight_kg(), request.getWeightGoal(), request.getMealsCount());
        
        try {
            // Call the external service client with request parameters
            DietPlan dietPlan = externalDietPlanClient.callOpenAI(request);
            
            log.info("Successfully generated personalized diet plan from external service");
            return dietPlan;
            
        } catch (Exception e) {
            log.error("Failed to generate personalized diet plan from external service", e);
            throw new RuntimeException("Failed to generate personalized diet plan", e);
        }
    }
}
