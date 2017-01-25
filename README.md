# gocd-elastic-agent-marathon [![Build Status](https://travis-ci.org/pikselpalette/gocd-elastic-agent-marathon.png)](https://travis-ci.org/pikselpalette/gocd-elastic-agent-marathon)

## Building the code base

To build the jar, run `./gradlew clean build jacocoTestReport

## Is this production ready?

It depends.

The plugin, as it is currently implemented is meant to be a very simple plugin to demonstrate how to get started with GoCD [elastic agent](https://plugin-api.go.cd/current/elastic-agents) feature.  It works for us

## Customizing your docker image to run as a GoCD Elastic Agent

There are two ways to customize your docker image to work with this plugin
 
### Use the GoCD agent

* Ensure that you have installed the go-agent for your distribution, using apt/yum or a zip file if you're using a distribution that does not support apt or yum.
* Once the agent is installed, create a simple shell script executed via (`CMD`) that will accept the following variables and execute the agent bootstrapper process. These environment variables are passed by this plugin when performing a `docker run`. Your image is expected to use these variables to create a correct `autoregister.properties`:
  * `GO_EA_SERVER_URL` - the URL of the GoCD server (from the plugin settings page)
  * `GO_EA_AUTO_REGISTER_KEY` - the auto-register key
  * `GO_EA_AUTO_REGISTER_ENVIRONMENT` - the auto-register environment
  * `GO_EA_AUTO_REGISTER_ELASTIC_AGENT_ID` - the elastic agent id
  * `GO_EA_AUTO_REGISTER_ELASTIC_PLUGIN_ID` — the elastic plugin id
  * `GO_EA_GUID` — the guid of the elastic agent. Create a file called `config/guid.txt` with the contents of this environment variable.

### Use the GoCD golang bootstrapper

This method is a [bit insecure (PR welcome)](https://github.com/ketan/gocd-golang-bootstrapper), but uses lesser memory and boots up and starts off a build quickly:
 
```dockerfile
FROM yourimage

# install whatever packages you need, in addition to the JRE, and git
# apt-get install openjdk-8-jre-headless git
# yum install java-1.8.0-openjdk-headless git

# Add a user to run the go agent
RUN adduser go go -h /go -S -D

# download the agent bootstrapper
ADD https://github.com/ketan/gocd-golang-bootstrapper/releases/download/0.9/go-bootstrapper-0.9.linux.amd64 /go/go-agent
RUN chmod 755 /go/go-agent

# download tini
ADD https://github.com/krallin/tini/releases/download/v0.10.0/tini /tini
RUN chmod +x /tini
ENTRYPOINT ["/tini", "--"]

# Run the bootstrapper as the `go` user
USER go
CMD /go/go-agent
``` 

## Usage instructions

* Download and install mesos and marathon (see https://mesosphere.github.io/marathon/docs/)

* Download the latest GoCD installer from https://go.cd/download

    ```shell
    $ unzip go-server-VERSION.zip
    $ mkdir -p go-server-VERSION/plugins/external
    ```

* Build and install the plugin

    ```shell
    $ ./gradlew clean build
    $ cp build/libs/gocd-elastic-agent-marathon-0.1.jar /path/to/go-server-VERSION/plugins/external
    ```

* Start the server and configure the plugin (turn on debug logging to get more logs, they're not that noisy)

  On linux/mac

    ```shell
    $ GO_SERVER_SYSTEM_PROPERTIES='-Dplugin.cd.go.contrib.elastic-agent.marathon.log.level=debug' ./server.sh
    ```

  On windows

    ```
    C:> set GO_SERVER_SYSTEM_PROPERTIES='-Dplugin.cd.go.contrib.elastic-agent.marathon.log.level=debug'
    C:> server.cmd
    ```

To configure the plugin, navigate to the plugin settings page on your GoCD server http://localhost:8153/go/admin/plugins and setup the following settings for the marathon plugin.

```
Go Server Host — https://YOUR_IP_ADDRESS:8154/go — do not use "localhost"
Auto register timeout - between 1-3 minutes
Marathon URL — https://YOUR_IP_ADDRESS:8080 — "localhost" is ok, if it runs on the same host as the go server
Marathon Username and password - necessary if you have configured marathon for basic auth
Marathon Prefix - good for access control, so the user that go uses can only affects applications under this path
Max Agents - restrict runaway agent requests.
```

Now setup the config.xml —

* add `agentAutoRegisterKey="some-secret-key"` to the `<server/>` tag.
* setup a job —

```xml
<server agentAutoRegisterKey="...">
  <elastic>
    <profiles>
        <profile id="tests" pluginId="go.cd.contrib.elastic-agent.marathon">
          <property>
            <key>Image</key>
            <value>goagent:latest</value>
          </property>
          <property>
            <key>Memory</key>
            <value>512MB</value>
          </property>
          <property>
            <key>CPUs</key>
            <value>0.5</value>
          </property>
        </profile>
        <profile id="big" pluginId="go.cd.contrib.elastic-agent.marathon">
          <property>
            <key>Image</key>
            <value>goagent</value>
          </property>
          <property>
            <key>Memory</key>
            <value>4096MB</value>
          </property>
          <property>
            <key>CPUs</key>
            <value>4</value>
          </property>
        </profile>
    </profiles>
  </elastic>
</server>
...
<pipelines group="defaultGroup">
  <pipeline name="Foo">
    <materials>
      <git url="YOUR GIT URL" />
    </materials>
    <stage name="defaultStage">
      <jobs>
        <job name="defaultJob" elasticProfileId="marathon.unit-tests">
          <tasks>
            <exec command="ls" />
          </tasks>
        </job>
      </jobs>
    </stage>
  </pipeline>
</pipelines>
```

All of the config.xml settings are configurable under the elastic agents profile settings in the GoCD admin UI.

## Troubleshooting

Enabling debug level logging can help you troubleshoot an issue with the elastic agent plugin. To enable debug level logs, edit the `/etc/default/go-server` (for Linux) to add:

```bash
export GO_SERVER_SYSTEM_PROPERTIES="$GO_SERVER_SYSTEM_PROPERTIES -Dplugin.cd.go.contrib.elastic-agent.marathon.log.level=debug"
```

If you're running the server via `./server.sh` script —

```
$ GO_SERVER_SYSTEM_PROPERTIES="-Dplugin.cd.go.contrib.elastic-agent.marathon.log.level=debug" ./server.sh
```

## License

```plain
Copyright 2017, Piksel, Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
