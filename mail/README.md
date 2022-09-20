# Mail Service

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
  * [REST API](#rest-api)
  * [Thymeleaf Template](#thymeleaf-template)
  * [Tech Stack](#tech-stack)

<!-- tocstop -->

## ATLAS
This application is part of ATLAS. General documentation is available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas-backend/browse/README.md#big-picture).

## Project Versioning
This project uses [Semantic Versioning](https://semver.org/).

## Links

### Localhost


### Development


### Test

In this project [Greenmail](https://greenmail-mail-test.github.io/greenmail/) is used to "mock" the SMPT server
in the Integration tests.

### Integration


### Production


### Project Infrastructure
* Jenkins: https://ci.sbb.ch/job/KI_ATLAS/job/atlas/
* Sonarqube: https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas&branch=master
* JFrog / Artifactory
    * Maven repository: https://bin.sbb.ch/ui/repos/tree/General/atlas.mvn
    * Docker registry: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker
* Infrastructure documentation: https://confluence.sbb.ch/display/ATLAS/%5BATLAS%5D+7.1.+Infrastruktur+Ebene+1

## Development

### REST API

The MailService provides a REST API to communicate with him. The [postman collection](postman/MailService.postman_collection.json)
defines some REST Call example.

:warning: The REST API will be deleted when ATLAS will integrate Kafka for the Service to Service cominication. :warning:

### Thymeleaf Template

To send E-Mail in HTML this project uses [Spring with Thymeleaf](https://www.thymeleaf.org/doc/articles/springmail.html).

The HTML templates must be defined in the [templates](src/main/resources/templates). 

For CSS Compatibilty see [Campaign Monitor](https://www.campaignmonitor.com/css). 

### Tech Stack
| Layer     |  Technologie    |  Link     |
|-----------|------------|-----------|
|Frontend   | Angular9 + | [ESTA-Web](https://confluence.sbb.ch/display/CLEW/ESTA-Web) |
|Backend    |Java Spring Boot 2.5 | [ESTA-Backend (Spring-Boot)](https://confluence.sbb.ch/pages/viewpage.action?pageId=1306395091) |
|           |Lombok | https://projectlombok.org/ |
|           |OpenAPI | https://swagger.io/specification/ |
|Database	|PostgreSQL| [Service PostgreSQL](https://confluence.sbb.ch/display/PLA/Service+PostgreSQL)|
|Messaging	|Apache Kafka| [KAFKA Home](https://confluence.sbb.ch/display/KAFKA/KAFKA+Home)|
|Infrastructure|	Openshift AWS 4.0| [ESTA-Cloud](https://confluence.sbb.ch/display/CLEW/ESTA-Cloud)|
|Deployment	|ESTA Cloud Pipeline| [Esta Cloud Pipeline](https://confluence.sbb.ch/display/CLEW/Esta+Cloud+Pipeline)|
|Interface|  API Management oder ähnliches Tool nach Entscheid KISPF-198 <br> REST & Json| [KISPF-198](https://flow.sbb.ch/browse/KISPF-198) - SKI/SKI+ API Strategie|

### Authentication for SMTP

According to https://sbb.sharepoint.com/sites/ict-workplace-training/SitePages/Mailversand-aus-Anwendungen.aspx we use SMTP with port 587 and use our functional user to authenticate to the server.

To run the service locally, make sure you set the environment variable `FXATL_A_PASSWORD` on your machine.