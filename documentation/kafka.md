# Kafka

SBB Documentation on Kafka: https://confluence.sbb.ch/display/KAFKA/Getting+Started

Appmanagement is performed via https://cloud-ssp.sbb-cloud.net/kafka/appmanagement.

Swagger-UI for creating topics etc.: https://automation-ng.kafka.sbb.ch/swagger-ui/index.html

## Atlas

Atlas is currently on *Swisscloud* and using SASL/SCRAM for authentication.
Links to the environments can be found on https://confluence.sbb.ch/display/KAFKA/Umgebungen.

## Automation

In our monorepo there is the new folder `kafka`. This currently contains topics, which will be created on deployment on various stages.
To include these `json` configuration files into the build, we set up `estaCloudPipeline.json` to include kafka on each stage e.g.:

```json
"kafkaDeployParameters": [
        {
          "environmentId": "test",
          "type": "topics",
          "fileGlob": "kafka/topics/dev/*.json",
          "clientId": "825e4030",
          "clientSecret": "3dc481acc5078d23dd9be02b7c5de29b",
          "providerId": "sc",
          "kafkaAutomationUrl": "https://automation-ng.kafka.sbb.ch"
        }
```

This will make sure all topics under `kafka/topics/dev/*.json` are deployed via kafka automation.

### Create a new client for kafka automation API

- Sign up at https://developer.sbb.ch/apis/kafka_automation_api/information.
- Choose Client Credentials

### Authorize the new client to access the app

Open up a ticket at https://confluence.sbb.ch/display/KAFKA/KAFKA+Home
Include the client id