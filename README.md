# Atlas Gateway

[![Build Status](https://ci.sbb.ch/job/KI_ATLAS/job/atlas-gateway/job/master/badge/icon)](https://ci.sbb.ch/job/KI_ATLAS/job/atlas-gateway/job/master/)
[![Quality Gate Status](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb%3Aatlas-gateway&metric=alert_status)](https://codequality.sbb.ch/dashboard?id=ch.sbb%3Aatlas-gateway)

<!-- toc -->

- [ATLAS](#atlas)
- [Versioning](#versioning)
- [Gateway](#gateway)

<!-- tocstop -->

## ATLAS
This application is part of ATLAS. General documentation is available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas-backend/browse/README.md#big-picture).

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
* Swagger UI: https://dev.atlas-gateway.sbb-cloud.net/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* API as Json: https://dev.atlas-gateway.sbb-cloud.net/v3/api-docs

### Test
* Swagger UI: https://test.atlas-gateway.sbb-cloud.net/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* API as Json: https://test.atlas-gateway.sbb-cloud.net/v3/api-docs

### Integration
* Swagger UI: https://int.atlas-gateway-int.sbb-cloud.net/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* API as Json: https://int.atlas-gateway-int.sbb-cloud.net/v3/api-docs