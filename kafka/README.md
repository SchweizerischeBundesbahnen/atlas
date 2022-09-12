# Atlas Kafka Lib

Hello there and welcome to Atlas Kafka Library.

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

4. `@Import(AtlasKafkaConfiguration.class)` to include the config to the application context.
3. Use this library for models which shall be sent over Kafka

## What it does

This will configure the Kafka authorization settings for spring with truststore and passwords and also configure the standard producer and consumer settings.

If you need more specific settings four your application, just override the properties in your `application.yml`

## Deploy new topics

You may add Kafka-Automation settings to `topics` to deploy new topics. This folder is configured to be an input with `estaCloudPipeline.json`