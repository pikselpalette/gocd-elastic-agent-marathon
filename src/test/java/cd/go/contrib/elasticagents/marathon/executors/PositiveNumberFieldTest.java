package cd.go.contrib.elasticagents.marathon.executors;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PositiveNumberFieldTest {
    private PositiveNumberField field;
    @Before
    public void setup() {
        field = new PositiveNumberField("key", "aNumber", "10", false, false, "10");
    }

    @Test
    public void negativeNumberShouldReturnError() throws Exception {
        Map<String, String> errors = field.validate("-1");
        assertThat(errors.getOrDefault("message", ""), is("aNumber must be a positive integer."));
    }

    @Test
    public void notANumberShouldReturnError() throws Exception {
        Map<String, String> errors = field.validate("a");
        assertThat(errors.getOrDefault("message", ""), is("aNumber must be a positive integer."));
    }

    @Test
    public void aPositiveNumberShouldValidate() throws Exception {
        Map<String, String> errors = field.validate("1");
        assertThat(errors.getOrDefault("message", ""), is(""));
    }
}
