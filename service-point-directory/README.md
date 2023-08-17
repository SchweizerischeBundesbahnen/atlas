# Service Point Directory

<!-- toc -->

- [Service Point Directory in ATLAS](#service-point-directory-in-atlas)
- [Service Point geographic data in ATLAS](#service-point-geographic-data-in-atlas)
  * [How?](#how)
    + [Important DEV-notice](#important-dev-notice)
- [Links](#links)
  * [Localhost](#localhost)
  * [Development](#development)
  * [Test](#test)
  * [Integration](#integration)
  * [Production](#production)
- [Legacy Documentation - DB Schema - Didok](#legacy-documentation---db-schema---didok)
- [Full clean import of service points, traffic point elements and loading points](#full-clean-import-of-service-points-traffic-point-elements-and-loading-points)

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

There are several open source web client libraries, to read and display protobuf vector tiles on
a map in Web:

- [Mapbox GL JS](https://github.com/mapbox/mapbox-gl-js) - JavaScript/WebGL vector maps library.
- [MapLibre GL](https://github.com/maplibre/maplibre-gl-js) - Is a community led fork derived from
  Mapbox GL JS prior to their switch to a non-OSS license.
- [OpenLayers](https://openlayers.org/en/latest/examples/mapbox-vector-layer.html) - JavaScript
  vector & raster library.
- [Vector Tiles Google Maps](https://github.com/techjb/Vector-Tiles-Google-Maps) - Render vector
  tile layers on Google Maps.

_(Find an extended list on https://github.com/mapbox/awesome-vector-tiles)._

We are using [MapLibre](https://maplibre.org/), which is the best Open Source alternative of the
Mapbox GL Client.

The JAVA Backend vector tiles encoder service, was found
on [GitHub](https://github.com/ElectronicChartCentre/java-vector-tile) and simplified to handle
Point-Geometries-Only.

#### Important DEV-notice

Always provide all input coordinates for vector tiles in Wgs84WebMercator spatial
reference (https://epsg.io/3857), which is the coordinate system, used for rendering maps in Google
Maps, OpenStreetMap, etc.

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

## Full clean import of service points, traffic point elements and loading points

To do a full import of service points, traffic point elements and loading points from csv we need to delete all the existing data
from the service-point db:

```sql
-- Service Points
delete
from service_point_version;

-- faster delete without fk constraint
alter table service_point_version drop constraint fk_service_point_geolocation_id;
delete
from service_point_version_geolocation;
alter table service_point_version
    add constraint fk_service_point_geolocation_id
        FOREIGN KEY (service_point_geolocation_id)
            REFERENCES service_point_version_geolocation (id);

delete
from service_point_version_categories;
delete
from service_point_version_means_of_transport;


-- Traffic Point Elements
delete
from traffic_point_element_version;

-- faster delete without fk constraint
alter table traffic_point_element_version drop constraint fk_traffic_point_element_version_geolocation_id;
delete
from traffic_point_element_version_geolocation;
alter table traffic_point_element_version
    add constraint fk_traffic_point_element_version_geolocation_id
        FOREIGN KEY (traffic_point_geolocation_id)
            REFERENCES traffic_point_element_version_geolocation (id);

-- Loading Points
delete
from loading_point_version;
```

Further we need to clear the import-service-point db:

```sql
DELETE
FROM BATCH_STEP_EXECUTION_CONTEXT;
DELETE
FROM BATCH_STEP_EXECUTION;
DELETE
FROM BATCH_JOB_EXECUTION_PARAMS;
DELETE
FROM batch_job_execution_context;
DELETE
FROM BATCH_JOB_EXECUTION;
DELETE
FROM BATCH_JOB_INSTANCE;
```
