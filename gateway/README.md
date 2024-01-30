# Atlas Gateway

<!-- toc -->

- [ATLAS](#atlas)
- [Versioning](#versioning)
- [Gateway](#gateway)
- [Links](#links)
    * [Development](#development)
    * [Test](#test)
    * [Integration](#integration)
    * [Production](#production)

<!-- tocstop -->

## ATLAS

This application is part of ATLAS. General documentation is
available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/README.md).

## Versioning

This project uses [Semantic Versioning](https://semver.org/).

## Gateway

This project functions as a gateway between our CloudWAF and the backend services.
Configure the services in the configuration like this:

```yaml
gateway:
  routes:
    timetable-field-number: http://localhost:8080
    line-directory: http://localhost:8082
```

## Links

### Development

* Swagger UI: https://gateway.dev.sbb-cloud.net/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* API as Json: https://gateway.dev.sbb-cloud.net/v3/api-docs

### Test

* Swagger UI: https://gateway.test.sbb-cloud.net/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* API as Json: https://gateway.test.sbb-cloud.net/v3/api-docs

### Integration

* Swagger UI: https://gateway.int.sbb-cloud.net/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* API as Json: https://gateway.int.sbb-cloud.net/v3/api-docs

### Production

* Swagger UI: https://gateway.prod.sbb-cloud.net/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* API as Json: https://gateway.prod.sbb-cloud.net/v3/api-docs
