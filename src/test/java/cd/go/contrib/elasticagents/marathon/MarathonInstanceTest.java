package cd.go.contrib.elasticagents.marathon;

import cd.go.contrib.elasticagents.marathon.requests.CreateAgentRequest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MarathonInstanceTest extends BaseTest {

    @Test
    public void shouldSomething() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put("Image", "myImage");
        properties.put("Memory", "2048MB");
        properties.put("CPUs", "1");
        properties.put("Command", "/bin/true");
        properties.put("User", "me");
        properties.put("Constraints", "[\"stage\", \"CLUSTER\", \"dev\"]");

        CreateAgentRequest createAgentRequest = new CreateAgentRequest(
                "aaaa",
                properties,
                "production"
        );

        MarathonInstance instance1 = MarathonInstance.create(createAgentRequest, createSettings(), createClient());
        MarathonInstance instance2 = MarathonInstance.create(createAgentRequest, createSettings(), createClient());
        assertTrue(instance1.equals(instance1));
        assertFalse(instance1.equals(instance2));

        // Silly, but names should never match
        assertFalse(instance1.getName().equals(instance2.getName()));
    }
}
