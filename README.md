# LiDi Backend 
[![Build Status](https://ci.sbb.ch/job/KI_ATLAS/job/line-directory-backend/job/master/badge/icon)](https://ci.sbb.ch/job/KI_ATLAS/job/line-directory-backend/job/master/)
[![Quality Gate Status](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb%3Aline-directory-frontend&metric=alert_status)](https://codequality.sbb.ch/dashboard?id=ch.sbb%3Aline-directory-frontend)

<!-- toc -->

- [Big Picture](#big-picture)
- [Versioning](#versioning)
- [Tech Stack](#tech-stack)
- [Development](#development)
  * [PostgreSQL Docker](#postgresql-docker)
- [Links](#links)
  * [OpenApi Links](#openapi-links)
- [Monitoring and Logging](#monitoring-and-logging)

<!-- tocstop -->

## Big Picture
![ATLAS Big Picture](documentation/ATLAS_Infrastruktur.svg)

## Versioning
This project uses the [Semantic Versioning](https://semver.org/) for the versioning. 

## Tech Stack 
| Layer     |  Techno    |  Link     |
|-----------|------------|-----------|
|Frontend   | Angular9 + | [ESTA-Web](https://confluence.sbb.ch/display/CLEW/ESTA-Web) |
|Backend    |Java Spring Boot 2.4 | [ESTA-Backend (Spring-Boot)](https://confluence.sbb.ch/pages/viewpage.action?pageId=1306395091) |
|           |Lombok | https://projectlombok.org/ |
|           |OpenAPI | https://swagger.io/specification/ |
|Database	|PostGreSQL| [Service PostgreSQL](https://confluence.sbb.ch/display/PLA/Service+PostgreSQL)|
|Messaging	|Apache Kafka| [KAFKA Home](https://confluence.sbb.ch/display/KAFKA/KAFKA+Home)|
|Infrastructure|	Openshift AWS 4.0| [ESTA-Cloud](https://confluence.sbb.ch/display/CLEW/ESTA-Cloud)|
|Deployment	|ESTA Cloud Pipeline| [Esta Cloud Pipeline](https://confluence.sbb.ch/display/CLEW/Esta+Cloud+Pipeline)|
|Interface|  API Management oder ähnliches Tool nach Entscheid KISPF-198 <br> REST & Json| [KISPF-198](https://flow.sbb.ch/browse/KISPF-198) - SKI/SKI+ API Strategie|        

## Development
### PostgreSQL Docker
Run PostgreSQL in docker:
~~~
docker-compose up
~~~

Stop PostgreSQL container:
~~~
docker-compose down
~~~

Stop PostgreSQL container and remove volume:
~~~
docker-compose down -v 
~~~

## Links
* Dev:
  * DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=line-directory-dev
  * Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
  * Project deploy: https://line-directory-backend-dev.apps.aws01t.sbb-aws-test.net
  * Swagger UI: https://line-directory-backend-dev.apps.aws01t.sbb-aws-test.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Developer Portal Prod:
* Developer Portal Int:
* Openshift Prod:
* Openshift Int:
* Openshift Test:
* Openshift Dev: 
* Jenkins: https://ci.sbb.ch/job/KI_ATLAS/job/line-directory-backend/
* Sonarqube: https://codequality.sbb.ch/dashboard?id=ch.sbb%3Aline-directory
* JFrog 
  * Maven repository: https://bin.sbb.ch/ui/repos/tree/General/atlas.mvn
  * Docker registry: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker
* Infrastruktur: https://confluence.sbb.ch/display/ATLAS/%5BATLAS%5D+7.1.+Infrastruktur+Ebene+1

### OpenApi Links

* Swagger UI: http://localhost:8082/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Api Docs as JSON: http://localhost:8082/v3/api-docs/
* Api Docs as YAML: http://localhost:8082/v3/api-docs.yaml

## Monitoring and Logging
* [Monitoring (Instana and Actuator)](documentation/Monitoring.md)
* [Logging to Splunk](documentation/Logging.md)
