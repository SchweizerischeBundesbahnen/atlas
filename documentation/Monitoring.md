# Monitoring with Instana

General informations about instana are available here:\
https://confluence.sbb.ch/display/MON/Instana

## Implementation

Add the following dependency to add [Spring Sleuth](https://spring.io/projects/spring-cloud-sleuth).
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```

Edit your application.properties or yml and add `traceid=%X{X-B3-TraceId:-}` and `spanid=%X{X-B3-SpanId:-}` to it. E.g. it should look like this:
```yaml
logging:
  pattern:
    console: timestamp=%d thread=%t loglevel=%-5p class=%c appname=atlas traceid=%X{X-B3-TraceId:-} spanid=%X{X-B3-SpanId:-} message="%m"%n
```
# Actuator

The [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) is available on `/actuator` and provides information about the running application.

To use Spring Actuator include the dependency to your project:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

We configured it to show details on the health endpoint (e.g. for DB Health indication) and exposed all actuator endpoints.
This can be configured in the `application.yaml` or properties respectively
```yaml
management:
  endpoint:
    health:
      show-details: always
  endpoints:
    web:
      exposure:
        include: "*"
```