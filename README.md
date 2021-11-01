# Timetable field number Frontend

[![Build Status](https://ci.sbb.ch/job/KI_ATLAS/job/atlas-frontend/job/master/badge/icon)](https://ci.sbb.ch/job/KI_ATLAS/job/atlas-frontend/job/master/)
[![E2E Tests](https://ci.sbb.ch/job/KI_ATLAS_E2E/job/atlas-frontend/job/master/badge/icon)](https://ci.sbb.ch/job/KI_ATLAS_E2E/job/atlas-frontend/job/master/)

[![Quality Gate Status](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb%3Aatlas-frontend&metric=alert_status)](https://codequality.sbb.ch/dashboard?id=ch.sbb%3Aatlas-frontend)

This project was generated from [esta-cloud-angular](https://code.sbb.ch/projects/KD_ESTA_BLUEPRINTS/repos/esta-cloud-angular/browse).
See [ESTA Documentation](https://confluence.sbb.ch/display/CLEW/ESTA-Web).

<!-- toc -->

- [Links](#links)
- [Development](#development)
  - [Cypress E2E](#cypress-e2e)
    - [Run cypress test](#run-cypress-test)
    - [Cypress E2E CI-Jenkins](#cypress-e2e-ci-jenkins)
      - [Cypress Tests results for troubleshooting](#cypress-tests-results-for-troubleshooting)
  - [Set SBB Artifactory as npm registry](#set-sbb-artifactory-as-npm-registry)
  - [Azure AD App Registration](#azure-ad-app-registration)
- [Monitoring and Logging](#monitoring-and-logging)

<!-- tocstop -->

## Links

- **Jenkins**: https://ci.sbb.ch/job/KI_ATLAS/job/atlas-frontend/
- **Jenkins-E2E**: https://ci.sbb.ch/job/KI_ATLAS_E2E/job/atlas-frontend/
- **Sonarqube**: https://codequality.sbb.ch/dashboard?id=ch.sbb%3Aatlas-frontend
- **JFrog Artifactory**:
  - **npm**: https://bin.sbb.ch/ui/repos/tree/General/atlas.npm%2Fatlas-frontend
  - **docker**: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker%2Fatlas-frontend
- **Openshift**:
  - **Dev**: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
  - **Test**: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
- **Deployment**:
  - **Dev**: https://atlas-frontend-dev.apps.aws01t.sbb-aws-test.net
  - **Test**: https://atlas-frontend-test.apps.aws01t.sbb-aws-test.net

## Development

### Cypress E2E

To run the cypress tests on your machine you have to replace the **clientId** and the **clientSecretId** properties
in the [cypress.json](cypress.json). The credentials are stored [here](https://confluence.sbb.ch/pages/viewpage.action?pageId=1881802050).

#### Run cypress test

1. replace **clientId** and the **clientSecretId** in [cypress.json](cypress.json) (see above)
2. replace **baseUrl** with your localhost running app (e.g. http://localhost:4200) or your deployed app url in [cypress.json](cypress.json)
3. run cypress:
   1. with the console for debugging: `npm run cypress:open` or `cypress open`
   2. as headless test: `npm run cypress:run` or `cypress run`

#### Cypress E2E CI-Jenkins

In [Jenkins](https://ci.sbb.ch/) under the organization Folder [ATLAS_Cypress_E2E](https://ci.sbb.ch/job/KI_ATLAS_E2E/)
the Cypress E2E tests are defined in the job [atlas-frontend](https://ci.sbb.ch/job/KI_ATLAS_E2E/job/atlas-frontend/).

This job is executed only when is triggered by **postCiDeploymentJob** defined in the main [Jenkins pipeline](https://ci.sbb.ch/job/KI_ATLAS/job/atlas-frontend/),
see the [Jenkinsfile](Jenkinsfile). On this job the commit push notification is disabled.

The Jenkinsfile is stored in [cypress/Jenkinsfile](cypress/Jenkinsfile)

##### Cypress Tests results for troubleshooting

After each job execution a cypress video is captured and stored as **Build Artifacts**.

In case of a failure under **Build Artifacts** are stored 2 directories, one with the logs and the second with the screenshots.

### Set SBB Artifactory as npm registry

See [set SBB Artifactory as npm registry](https://confluence.sbb.ch/display/CLEW/Configuration+Artifactory+7.x+as+NPM+Registry)

### Azure AD App Registration

So you want to use AzureAD to login your users?

1. Create azure-app-registration.yml ([Dokumentation](https://confluence.sbb.ch/display/IAM/Azure+AD+API%3A+Self-Service+API+for+App+Registrations+with+Azure+AD#AzureADAPI:SelfServiceAPIforAppRegistrationswithAzureAD-1.1.Createapp-registrationsusingthefile-basedAPIendpoint))
2. Use the [REST-API](https://azure-ad.api.sbb.ch/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#) to POST/create your application
3. The configured owner may edit it with the same REST-API using PUT

Finding an application within the registry is best performed by using the GET /v1/applications and look for a name.

## Monitoring and Logging

- [Logging to Splunk](documentation/Logging.md)
