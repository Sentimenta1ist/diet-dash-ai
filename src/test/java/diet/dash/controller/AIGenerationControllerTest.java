package diet.dash.controller;

import diet.dash.model.DietPlan;
import diet.dash.model.DietPlanRequest;
import diet.dash.service.DietPlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIGenerationControllerTest {

    @Mock
    private DietPlanService dietPlanService;

    @InjectMocks
    private AIGenerationController controller;

    private DietPlan mockDietPlan;
    private DietPlanRequest mockRequest;

    @BeforeEach
    void setUp() {
        // Create mock DietPlan
        mockDietPlan = new DietPlan();
        mockDietPlan.setDailyCalories(2150);
        mockDietPlan.setNotes("Test diet plan");
        mockDietPlan.setEstimatedCostEUR(18.50);

        // Create mock request with custom target
        mockRequest = new DietPlanRequest();
        mockRequest.setGender("Male");
        mockRequest.setAge(30);
        mockRequest.setHeight_cm(180);
        mockRequest.setWeight_kg(80);
        mockRequest.setWeightGoal("custom");
        mockRequest.setMealsCount(4);
        mockRequest.setComments("Test comments");
        
        DietPlanRequest.Target target = new DietPlanRequest.Target();
        target.setCalories(2000);
        target.setProtein_g(150);
        target.setCarbs_g(200);
        target.setFat_g(70);
        mockRequest.setTarget(target);
    }

    @Test
    void generate_WithValidRequest_ReturnsDietPlan() {
        // Arrange
        when(dietPlanService.generateDietPlan(mockRequest)).thenReturn(mockDietPlan);

        // Act
        ResponseEntity<DietPlan> response = controller.generate(mockRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockDietPlan, response.getBody());
        verify(dietPlanService, times(1)).generateDietPlan(mockRequest);
    }

    @Test
    void generate_WithRequestWithoutTarget_ReturnsDietPlan() {
        // Arrange
        DietPlanRequest requestWithoutTarget = new DietPlanRequest();
        requestWithoutTarget.setGender("Female");
        requestWithoutTarget.setAge(25);
        requestWithoutTarget.setHeight_cm(165);
        requestWithoutTarget.setWeight_kg(60);
        requestWithoutTarget.setWeightGoal("loss");
        requestWithoutTarget.setMealsCount(3);
        requestWithoutTarget.setComments("Weight loss plan");
        // No target set

        when(dietPlanService.generateDietPlan(requestWithoutTarget)).thenReturn(mockDietPlan);

        // Act
        ResponseEntity<DietPlan> response = controller.generate(requestWithoutTarget);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockDietPlan, response.getBody());
        verify(dietPlanService, times(1)).generateDietPlan(requestWithoutTarget);
    }

    @Test
    void generate_WithServiceException_ThrowsException() {
        // Arrange
        when(dietPlanService.generateDietPlan(mockRequest))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            controller.generate(mockRequest);
        });
        verify(dietPlanService, times(1)).generateDietPlan(mockRequest);
    }
}
