package diet.dash.service;

import diet.dash.model.DietPlan;
import diet.dash.model.DietPlanRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DietPlanServiceImplTest {

    @Mock
    private ExternalDietPlanClient externalDietPlanClient;

    @InjectMocks
    private DietPlanServiceImpl dietPlanService;

    private DietPlan mockDietPlan;
    private DietPlanRequest mockRequest;

    @BeforeEach
    void setUp() {
        // Create mock DietPlan
        mockDietPlan = new DietPlan();
        mockDietPlan.setDailyCalories(2150);
        mockDietPlan.setNotes("Test diet plan");
        mockDietPlan.setEstimatedCostEUR(18.50);

        // Create mock request
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
    void generateDietPlan_WithRequest_ReturnsDietPlan() {
        // Arrange
        when(externalDietPlanClient.callOpenAI(mockRequest)).thenReturn(mockDietPlan);

        // Act
        DietPlan result = dietPlanService.generateDietPlan(mockRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockDietPlan, result);
        verify(externalDietPlanClient, times(1)).callOpenAI(mockRequest);
    }

    @Test
    void generateDietPlan_WithRequestWithoutTarget_ReturnsDietPlan() {
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

        when(externalDietPlanClient.callOpenAI(requestWithoutTarget)).thenReturn(mockDietPlan);

        // Act
        DietPlan result = dietPlanService.generateDietPlan(requestWithoutTarget);

        // Assert
        assertNotNull(result);
        assertEquals(mockDietPlan, result);
        verify(externalDietPlanClient, times(1)).callOpenAI(requestWithoutTarget);
    }

    @Test
    void generateDietPlan_WithRequest_WhenExternalServiceFails_ThrowsException() {
        // Arrange
        when(externalDietPlanClient.callOpenAI(mockRequest))
                .thenThrow(new RuntimeException("External service error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dietPlanService.generateDietPlan(mockRequest);
        });
        
        assertEquals("Failed to generate personalized diet plan", exception.getMessage());
        verify(externalDietPlanClient, times(1)).callOpenAI(mockRequest);
    }
}
