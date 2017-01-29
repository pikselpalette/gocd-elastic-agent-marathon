package cd.go.contrib.elasticagents.marathon.executors;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayMetadata extends Metadata {
    ArrayMetadata(String key, boolean required) {
        super(key, required, false);
    }

    @Override
    protected String doValidate(String input) {
        List<String> errors = new ArrayList<>();
        errors.add(super.doValidate(input));

        try {
            Gson gson = new Gson();
            List<List<String>> constraints = new ArrayList<>();
            for (String constraint : input.split("\n")) {
                constraints.add(gson.fromJson(constraint, List.class));
            }
        } catch (Exception e) {
            errors.add(e.getMessage());
        }

        errors.removeAll(Collections.singleton(null));

        if (errors.isEmpty()) {
            return null;
        }
        return StringUtils.join(errors, ". ");
    }
}
