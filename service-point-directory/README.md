# Service Point Directory

<!-- toc -->

- [Service Point Directory in ATLAS](#service-point-directory-in-atlas)
- [Links](#links)
    * [Localhost](#localhost)
    * [Development](#development)
    * [Test](#test)
    * [Integration](#integration)
    * [Production](#production)
- [Legacy Documentation - DB Schema - Didok](#legacy-documentation---db-schema---didok)

<!-- tocstop -->

## Service Point Directory in ATLAS

The main goal is to serve the following business objects:

- Service Points (Dienststellen)
- Traffic Point Elements (Verkehrspunktelemente)
- Loading Points (Ladestellen)

Each of these business objects has its own versioning.

`TrafficPointElements` and `LoadingPoints` always belong to a `ServicePoint`, referencing it
via `servicePointNumber`.

These objects are stored and represented in our [Database Schema](src/docs/db-schema.md)
Additional business documentation may be found on
Confluence: https://confluence.sbb.ch/display/ATLAS/%5BDiDok%5D+Dienststellenverwaltung

A glossary of used abbreviations may be found
here: https://confluence.sbb.ch/display/ATLAS/%5BATLAS%5D+Glossar

## Service Point geographic data in ATLAS

The main goal is to serve geographic information about service points and visualize on a map,
without
using any 3rd-party services or SDKs.

### How?

All coordinates and attributes of service points, are mapped
as [mapbox vector tiles](https://docs.mapbox.com/data/tilesets/guides/vector-tiles-standards/).

Then the resulting vector tile object is serialized
to [Google Protobufs (PBF)](https://github.com/protocolbuffers/protobuf),
and send in response back to a web client.

Protobuf is open source and it performs better than
json+gzip: https://auth0.com/blog/beating-json-performance-with-protobuf/

There are several open source web client libraries, to read and display vector tiles as protobuf on
a map.
We will use the [MapLibre](https://maplibre.org/).

The JAVA vector tiles encoder service, was found
on [GitHub](https://github.com/ElectronicChartCentre/java-vector-tile) and simplified to handle
Point-Geometries-Only.

#### Important DEV-notice! Provide all input coordinates for vector tiles in Wgs84WebMercator (SRID:3857) spatial reference.

## Links

### Localhost

* Swagger UI: http://localhost:8088/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Api Docs as JSON: http://localhost:8088/v3/api-docs/
* Api Docs as YAML: http://localhost:8088/v3/api-docs.yaml
* Rest-api generated doc: http://localhost:8088/static/rest-api.html

### Development

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=service-point-directory-dev
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://service-point-directory.dev.sbb-cloud.net
* Swagger
  UI: https://service-point-directory.dev.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Test

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=service-point-directory-test
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
* Project deploy: https://service-point-directory.test.sbb-cloud.net
* Swagger
  UI: https://service-point-directory.test.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Integration

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=service-point-directory-int
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
* Project deploy: https://service-point-directory.int.sbb-cloud.net
* Swagger
  UI: https://service-point-directory.int.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Production

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=service-point-directory-prod
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-prod
* Project deploy: https://service-point-directory.prod.sbb-cloud.net
* Swagger
  UI:  https://service-point-directory.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

## Legacy Documentation - DB Schema - Didok

- Model: https://confluence.sbb.ch/display/ADIDOK/Datenbank
- Migration
  plan: https://sbb.sharepoint.com/:x:/s/didok-atlas/ERrMJki5bFtMqGjShTeKSOQBkUqI2hq4cPixMOXZHqUucg?e=etg8dr
- ServicePoint Category
  Tree: https://confluence.sbb.ch/display/ADIDOK/Big+Picture#BigPicture-Kategorienbaum
