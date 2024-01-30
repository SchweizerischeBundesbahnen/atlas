# GO-V - Business Organisation Directory Backend

<!-- toc -->

- [ATLAS](#atlas)
- [GO-V - Geschäftsorganisations-Verzeichnis](#go-v---geschaftsorganisations-verzeichnis)
- [Project Versioning](#project-versioning)
- [Links](#links)
    * [Localhost](#localhost)
    * [Development](#development)
    * [Test](#test)
    * [Integration](#integration)
    * [Production](#production)
    * [Project Infrastructure](#project-infrastructure)
- [Development](#development-1)
- [CRD Certificate handling](#crd-certificate-handling)
    * [From PEM to jks](#from-pem-to-jks)
    * [Tech Stack](#tech-stack)
    * [Test RESTful Web services](#test-restful-web-services)
    * [Proxy configuration to load Transport Companies from within SBB Network](#proxy-configuration-to-load-transport-companies-from-within-sbb-network)

<!-- tocstop -->

## ATLAS

This application is part of ATLAS. General documentation is
available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/README.md).

## GO-V - Geschäftsorganisations-Verzeichnis

For more information
see [GO-V - Geschäftsorganisations-Verzeichnis](https://confluence.sbb.ch/pages/viewpage.action?pageId=1954104583)

## Project Versioning

This project uses [Semantic Versioning](https://semver.org/).

## Links

### Localhost

* Swagger UI: http://localhost:8083/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Api Docs as JSON: http://localhost:8083/v3/api-docs/
* Api Docs as YAML: http://localhost:8083/v3/api-docs.yaml
* Rest-api generated doc: http://localhost:8083/static/rest-api.html

### Development

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=business-organisation-directory-dev
* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://business-organisation-directory.dev.sbb-cloud.net
* Swagger
  UI: https://business-organisation-directory.dev.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Test

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=business-organisation-directory-test
* Openshift Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
* Project deploy: https://business-organisation-directory.test.sbb-cloud.net
* Swagger
  UI: https://business-organisation-directory.test.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Integration

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=business-organisation-directory-int
* Openshift Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
* Project deploy: https://business-organisation-directory.int.sbb-cloud.net
* Swagger
  UI: https://business-organisation-directory.int.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Production

* DB AWS PostgreSQL: https://ssp.dbms.sbb.ch/manageinstanceaws?i=business-organisation-directory-prod
* Openshift Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-prod
* Project deploy: https://business-organisation-directory.prod.sbb-cloud.net
* Swagger UI:  https://business-organisation-directory.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Project Infrastructure

* Jenkins: https://ci.sbb.ch/job/KI_ATLAS/job/atlas/
* Sonarqube: https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas&branch=master
* JFrog / Artifactory
    * Maven repository: https://bin.sbb.ch/ui/repos/tree/General/atlas.mvn
    * Docker registry: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker
* Infrastructure documentation: https://confluence.sbb.ch/display/ATLAS/%5BATLAS%5D+7.1.+Infrastruktur+Ebene+1

## Development

CRD Import requires passwords. These can be imported by using bash/cmd. The statement can be
found [here](https://confluence.sbb.ch/pages/viewpage.action?pageId=1881802050)

## CRD Certificate handling

### From PEM to jks

```bash
# name defines the alias within the p12 file
openssl pkcs12 -in keystore.pem -out keypair.p12 -export -name "atlas"

# Convert the p12 to a jks
keytool -importkeystore -srckeystore keypair.p12 -srcstoretype pkcs12 -destkeystore keystore.jks

# Cleanup
rm keypair.p12
rm keystore.pem
```

### Tech Stack

See [Tech Stack Documentation](../documentation/tech-stack-service.md)

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

### Proxy configuration to load Transport Companies from within SBB Network

IntelliJ: Insert into VM options of your Spring Boot Run Configuration the following arguments:

~~~
-Dhttps.proxyHost=zscaler.sbb.ch -Dhttps.proxyPort=10465 -Dhttp.nonProxyHosts=login.microsoftonline.com
~~~

### Import TU-GO Relations from csv

```
# From csv-import-tool dir
npm start -- --token $TOKEN --url https://atlas-int.api.sbb.ch:443/business-organisation-directory/v1/transport-company-relations --csv ../../business-organisation-directory/scripts/220803_ATLAS_EXP_VERB_TU_GO.csv
```
