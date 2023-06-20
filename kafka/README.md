# Atlas Kafka Lib

Hello there and welcome to Atlas Kafka Library.

Use the [Kafka Portal](https://self.kafka.sbb.ch/) to manage Applications and Topics. \
Or use the kafka-automation API available at https://automation-ng.kafka.sbb.ch/swagger-ui/index.html.

<!-- toc -->

- [How to use](#how-to-use)
- [What it does](#what-it-does)
- [Deploy new topics](#deploy-new-topics)

<!-- tocstop -->

## How to use

1. Add the library to the dependencies
```xml
<dependency>
  <groupId>ch.sbb.atlas</groupId>
  <artifactId>kafka</artifactId>
  <version>${revision}</version>
</dependency>
```

2. Setup the Truststore for your Application

```java
public static void main(String[]args){
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Zurich")));
    KafkaTruststorePreparation.setupTruststore();
    SpringApplication.run(LineDirectoryApplication.class,args);
}
```

3. Make sure your Deployment configures the Openshift Secrets correctly:
```yaml
            - name: KAFKA_SCRAM_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: kafka-secrets
                  key: scram-password
            - name: KAFKA_TRUSTSTORE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: kafka-secrets
                  key: truststore-password
```

4. `@Import(AtlasKafkaConfiguration.class)` to include the config to the application context.
5. Use this library for models which shall be sent over Kafka

## What it does

This will configure the Kafka authorization settings for spring with truststore and passwords and also configure the standard producer and consumer settings.

If you need more specific settings four your application, just override the properties in your `application.yml`

## Deploy new topics

You may add Kafka-Automation settings to `topics` to deploy new topics. This folder is configured to be an input with `estaCloudPipeline.json`