# Export Service Point

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
- [Development](#development-1)
    * [Spring Batch](#spring-batch)
    * [Multiple DataSources](#multiple-datasources)
- [Jobs](#jobs)
    * [Export ServicePointVersions](#export-servicepointversions)
    * [Export TrafficPointElementVersions](#export-trafficpointelementversions)
    * [Export LoadingPointVersions](#export-loadingpointversions)
    * [Export StopPointVersions](#export-stoppointversions)
    * [Jobs Recovery](#jobs-recovery)
- [Tech Stack](#tech-stack)

<!-- tocstop -->

The main goal of the Atlas Export Service Point is to export Service Point Directory Data as CSV and JSON file and
upload them to the Amazon S3 Bucket.

See [ADR-0017](https://confluence.sbb.ch/display/ATLAS/ADR-0017%3A++Service+Point+Directory+CSV+Export)

## ATLAS

This application is part of ATLAS. General documentation is
available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/README.md)
.

## Project Versioning

This project uses [Semantic Versioning](https://semver.org/).

## Links

### Localhost

* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://export-service-point-int.dev.sbb-cloud.net

### Development

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=export-service-point-dev
* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://export-service-point-int.dev.sbb-cloud.net

### Test

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=export-service-point-test
* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
* Project deploy: https://export-service-point-int.test.sbb-cloud.net

### Integration

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=export-service-point-int
* Openshift Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
* Project deploy: https://export-service-point-int.int.sbb-cloud.net

### Production

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=export-service-point-prod
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

![Architecture](documentation/AtlasServicePointExportArch.svg)

## Development

### Spring Batch

We use [Spring Batch Jobs](https://docs.spring.io/spring-batch/docs/current/reference/html/) to export CSV file from
[ATLAS Amazon S3 Bucket](../base-atlas/documentation/amazon/README.md).

### Multiple DataSources

We use
2 [Data Sources](https://docs.spring.io/spring-boot/docs/2.1.x/reference/html/howto-data-access.html#howto-two-datasources):

1. Batch DB
2. ServicePoint DB

## Jobs

### Export ServicePointVersions

The
export [ServicePointVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/ServicePointVersionExportBatchConfig.java)
Job is responsible to:

* read [ServicePointVersion](src/main/java/ch/sbb/exportservice/entity/ServicePointVersion.java) data from ServicePoint
  dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportType.java](src/main/java/ch/sbb/exportservice/model/SePoDiExportType.java):
    * actual-date
        * world
        * swiss-only
    * full
        * world
        * swiss-only
    * future-timetable
        * world
        * swiss-only

### Export TrafficPointElementVersions

The
export [TrafficPointElementVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/TrafficPointElementVersionExportBatchConfig.java)
Job is responsible to:

* read [TrafficPointElementVersions](src/main/java/ch/sbb/exportservice/entity/TrafficPointElementVersion.java) data
  from ServicePoint dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportType.java](src/main/java/ch/sbb/exportservice/model/SePoDiExportType.java):
    * actual-date
        * world
    * full
        * world
    * future-timetable
        * world

### Export LoadingPointVersions

The
export [LoadingPointVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/LoadingPointVersionExportBatchConfig.java)
Job is responsible to:

* read [LoadingPointVersions](src/main/java/ch/sbb/exportservice/entity/LoadingPointVersion.java) data
  from ServicePoint dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportType.java](src/main/java/ch/sbb/exportservice/model/SePoDiExportType.java):
    * actual-date
        * world
    * full
        * world
    * future-timetable
        * world

### Export StopPointVersions

The
export [StopPointVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/StopPointVersionExportBatchConfig.java)
Job is responsible to:

* read [StopPointVersions](src/main/java/ch/sbb/exportservice/entity/StopPointVersion.java) data
  from Prm dataSource
* generate zipped CSV and gzipped JSON Files based
  on [PrmExportType.java](src/main/java/ch/sbb/exportservice/model/PrmExportType.java):
    * actual-date
    * full
    * future-timetable

### Jobs Recovery

* a retry system is configured on the step level when certain exception are thrown (
  see [StepUtils.java](src/main/java/ch/sbb/exportservice/utils/StepUtils.java))
* [RecoveryJobsRunner.java](src/main/java/ch/sbb/exportservice/recovery/RecoveryJobsRunner.java) checks at startup if there are
  any unfinished jobs or if all jobs have been run. In case
  there are incomplete jobs or not all jobs have been run all jobs are run again.
* If a job has been completed unsuccessfully an email notification is sent to TechSupport-ATLAS@sbb.ch

## Tech Stack

See [Tech Stack Documentation](../documentation/tech-stack-service.md)
