package cd.go.contrib.elasticagents.marathon.executors;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class MetadataTest {
    @Test
    public void metadataWithKeyOnly() throws Exception {
        Metadata metadata = new Metadata("foo");
        assertThat(metadata.isRequired(), is(false));
    }

    @Test
    public void metadataValidationShouldFailOnBlankInput() throws Exception {
        Metadata metadata = new Metadata("hello", true, false);
        Map<String, String> validation1 = metadata.validate("");
        Map<String, String> validation2 = metadata.validate("a");
        assertThat(validation1.getOrDefault("message", ""), is("hello must not be blank."));
        assertThat(validation2.getOrDefault("message", ""), is(""));
    }
}
