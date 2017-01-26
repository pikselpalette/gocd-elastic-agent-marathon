package cd.go.contrib.elasticagents.marathon.marathon;

import mesosphere.marathon.client.model.v2.App;

public class MarathonApp extends App {

    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
