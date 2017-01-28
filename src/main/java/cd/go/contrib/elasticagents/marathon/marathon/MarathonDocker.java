package cd.go.contrib.elasticagents.marathon.marathon;

import mesosphere.marathon.client.model.v2.Docker;

public class MarathonDocker extends Docker {
    private boolean forcePullImage;
    private boolean privileged;

    public boolean isForcePullImage() {
        return forcePullImage;
    }

    public void setForcePullImage(boolean forcePullImage) {
        this.forcePullImage = forcePullImage;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public void setPrivileged(boolean privileged) {
        this.privileged = privileged;
    }
}