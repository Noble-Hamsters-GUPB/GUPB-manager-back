package com.gupb.manager.requirements;
import com.gupb.manager.controllers.RequirementController;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;

class RequirementControllerTest {
    private static final RequirementController reqC = new RequirementController();

    @Test
    public void getRequirementsTest() {
        Map<String, String> requirements = null;
        try {
            requirements = reqC.getRequirements();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(requirements.get("bresenham"),"0.2.1");
    }

}

