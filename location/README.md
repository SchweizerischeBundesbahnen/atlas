# Location Service

<!-- toc -->

- [ATLAS](#atlas)
- [ADR Architecture Decision Record](#adr-architecture-decision-record)
- [Project Versioning](#project-versioning)
- [Links](#links)
  * [Development](#development)
  * [Test](#test)
  * [Integration](#integration)
  * [Production](#production)

<!-- tocstop -->

## ATLAS

This application is part of ATLAS. General documentation is
available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/README.md).

## ADR Architecture Decision Record

Siehe [ADR-0021: SLOID Service](https://confluence.sbb.ch/x/LCRTmw).

## Project Versioning

This project uses [Semantic Versioning](https://semver.org/).

## Links

### Development

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=location-dev
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://location.dev.sbb-cloud.net

### Test

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=location-test
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
* Project deploy: https://location.test.sbb-cloud.net

### Integration

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=location-int
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
* Project deploy: https://location.int.sbb-cloud.net

### Production

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=location-prod
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-prod
* Project deploy: https://location.prod.sbb-cloud.net
