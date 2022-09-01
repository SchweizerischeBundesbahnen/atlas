# LiDi Backend

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
  * [Tech Stack](#tech-stack)
  * [Test RESTful Web services](#test-restful-web-services)
- [Scheduler Export](#scheduler-export)

<!-- tocstop -->

## ATLAS

This application is part of ATLAS. General documentation is
available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas-backend/browse/README.md#big-picture)
.

## Project Versioning

This project uses [Semantic Versioning](https://semver.org/).

## Links

### Localhost

* Swagger UI: http://localhost:8082/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Api Docs as JSON: http://localhost:8082/v3/api-docs/
* Api Docs as YAML: http://localhost:8082/v3/api-docs.yaml
* Rest-api generated doc: http://localhost:8082/static/rest-api.html

### Development

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=line-directory-dev
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://line-directory.dev.sbb-cloud.net
* Swagger
  UI: https://line-directory.dev.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Test

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=line-directory-test
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
* Project deploy: https://line-directory.test.sbb-cloud.net
* Swagger
  UI: https://line-directory.test.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Integration

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=line-directory-int
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
* Project deploy: https://line-directory.int.sbb-cloud.net
* Swagger
  UI: https://line-directory.int.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Production

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=line-directory-prod
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-prod
* Project deploy: https://line-directory.prod.sbb-cloud.net
* Swagger
  UI:  https://line-directory.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Project Infrastructure

* Jenkins: https://ci.sbb.ch/job/KI_ATLAS/job/atlas/
* Sonarqube: https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas&branch=master
* JFrog / Artifactory
    * Maven repository: https://bin.sbb.ch/ui/repos/tree/General/atlas.mvn
    * Docker registry: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker
* Infrastructure
  documentation: https://confluence.sbb.ch/display/ATLAS/%5BATLAS%5D+7.1.+Infrastruktur+Ebene+1

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

### Test RESTful Web services

We use InteliJ HTTP Client for
testing: https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html.

U need only to configure some secrets:

* in `/http-requests` directory create a new json file: `http-client.private.env.json`
* get the secrets from ATLAS Confluence Page "Restricted Access -> E2ETests with Cypress":

```javascript
{
  "local"
:
  {
    "clientSecret"
  :
    "<client secret>",
      "username"
  :
    "<user name>",
      /* IMPORTANT !!!*/
      "password"
  :
    "<password: the % must be url-encoded as %25>"
  }
,
  "integration"
:
  {
    "clientSecret"
  :
    "" // integration
  }
,
  "production"
:
  {
    "clientSecret"
  :
    "" //production 
  }
  // ... etc...
}
```

## Scheduler Export

LiDi exports CSV files and put them on [Atlas Amazon S3 Client](../amazon-s3/README.md).
These exports are triggered
by [ExportScheduler.java](src/main/java/ch/sbb/line/directory/scheduler/ExportScheduler.java).

Since LiDi is deployed as multiple instances we
use [ShedLock](https://github.com/lukas-krecan/ShedLock) to prevent a scheduling job from running
multiple times.