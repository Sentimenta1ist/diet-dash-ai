package diet.dash.controller;

import diet.dash.model.DietPlan;
import diet.dash.model.DietPlanRequest;
import diet.dash.service.DietPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AIGenerationController {

    private final DietPlanService dietPlanService;

    @PostMapping("/generate")
    public ResponseEntity<DietPlan> generate(@RequestBody DietPlanRequest request) {
        return ResponseEntity.ok(dietPlanService.generateDietPlan(request));
    }

//    @GetMapping("/generate")
//    public ResponseEntity<DietPlan> generateDefault() {
//        return ResponseEntity.ok(dietPlanService.generateDietPlan());
//    }

}
