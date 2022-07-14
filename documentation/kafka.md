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
          "clientSecret": "${KAFKA_AUTOMATION_CLIENT_SECRET}",
          "providerId": "sc",
          "kafkaAutomationUrl": "https://automation-ng.kafka.sbb.ch"
        }
]
```

This will make sure all topics under `kafka/topics/dev/*.json` are deployed via kafka automation.

### Create a new client for kafka automation API

- Sign up at https://developer.sbb.ch/apis/kafka_automation_api/information.
- Choose Client Credentials

### Authorize the new client to access the app

Open up a ticket at https://confluence.sbb.ch/display/KAFKA/KAFKA+Home. \
Include the client id and the app name from the appmanagement (https://cloud-ssp.sbb-cloud.net/kafka/appmanagement).

## Creating the kafka user

```json
{
  "name": "atlas.kafka.user",
  "authentication": "scram-sha-512",
  "acls": [
    {
      "resource": {
        "type": "topic",
        "name": "atlas.",
        "patternType": "prefix"
      },
      "operation": "Read"
    },
    {
      "resource": {
        "type": "topic",
        "name": "atlas.",
        "patternType": "prefix"
      },
      "operation": "Write"
    },
    {
      "resource": {
        "type": "group",
        "name": "atlas.",
        "patternType": "prefix"
      },
      "operation": "Read"
    }
  ],
  "connectUser": false
}
```

This user has to be configured with the `ScramLoginModule` in our application setup.

```yaml
    ...
      sasl:
        mechanism: SCRAM-SHA-512
        jaas:
          config: org.apache.kafka.common.security.scram.ScramLoginModule required username="atlas.kafka.user" password="${KAFKA_SCRAM_PASSWORD}";
```

The password will be stored as a Openshift-Secret and injected into the pod as environment variable. It can be optained via https://automation-ng.kafka.sbb.ch/swagger-ui/index.html#/user-controller/getPassword

## Setting up the truststore

Get a truststore @ https://automation-ng.kafka.sbb.ch/swagger-ui/index.html#/environment-controller/getTrustStore by providing a password.
We add it to the classpath in the `resources` folder as `kafka/truststore-test.p12`.

We have to add truststores for the three kafka environments: test, inte and prod.
They will be included into our application by setting up the spring application like this:

```java
  public static void main(String[] args) throws IOException {
    setupTruststore();
    SpringApplication.run(MailApplication.class, args);
    }

private static void setupTruststore() throws IOException {
    Path truststore = Files.createTempFile("truststore", ".p12");
    Files.copy(Objects.requireNonNull(MailApplication.class.getClassLoader()
    .getResourceAsStream(
    "kafka/" + getTruststoreFileName()
    + ".p12")), truststore,
    StandardCopyOption.REPLACE_EXISTING);
    System.setProperty("KAFKA_TRUSTSTORE_LOCATION", truststore.toUri().toString());
    }

private static String getTruststoreFileName() {
    String truststoreFileName = "truststore-test";

    String profilesActive = System.getenv("SPRING_PROFILES_ACTIVE");
    if ("int".equals(profilesActive)) {
    truststoreFileName = "truststore-inte";
    }
    if ("prod".equals(profilesActive)) {
    truststoreFileName = "truststore-prod";
    }
    return truststoreFileName;
    }
```

and configuring `application-dev.yml`

```yaml
  kafka:
    ssl:
      trust-store-location: ${KAFKA_TRUSTSTORE_LOCATION}
      trust-store-password: ${KAFKA_TRUSTSTORE_PASSWORD}
      trust-store-type: PKCS12
```