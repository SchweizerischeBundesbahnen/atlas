# PRM (Person with Reduced Mobility) Backend

<!-- toc -->

- [ATLAS](#atlas)
- [ADR Architecture Decision Record](#adr-architecture-decision-record)
- [Architecture](#architecture)
- [DB Compact](#db-compact)
- [Project Versioning](#project-versioning)
- [Links](#links)
    * [Localhost](#localhost)
    * [Development](#development)
    * [Test](#test)
    * [Integration](#integration)
    * [Production](#production)
    * [Project Infrastructure](#project-infrastructure)
    * [Tech Stack](#tech-stack)
- [Full clean import of stop points](#full-clean-import-of-stop-points)

<!-- tocstop -->

## ATLAS

This application is part of ATLAS. General documentation is
available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/README.md).

## ADR Architecture Decision Record

See [ADR-0020: PRM (BehiG) Migration](https://confluence.sbb.ch/x/3RTcl)

## Architecture

![PRM-Architecture](documentation/PRM-Architecture-kafka.drawio.svg)

## DB Compact

![PRM-DB-Compact](documentation/PRM-DB-Compact.drawio.svg)

## Project Versioning

This project uses [Semantic Versioning](https://semver.org/).

## Links

### Localhost

* Swagger UI: http://localhost:8093/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Api Docs as JSON: http://localhost:8093/v3/api-docs/
* Api Docs as YAML: http://localhost:8093/v3/api-docs.yaml
* Rest-api generated doc: http://localhost:8093/static/rest-api.html

### Development

* DB AWS PostgreSQL: https://backstage.sbb-cloud.net/catalog/default/resource/prm-directory-dev
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://prm-directory.dev.sbb-cloud.net
* Swagger
  UI: https://prm-directory.dev.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Test

* DB AWS PostgreSQL: https://backstage.sbb-cloud.net/catalog/default/resource/prm-directory-test
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
* Project deploy: https://prm-directory.test.sbb-cloud.net
* Swagger
  UI: https://prm-directory.test.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Rest Doc: https://prm-directory.test.sbb-cloud.net/static/rest-api.html

### Integration

* DB AWS PostgreSQL: https://backstage.sbb-cloud.net/catalog/default/resource/prm-directory-int
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
* Project deploy: https://prm-directory.int.sbb-cloud.net
* Swagger
  UI: https://prm-directory.int.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Rest Doc: https://prm-directory.int.sbb-cloud.net/static/rest-api.html

### Production

* DB AWS PostgreSQL: https://backstage.sbb-cloud.net/catalog/default/resource/prm-directory-prod
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-prod
* Project deploy: https://prm-directory.prod.sbb-cloud.net
* Swagger
  UI:  https://prm-directory.prod.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Rest Doc: https://prm-directory.prod.sbb-cloud.net/static/rest-api.html

### Project Infrastructure

* Jenkins: https://ci.sbb.ch/job/KI_ATLAS/job/atlas/
* Sonarqube: https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas&branch=master
* JFrog / Artifactory
    * Maven repository: https://bin.sbb.ch/ui/repos/tree/General/atlas.mvn
    * Docker registry: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker
* Infrastructure
  documentation: https://confluence.sbb.ch/display/ATLAS/%5BATLAS%5D+7.1.+Infrastruktur+Ebene+1

### Tech Stack

See [Tech Stack Documentation](../documentation/tech-stack-service.md)

## Full clean import of stop points

To do a full import of stop points, from csv we need to delete all the existing data from the prm db:

```sql
-- Stop Points
delete
from stop_point_version;
delete
from stop_point_version_means_of_transport;

-- Platforms
delete
from platform_version_info_opportunities;
delete
from platform_version;

-- Reference Points
delete
from reference_point_version;

-- Contact Points
delete
from contact_point_version;

-- Parking Lots
delete
from parking_lot_version;

-- Toilets
delete
from toilet_version;
```

REMARK: If you do full import from scratch, importing first ServicePoint data and then PRM data, then you need to clean
up `shared_service_point` Table
```sql
delete from shared_service_point;
```
And after importing `service_point_version` data and `traffic_point_version` data, you need to trigger sync API call on 
ServicePointApi (`/sync-service-points`) in order to avoid possible issues with PlatformImport.

Further we need to clear the import-service-point db: see * [Reset Batch](../documentation/batch_util.md)

And the Location DB:

For the sloid-types `PLATFORM`, `REFERENCE_POINT`, `TOILET`, `PARKING_LOT`, `CONTACT_POINT`:

```sql
delete
from allocated_sloid
where sloidtype = '{{sloidtype}}';
```
