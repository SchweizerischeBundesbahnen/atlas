# Atlas API

<!-- toc -->

- [Feign Clients](#feign-clients)

<!-- tocstop -->

Base Package for all API Interfaces, to share API calls throughout the project.


## Feign Clients

In order to use the Feign Clients in this package you may import the Configuration as follows:

```java
@Import(AtlasApiFeignClientsConfig.class)
```

This enables you to use clients under `ch.sbb.atlas.api.client`.

To pass the current user token to another service when using Feign, you may use the `TokenPassingFeignClientConfig`