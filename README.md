# Atlas

This is the main repository for business relevant services for ATLAS.

<!-- toc -->

- [Big Picture](#big-picture)
- [Links](#links)
- [Monitoring and Logging](#monitoring-and-logging)
- [Development](#development)
  * [Run locally](#run-locally)
- [Structure](#structure)
  * [apim-configuration](#apim-configuration)
  * [charts](#charts)
  * [Gateway](#gateway)
  * [Line-directory](#line-directory)
  * [Versioning lib](#versioning-lib)
  * [Frontend](#frontend)

<!-- tocstop -->

[![Build Status](https://ci.sbb.ch/job/KI_ATLAS/job/line-directory-backend/job/master/badge/icon)](https://ci.sbb.ch/job/KI_ATLAS/job/line-directory-backend/job/master/)
[![Build Status E2E](https://ci.sbb.ch/job/KI_ATLAS_E2E/job/line-directory-backend/job/master/badge/icon)](https://ci.sbb.ch/job/KI_ATLAS_E2E/job/line-directory-backend/job/master/)
[![Quality Gate Status](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb%3Aline-directory-backend&metric=alert_status)](https://codequality.sbb.ch/dashboard?id=ch.sbb%3Aline-directory-backend)
## Big Picture
ATLAS shall be a platform, on which relevant data for customer information can be managed. \
Applications on this platform share their tech stack and have the same monitoring and logging.

It's applications share the following architectural goal:

![ATLAS Big Picture](documentation/ATLAS_Infrastruktur.svg)

## Links

- **Jenkins**: https://ci.sbb.ch/job/KI_ATLAS/job/line-directory-backend/job/master/
- **Jenkins-E2E**: https://ci.sbb.ch/job/KI_ATLAS_E2E/job/line-directory-backend/
- **Sonarqube**: https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas&branch=master
- **JFrog Artifactory**:
  - **npm**: https://bin.sbb.ch/ui/repos/tree/General/atlas.npm%2Fatlas-frontend
  - **docker**: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker%2Fatlas-frontend
- **Openshift**:
  - **Dev**: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
  - **Test**: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
- **Deployment**:
  - **Dev**: https://atlas-frontend-dev.apps.aws01t.sbb-aws-test.net
  - **Test**: https://atlas-frontend-test.apps.aws01t.sbb-aws-test.net


## Monitoring and Logging
* [Monitoring (Instana and Actuator)](documentation/Monitoring.md)
* [Logging to Splunk](documentation/Logging.md)

## Development
### Run locally

- Make sure your needed business services are up
- Start the gateway (the frontend Angular application uses it as a target for API calls)

## Structure

Quick overview of the modules. There are more detailed `README`s available within each module.

### apim-configuration

Module, which will be published to APIM and served on the SBB developer portal.

The module combines the APIs from services into one composed API.

### charts

Contains helm charts for the entire ATLAS application. 
We use one helm chart with a flat structure to publish multiple `Deployments`, `Services` and `Routes`.

You can generate the helm charts yamls, which will be deployed by using helm from the commandline. 
This is useful for debugging and local inspection of value resolution.

```bash
# Working dir ./charts/atlas
# Generate Template for atlas-dev
helm template . -n atlas-dev -f values-atlas-dev.yaml
```

### Gateway

Module to handle routing of API endpoints to the respective business applications. Start this locally, if you want to run the angular UI.
See [Gateway documentation](gateway/README.md);

### Line-directory

Business service for lines, sublines and timetable field numbers. All of these business objects use the atlas own versioning.
See [Line-directory documentation](line-directory/README.md);

### Versioning lib

Library used to perform business object versioning according to the [documentation](https://confluence.sbb.ch/pages/viewpage.action?spaceKey=ATLAS&title=%5BATLAS%5D+8.7+Versionierung)
See [Versioning documentation](versioning/README.md);


### Frontend
ATLAS Angular App. See [Frontend documentation](frontend/README.md);
