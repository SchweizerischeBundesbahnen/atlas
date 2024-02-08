# Workflow Service

<!-- toc -->

- [ATLAS](#atlas)
- [Architecture](#architecture)
- [DB Model](#db-model)
- [Project Versioning](#project-versioning)
- [Links](#links)
    * [Localhost](#localhost)
    * [Development](#development)
    * [Test](#test)
    * [Integration](#integration)
    * [Production](#production)
    * [Project Infrastructure](#project-infrastructure)
- [Development](#development-1)
    * [Tech Stack](#tech-stack)

<!-- tocstop -->

The main task of the Atlas Workflow Service is to provide a central API to execute Atlas Workflows.
For more information see [Workflow Lidi & FPFN](https://confluence.sbb.ch/pages/viewpage.action?pageId=1972998390)

## ATLAS

This application is part of ATLAS. General documentation is
available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/README.md).

## Use Cases

![Workflow-Line-Use-Cases](documentation/workflow-line-use-cases.drawio.svg)

## Architecture

![Workflow-Line-Architecture](documentation/workflow-line-architecture.drawio.svg)

## DB Model

![DB-Model](documentation/workflow-service-db.drawio.svg)

## Project Versioning

This project uses [Semantic Versioning](https://semver.org/).

## Links

### Localhost

* Swagger UI: http://localhost:8087/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Api Docs as JSON: http://localhost:8087/v3/api-docs/
* Api Docs as YAML: http://localhost:8087/v3/api-docs.yaml
* Rest-api generated doc: http://localhost:8087/static/rest-api.html

### Development

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=workflow-dev
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://workflow.dev.sbb-cloud.net
* Swagger UI: https://workflow.dev.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Test

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=workflow-test
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
* Project deploy: https://workflow.test.sbb-cloud.net
* Swagger UI: https://workflow.test.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Integration

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=workflow-int
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
* Project deploy: https://workflow.int.sbb-cloud.net
* Swagger UI: https://workflow.int.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Production

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=workflow-prod
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-prod
* Project deploy: https://workflow.prod.sbb-cloud.net
* Swagger UI:  https://workflow.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Project Infrastructure

* Jenkins: https://ci.sbb.ch/job/KI_ATLAS/job/atlas/
* Sonarqube: https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas&branch=master
* JFrog / Artifactory
    * Maven repository: https://bin.sbb.ch/ui/repos/tree/General/atlas.mvn
    * Docker registry: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker
* Infrastructure
  documentation: https://confluence.sbb.ch/display/ATLAS/%5BATLAS%5D+7.1.+Infrastruktur+Ebene+1

## Development

### Tech Stack

See [Tech Stack Documentation](../documentation/tech-stack-service.md)
