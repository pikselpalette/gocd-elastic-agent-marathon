package cd.go.contrib.elasticagents.marathon.executors;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ArrayMetadataTest {
    @Test
    public void shouldValidate() throws Exception {
        ArrayMetadata metadata = new ArrayMetadata("hello", true);
        Map<String, String> validation1 = metadata.validate("");
        Map<String, String> validation2 = metadata.validate("[\"foo\"]");
        Map<String, String> validation3 = metadata.validate("[\"foo]");
        assertThat(validation1.getOrDefault("message", ""), is("hello must not be blank."));
        assertThat(validation2.getOrDefault("message", ""), is(""));
        assertThat(validation3.getOrDefault("message", ""), is(not("")));
        assertThat(validation3.getOrDefault("message", ""), is(not("hello must not be blank.")));
    }
}
