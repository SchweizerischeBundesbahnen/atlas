# Bulk Import Service

<!-- toc -->

- [ATLAS](#atlas)
- [Project Versioning](#project-versioning)
- [Links](#links)
  * [Localhost](#localhost)
  * [Development](#development)
  * [Test](#test)
  * [Integration](#integration)
  * [Production](#production)
  * [Project Infrastructure](#project-infrastructure)
- [Big Picture Architecture](#big-picture-architecture)
- [Use Case](#use-case)
- [Development](#development-1)
  * [Spring Batch](#spring-batch)
- [Jobs](#jobs)
  * [Bulk Import](#bulk-import)
  * [Tech Stack](#tech-stack)
  * [Reset Batch](#reset-batch)

<!-- tocstop -->

The aim of “bulk import” is to modify a large amount of data in a simple and quick way.

The atlas support Team and transport companies will have the need to make major adjustments to data 
(create/update/terminate).
This is a hybrid between manual data maintenance and data maintenance via interface.

See [ADR-0023](https://confluence.sbb.ch/x/_wgyog)

## ATLAS

This application is part of ATLAS. General documentation is available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/README.md).

## Project Versioning

This project uses [Semantic Versioning](https://semver.org/).

## Links

### Localhost

* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://import-service-point-int.dev.sbb-cloud.net

### Development

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=import-service-point-dev
* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://import-service-point-int.dev.sbb-cloud.net

### Test

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=import-service-point-test
* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
* Project deploy: https://import-service-point-int.test.sbb-cloud.net

### Integration

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=import-service-point-int
* Openshift Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
* Project deploy: https://import-service-point-int.int.sbb-cloud.net

### Production

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=import-service-point-prod
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-prod

### Project Infrastructure

* Tekton : https://tekton-control-panel-atlas-tekton.sbb-cloud.net/projects/KI_ATLAS/repositories/atlas
* Sonarqube: https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas&branch=master
* JFrog / Artifactory
    * Maven repository: https://bin.sbb.ch/ui/repos/tree/General/atlas.mvn
    * Docker registry: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker
* Infrastructure
  documentation: https://confluence.sbb.ch/display/ATLAS/%5BATLAS%5D+7.1.+Infrastruktur+Ebene+1

## Big Picture Architecture

![Architecture](documentation/AtlasImportArch.svg)

## Use Case

![UseCase](documentation/UseCase.svg)

## Development

### Spring Batch

We use [Spring Batch Jobs](https://docs.spring.io/spring-batch/docs/current/reference/html/) to import CSV file from
[ATLAS Amazon S3 Bucket](../base-atlas/documentation/amazon/README.md).

Since CSV file sizes can be very large, we
use [Async Chunk Steps](https://docs.spring.io/spring-batch/docs/current/reference/html/scalability.html#scalability) within the
Job to scale the import process.

![Async Chunk Steps](documentation/BatchAsyncProcessing.svg)

## Jobs

### Bulk Import

TODO

### Tech Stack

See [Tech Stack Documentation](../documentation/tech-stack-service.md)

### Reset Batch

See [Batch Reset](../documentation/batch_util.md)
