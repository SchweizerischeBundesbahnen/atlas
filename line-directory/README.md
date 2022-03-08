# LiDi Backend

<!-- toc -->

- [ATLAS](#atlas)
- [Versioning](#versioning)
- [Links](#links)
- [Development](#development)

<!-- tocstop -->

## ATLAS
This application is part of ATLAS. General documentation is available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas-backend/browse/README.md#big-picture).

## Versioning
This project uses [Semantic Versioning](https://semver.org/).

## Links

### Localhost
* Swagger UI: http://localhost:8082/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Api Docs as JSON: http://localhost:8082/v3/api-docs/
* Api Docs as YAML: http://localhost:8082/v3/api-docs.yaml
* Rest-api generated doc: http://localhost:8082/static/rest-api.html

### Development
* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=line-directory-dev
* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://line-directory.dev.sbb-cloud.net
* Swagger UI: https://line-directory.dev.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Test
* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=line-directory-test
* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
* Project deploy: https://line-directory.test.sbb-cloud.net
* Swagger UI: https://line-directory.test.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Integration
* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=line-directory-int
* Openshift Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
* Project deploy: https://line-directory.int.sbb-cloud.net
* Swagger UI: https://line-directory.int.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Production
* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=line-directory-prod
* Openshift Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-prod
* Project deploy: https://line-directory.prod.sbb-cloud.net
* Swagger UI:  https://line-directory.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config


### Project Infrastructure
* Jenkins: https://ci.sbb.ch/job/KI_ATLAS/job/line-directory-backend/
* Sonarqube: https://codequality.sbb.ch/dashboard?id=ch.sbb%3Aline-directory-backend
* JFrog / Artifactory
  * Maven repository: https://bin.sbb.ch/ui/repos/tree/General/atlas.mvn
  * Docker registry: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker
* Infrastructure documentation: https://confluence.sbb.ch/display/ATLAS/%5BATLAS%5D+7.1.+Infrastruktur+Ebene+1

## Development
For an easy local development setup, we provide a `docker-compose.yml`, which can be used to start dependent infrastructure.

### Test RESTful Web services
We use InteliJ HTTP Client for testing: https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html.

U need only to configure some secrets:
* in `/http-requests` directory create a new json file: `http-client.private.env.json`
* get the secrets from ATLAS Confluence Page "Restricted Access -> E2ETests with Cypress":
```javascript
{
  "local": {
    "clientSecret": "<client secret>",
    "username": "<user name>",
    /* IMPORTANT !!!*/
    "password": "<password: the % must be url-encoded as %25>"
  },
  "integration": {
    "clientSecret": "" // integration
  },
  "production": {
    "clientSecret": "" //production 
  }
  // ... etc...
}
```

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
