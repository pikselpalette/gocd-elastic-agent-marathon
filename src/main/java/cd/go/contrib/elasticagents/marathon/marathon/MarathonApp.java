package cd.go.contrib.elasticagents.marathon.marathon;

import mesosphere.marathon.client.model.v2.App;

public class MarathonApp extends App {

    private String user;
    private UpgradeStrategy upgradeStrategy;

    public UpgradeStrategy getUpgradeStrategy() {
        return upgradeStrategy;
    }

    public void setUpgradeStrategy(UpgradeStrategy upgradeStrategy) {
        this.upgradeStrategy = upgradeStrategy;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
