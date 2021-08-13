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
```properties
logging.pattern.console: timestamp=%d thread=%t loglevel=%-5p class=%c appname=atlas traceid=%X{X-B3-TraceId:-} spanid=%X{X-B3-SpanId:-} message="%m"%n
```
