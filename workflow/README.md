# Workflow Service

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
- [Development](#development-1)
    * [Tech Stack](#tech-stack)

<!-- tocstop -->

The main task of the Atlas Workflow Service is to provide a central API to execute Atlas Workflows.
For more information see [Workflow Lidi & FPFN](https://confluence.sbb.ch/pages/viewpage.action?pageId=1972998390)

## ATLAS

This application is part of ATLAS. General documentation is
available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas-backend/browse/README.md#big-picture)
.

## Project Versioning

This project uses [Semantic Versioning](https://semver.org/).

## Links

### Localhost

### Development

### Test

### Integration

### Production

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

| Layer     |  Technologie    |  Link     |
|-----------|------------|-----------|
|Frontend   | Angular9 + | [ESTA-Web](https://confluence.sbb.ch/display/CLEW/ESTA-Web) |
|Backend    |Java Spring Boot 2.5 | [ESTA-Backend (Spring-Boot)](https://confluence.sbb.ch/pages/viewpage.action?pageId=1306395091) |
|           |Lombok | https://projectlombok.org/ |
|           |OpenAPI | https://swagger.io/specification/ |
|Database    |PostgreSQL| [Service PostgreSQL](https://confluence.sbb.ch/display/PLA/Service+PostgreSQL)|
|Messaging    |Apache Kafka| [KAFKA Home](https://confluence.sbb.ch/display/KAFKA/KAFKA+Home)|
|Infrastructure|    Openshift AWS 4.0| [ESTA-Cloud](https://confluence.sbb.ch/display/CLEW/ESTA-Cloud)|
|Deployment    |ESTA Cloud Pipeline| [Esta Cloud Pipeline](https://confluence.sbb.ch/display/CLEW/Esta+Cloud+Pipeline)|
|Interface|  API Management oder ähnliches Tool nach Entscheid KISPF-198 <br> REST & Json| [KISPF-198](https://flow.sbb.ch/browse/KISPF-198) - SKI/SKI+ API Strategie|
