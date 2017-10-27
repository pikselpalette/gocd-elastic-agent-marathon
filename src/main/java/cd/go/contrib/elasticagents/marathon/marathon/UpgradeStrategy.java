package cd.go.contrib.elasticagents.marathon.marathon;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpgradeStrategy {
    private Double maximumOverCapacity;
    private Double minimumHealthCapacity;

    public Double getMaximumOverCapacity() {
        return maximumOverCapacity;
    }

    private boolean validateRange(String field, Double val) throws RuntimeException {
        if (val > 1.0 || val < 0.0) {
            throw new RuntimeException(field + " must be between zero and one, inclusive");
        }
        return true;
    }

    public void setMaximumOverCapacity(Double maximumOverCapacity) {
        if (validateRange("maximumOverCapacity", maximumOverCapacity)) {
            this.maximumOverCapacity = maximumOverCapacity;
        }
    }

    public Double getMinimumHealthCapacity() {
        return minimumHealthCapacity;
    }

    public void setMinimumHealthCapacity(Double minimumHealthCapacity) {
        if (validateRange("minimumHealthCapacity", minimumHealthCapacity)) {
            this.minimumHealthCapacity = minimumHealthCapacity;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof UpgradeStrategy)) return false;

        UpgradeStrategy that = (UpgradeStrategy) o;

        return new EqualsBuilder()
                .append(getMaximumOverCapacity(), that.getMaximumOverCapacity())
                .append(getMinimumHealthCapacity(), that.getMinimumHealthCapacity())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getMaximumOverCapacity())
                .append(getMinimumHealthCapacity())
                .toHashCode();
    }
}
