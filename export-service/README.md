# Export Service

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
  * [Exports for Service Point Directory](#exports-for-service-point-directory)
    + [Export ServicePointVersions](#export-servicepointversions)
    + [Export TrafficPointElementVersions](#export-trafficpointelementversions)
    + [Export LoadingPointVersions](#export-loadingpointversions)
  * [Export PRM Directory](#export-prm-directory)
    + [Export StopPointVersions](#export-stoppointversions)
    + [Export PlatformVersion](#export-platformversion)
    + [Export ReferencePointVersion](#export-referencepointversion)
    + [Export ContactPointVersion](#export-contactpointversion)
    + [Export Toilet](#export-toilet)
    + [Export Relation](#export-relation)
  * [Export Business Organisation Directory](#export-business-organisation-directory)
    + [Export Business Organisations](#export-business-organisations)
    + [Export Transport Company](#export-transport-company)
  * [Export Line Directory](#export-line-directory)
    + [Export Lines](#export-lines)
    + [Export Sublines](#export-sublines)
    + [Export Timetable Field Number](#export-timetable-field-number)
  * [Jobs Recovery](#jobs-recovery)
- [Tech Stack](#tech-stack)

<!-- tocstop -->

The main goal of the Atlas Export Service is to export Data as CSV and JSON file and
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
* Project deploy: https://export-service-int.dev.sbb-cloud.net

### Development

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=export-service-dev
* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://export-service-int.dev.sbb-cloud.net

### Test

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=export-service-test
* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
* Project deploy: https://export-service-int.test.sbb-cloud.net

### Integration

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=export-service-int
* Openshift Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
* Project deploy: https://export-service-int.int.sbb-cloud.net

### Production

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=export-service-prod
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

![Architecture](documentation/AtlasServiceExportArch.svg)

## Development

### Spring Batch

We use [Spring Batch Jobs](https://docs.spring.io/spring-batch/docs/current/reference/html/) to export CSV file from
[ATLAS Amazon S3 Bucket](../base-atlas/documentation/amazon/README.md).

### Multiple DataSources

We use
2 [Data Sources](https://docs.spring.io/spring-boot/docs/2.1.x/reference/html/howto-data-access.html#howto-two-datasources):

1. Batch DB
2. ServicePoint DB

### How to export to Amazon S3

#### Add base-atlas dependency

In order to be able to export files to [SBB Amazon S3](../base-atlas/documentation/amazon/README.md) you have to add this
library to your module:

~~~kotlin
implementation("software.amazon.awssdk:s3:${property("awsS3Version")}")
implementation(project(":base-atlas"))
~~~

### Configure Amazon Client

#### Add Amazon Client Properties

~~~
amazon:
  accessKey: ${AMAZON_S3_ACCESS_KEY}
  secretKey: ${AMAZON_S3_SECRET_KEY}
  region: "eu-central-1"
  bucketName: "atlas-data-export-dev-dev"
  objectExpirationDays: 30
~~~

#### Configure Amazon Client Secrets Chart

You have to define in the **Chart template** the following properties:

~~~
- name: AMAZON_S3_ACCESS_KEY
  valueFrom:
    secretKeyRef:
        name: amazon-client-{{ .Values.YOUR-APPLICATION.name }}
        key: amazon-access-key
- name: AMAZON_S3_SECRET_KEY
  valueFrom:
    secretKeyRef:
        name: amazon-client-{{ .Values.YOUR-APPLICATION.name }}
        key: amazon-secret-key
~~~

#### Add the Secrets to Open Shift

Remember to store the secrets to our Open Shift for every environment.

#### Configure Client

See [AmazonConfig.java](/src/main/java/ch/sbb/exportservice/config/AmazonConfig.java)

#### Configure beans

Configure `FileService` bean:

~~~java
@Bean
public FileService fileService(){
    return new FileServiceImpl();
    }  
~~~

#### Upload the file

For an file upload example see [UploadJsonFileTaskletV2.java](/src/main/java/ch/sbb/exportservice/tasklet/upload/UploadJsonFileTaskletV2.java).

## Jobs

### Exports for Service Point Directory

#### Export ServicePointVersions

The
export [ServicePointVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/ServicePointVersionExportBatchConfig.java)
Job is responsible to:

* read [ServicePointVersion](src/main/java/ch/sbb/exportservice/entity/sepodi/ServicePointVersion.java) data from ServicePoint
  dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
    * actual-date
        * world
        * swiss
    * full
        * world
        * swiss
    * future-timetable
        * world
        * swiss

#### Export TrafficPointElementVersions

The
export [TrafficPointElementVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/TrafficPointElementVersionExportBatchConfig.java)
Job is responsible to:

* read [TrafficPointElementVersions](src/main/java/ch/sbb/exportservice/entity/sepodi/TrafficPointElementVersion.java) data
  from ServicePoint dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
    * actual-date
        * world
    * full
        * world
    * future-timetable
        * world

#### Export LoadingPointVersions

The
export [LoadingPointVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/LoadingPointVersionExportBatchConfig.java)
Job is responsible to:

* read [LoadingPointVersions](src/main/java/ch/sbb/exportservice/entity/sepodi/LoadingPointVersion.java) data
  from ServicePoint dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
    * actual-date
        * world
    * full
        * world
    * future-timetable
        * world

### Export PRM Directory

#### Export StopPointVersions

The
export [StopPointVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/StopPointVersionExportBatchConfig.java)
Job is responsible to:

* read [StopPointVersions](src/main/java/ch/sbb/exportservice/entity/prm/StopPointVersion.java) data
  from Prm dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
    * actual-date
    * full
    * future-timetable

#### Export PlatformVersion

The
export [PlatformVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/PlatformVersionExportBatchConfig.java)
Job is responsible to:

* read [PlatformVersion](src/main/java/ch/sbb/exportservice/entity/prm/PlatformVersion.java) data
  from Prm dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
    * actual-date
    * full
    * future-timetable

#### Export ReferencePointVersion

The
export [ReferencePointVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/ReferencePointVersionExportBatchConfig.java)
Job is responsible to:

* read [ReferencePointVersion](src/main/java/ch/sbb/exportservice/entity/prm/ReferencePointVersion.java) data
  from Prm dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
    * actual-date
    * full
    * future-timetable

#### Export ContactPointVersion

The
export [ContactPointVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/ContactPointVersionExportBatchConfig.java)
Job is responsible to:

* read [ContactPointVersion](src/main/java/ch/sbb/exportservice/entity/prm/ContactPointVersion.java) data
  from Prm dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
    * actual-date
    * full
    * future-timetable

#### Export Toilet

The
export [ToiletVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/ToiletVersionExportBatchConfig.java)
Job is responsible to:

* read [ToiletVersion](src/main/java/ch/sbb/exportservice/entity/prm/ToiletVersion.java) data
  from Prm dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
    * actual-date
    * full
    * future-timetable

#### Export Relation

The
export [RelationVersionExportBatchConfig](src/main/java/ch/sbb/exportservice/config/RelationVersionExportBatchConfig.java)
Job is responsible to:

* read [RelationVersion](src/main/java/ch/sbb/exportservice/entity/RelationVersion.java) data
  from Prm dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
  * actual-date
  * full
  * future-timetable

### Export Business Organisation Directory

#### Export Business Organisations

The
export [BusinessOrganisationExportBatchConfig](src/main/java/ch/sbb/exportservice/config/BusinessOrganisationExportBatchConfig.java)
Job is responsible to:

* read [BusinessOrganisation](src/main/java/ch/sbb/exportservice/entity/bodi/BusinessOrganisation.java) data
  from BoDi dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
  * actual-date
  * full
  * future-timetable

#### Export Transport Company

The
export [TransportCompanyExportBatchConfig](src/main/java/ch/sbb/exportservice/config/TransportCompanyExportBatchConfig.java)
Job is responsible to:

* read [TransportCompany](src/main/java/ch/sbb/exportservice/entity/bodi/TransportCompany.java) data
  from BoDi dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
  * actual-date
  * full
  * future-timetable

### Export Line Directory

#### Export Lines

The
export [LineExportBatchConfig](src/main/java/ch/sbb/exportservice/config/LineExportBatchConfig.java)
Job is responsible to:

* read [Line](src/main/java/ch/sbb/exportservice/entity/lidi/Line.java) data
  from LiDi dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
  * actual-date
  * full
  * future-timetable

#### Export Sublines

The
export [SublineExportBatchConfig](src/main/java/ch/sbb/exportservice/config/SublineExportBatchConfig.java)
Job is responsible to:

* read [Subline](src/main/java/ch/sbb/exportservice/entity/lidi/Subline.java) data
  from LiDi dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
  * actual-date
  * full
  * future-timetable

#### Export Timetable Field Number

The
export [TimetableFieldNumberExportBatchConfig](src/main/java/ch/sbb/exportservice/config/TimetableFieldNumberExportBatchConfig.java)
Job is responsible to:

* read [TimetableFieldNumber](src/main/java/ch/sbb/exportservice/entity/lidi/TimetableFieldNumber.java) data
  from LiDi dataSource
* generate zipped CSV and gzipped JSON Files based
  on [ExportTypeV2.java](src/main/java/ch/sbb/exportservice/model/ExportTypeV2.java):
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
