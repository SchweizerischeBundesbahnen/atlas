# UserAdministration Backend

<!-- toc -->

- [Links](#links)
  * [Localhost](#localhost)
  * [Development](#development)
  * [Test](#test)
  * [Integration](#integration)
  * [Production](#production)
  * [Project Infrastructure](#project-infrastructure)
- [Development](#development-1)
  * [Tech Stack](#tech-stack)
- [GraphAPI](#graphapi)

<!-- tocstop -->

## Links

### Localhost

* Swagger UI: http://localhost:8086/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
* Api Docs as JSON: http://localhost:8086/v3/api-docs/
* Api Docs as YAML: http://localhost:8086/v3/api-docs.yaml
* Rest-api generated doc: http://localhost:8086/static/rest-api.html

### Development

* DB AWS PostgreSQL: https://backstage.sbb-cloud.net/catalog/default/resource/user-administration-dev
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
* Project deploy: https://user-administration.dev.sbb-cloud.net
* Swagger UI: https://user-administration.dev.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Test

* DB AWS PostgreSQL: https://backstage.sbb-cloud.net/catalog/default/resource/user-administration-test
* Openshift
  Project: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
* Project deploy: https://user-administration.test.sbb-cloud.net
* Swagger UI: https://user-administration.test.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Integration

* DB AWS PostgreSQL: https://backstage.sbb-cloud.net/catalog/default/resource/user-administration-int
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
* Project deploy: https://user-administration.int.sbb-cloud.net
* Swagger UI: https://user-administration.int.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

### Production

* DB AWS PostgreSQL: https://backstage.sbb-cloud.net/catalog/default/resource/user-administration-prod
* Openshift
  Project: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-prod
* Project deploy: https://user-administration.prod.sbb-cloud.net
* Swagger UI:  https://user-administration.sbb-cloud.net/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

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
## GraphAPI

This project stores roles and BusinessOrganisation responsibilities of our users.
In our database we only store SBBUIDs.


We use Microsoft Graph API to retrieve additional account information for each SBBUIDS. This way we
may retrieve the current mail address, name and account status on the fly, as they are subject to
change.

We use the ApplicationRegistration of our API to perform the requests via Graph API.
The relevant properties are:

```yaml
azure-config:
  tenant-id: 2cda5d11-f0ac-46b3-967d-af1b2e1bd01a
  azure-ad-secret: ${AZURE_AD_SECRET}
  app-registration-id: 87e6e634-6ba1-4e7a-869d-3348b4c3eafc
```

These are included and initialized with `GraphClientConfig` to provide an
injectable `GraphServiceClient` bean.
In order to perform the requests the application was permissioned the `User.Read`
and `User.Read.All` in Azure.

This can be checked out on
e.g. https://portal.azure.com/#view/Microsoft_AAD_RegisteredApps/ApplicationMenuBlade/~/CallAnAPI/appId/f3cdcb3e-1e95-4591-b664-4526d00f5d66
for our prod environment.
