# Atlas

This is the repository for business relevant services for ATLAS.


<!-- toc -->

- [Big Picture](#big-picture)
- [Links](#links)
- [ATLAS CI/CD](#atlas-cicd)
  * [E2E Videos Results](#e2e-videos-results)
- [Stages and their purpose](#stages-and-their-purpose)
- [Monitoring and Logging](#monitoring-and-logging)
  * [Correlation-Id](#correlation-id)
- [Development](#development)
  * [Run locally](#run-locally)
  * [Monorepo](#monorepo)
  * [Running Python scripts](#running-python-scripts)
- [Structure](#structure)
  * [APIM-configuration](#apim-configuration)
  * [Charts](#charts)
  * [Gateway](#gateway)
  * [Kafka](#kafka)
  * [Line-directory](#line-directory)
  * [Business-organisation-directory](#business-organisation-directory)
  * [Mail Service](#mail-service)
  * [Workflow](#workflow)
  * [User Administration](#user-administration)
  * [Service-Point-Directory](#service-point-directory)
  * [Prm-Directory](#prm-directory)
  * [Import Service-Point](#import-service-point)
  * [Base Service lib](#base-service-lib)
  * [Frontend](#frontend)
- [Troubleshooting](#troubleshooting)

<!-- tocstop -->

Tekton CI/CD
Status: [![Build Status](https://esta-tekton-controller-atlas-tekton.sbb-cloud.net/api/status/icon/KI_ATLAS/atlas/build)](https://tekton-control-panel-atlas-tekton.sbb-cloud.net/projects/KI_ATLAS/repositories/atlas)

SonarQube:
[![Quality Gate Status](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb.atlas%3Aatlas&metric=alert_status&token=sqb_ec605dab116926bc9d32354da827978de8b35214)](https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas)
[![Maintainability Rating](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb.atlas%3Aatlas&metric=sqale_rating&token=sqb_ec605dab116926bc9d32354da827978de8b35214)](https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas)
[![Vulnerabilities](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb.atlas%3Aatlas&metric=vulnerabilities&token=sqb_ec605dab116926bc9d32354da827978de8b35214)](https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas)
[![Technical Debt](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb.atlas%3Aatlas&metric=sqale_index&token=sqb_ec605dab116926bc9d32354da827978de8b35214)](https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas)
[![Coverage](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb.atlas%3Aatlas&metric=coverage&token=sqb_ec605dab116926bc9d32354da827978de8b35214)](https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas)

## Big Picture

ATLAS shall be a platform, on which relevant data for customer information can be managed. \
Applications on this platform share their tech stack and have the same monitoring and logging.

It's applications share the following architectural goal:
![ATLAS Big Picture](documentation/image/ATLAS_Infrastruktur.svg)

Application MEGAID: `ADB3AE9A60E510ED`

## Links

- **Tekton**: https://tekton-control-panel-atlas-tekton.sbb-cloud.net/projects/KI_ATLAS/repositories/atlas
- **ArgoCD**:
    - **DEV/TEST**: https://argocd-server-atlas-argocd.apps.aws01t.sbb-aws-test.net/
    - **INT/PROD**: https://argocd-server-atlas-argocd.apps.maggie.sbb-aws.net/
- **Sonarqube**: https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas&branch=master
- **JFrog Artifactory**:
    - **docker**: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker/atlas/atlas-frontend
- **Openshift**:
    - **Dev**: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
    - **Test**: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
    - **Int**: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
    - **Prod**: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-prod
- **Deployment**:
    - **Dev**: https://atlas.dev.sbb-cloud.net
    - **Test**: https://atlas.test.sbb-cloud.net
    - **Int**: https://atlas.int.sbb-cloud.net
    - **Prod**: https://atlas.sbb-cloud.net
- **Developer Portal**:
  - **INT**: https://developer-int.sbb.ch/apis?text=atlas&all=
  - **PROD**: https://developer.sbb.ch/apis?text=atlas&all=
- **API Management - 3scale**:
  - **INT**: https://api-management.int.sbb-cloud.net
  - **PROD**: https://api-management.prod.sbb-cloud.net
- **Vulnerability Management (VMC)**: https://vmc.sbb.ch/de/reporting/mg/ADB3AE9A60E510ED/vulnerabilitylist

## ATLAS CI/CD

![ATLAS CI/CD](documentation/image/ATLAS-CI-CD.svg)

To apply the CI/CD we use the following additional repository:

* [atlas-tekton-pipeline-templates](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas-tekton-pipeline-templates/browse): Custom
  templates for esta-tekton which will be automatically applied to the atlas-tekton. See
  the [documentation](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas-tekton-pipeline-templates/browse/README.md)
* [docker](https://code.sbb.ch/projects/KI_ATLAS/repos/docker/browse): Atlas custom docker images. See
  the [documentation](https://code.sbb.ch/projects/KI_ATLAS/repos/docker/browse/README.md)
* [atlas-argocd](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas-argocd/browse): contains the Charts used to deploy atlas Apps,
  Jobs, Atlas ArgoCD, etc... See the [documentation](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas-argocd/browse/README.md)

### E2E Videos Results

The Cypress Videos results are stored to the following Amazon S3 Bucket:

* Bucket name: **atlas-cypress-ci-dev**
* [Credentials](https://vault-nonprod.sbb.ch/ui/vault/secrets/kv/show/SKI/ATLAS/amazon_s3/cypress-ci)

## Stages and their purpose

* [Stages](documentation/stages.md)

## Monitoring and Logging

* [Monitoring (Instana and Actuator)](documentation/Monitoring.md)
* [Logging to Splunk](documentation/Logging.md)

### Correlation-Id

The Atlas services use [Micrometer](https://micrometer.io/) to add to the log a
**Correlation-Id** which spreads between the services up to the snack bar in the fronted.

We can use the **Correlation-Id** to search it in [Splunk](documentation/Logging.md) or [Instana](documentation/Monitoring.md).

## Development

### Run locally

- For an easy local development setup, we provide a [docker-compose.yml](docker-compose.yml), which
  can be used to start dependent infrastructure for atlas.
- Make sure your needed business services are up
- Start the gateway (the frontend Angular application uses it as a target for API calls)

Run needed services for atlas in docker:

~~~
# -d to run in background
docker-compose up -d
~~~

Stop infrastructure container:

~~~
docker-compose down
~~~

Stop infrastructure container and remove volume (deletes persistent content):

~~~
docker-compose down -v 
~~~


### Monorepo

ATLAS has a Monorepo project structure. The CI/CD is executed on each module. For each push event on
master a Jenkins pipiline is executed. The modules are versionied and deployed with the same version
number.

![ATLAS Monorepo](documentation/image/ATLAS-Mono-Repo.svg)

### Running Python scripts

The backend services include python scripts, which were used to generate SQL-Insert-Scripts
from `xlsx` or `csv` files.
You can download and install python from https://www.python.org/downloads/.

## Structure

Quick overview of the modules. There are more detailed `README`s available within each module.

### APIM-configuration

Module, which will be published to APIM and served on the SBB developer portal.

The module combines the APIs from services into one composed API.

### Charts

Contains helm charts for the entire ATLAS application.
We use one helm chart with a flat structure to publish multiple `Deployments`, `Services`
and `Routes`.

You can generate the helm charts yamls, which will be deployed by using helm from the commandline.
This is useful for debugging and local inspection of value resolution.

```bash
# Working dir ./charts/atlas
# Generate Template for atlas-dev
helm template . -n atlas-dev -f values-atlas-dev.yaml
```

### Gateway

Module to handle routing of API endpoints to the respective business applications. Start this
locally, if you want to run the angular UI.
See [Gateway documentation](gateway/README.md);

### Kafka

This folder [kafka](kafka) is used to store `json` files that create topics using kafka-automation
with estaCloudPipeline.
More information can be found in the [kafka documentation](documentation/kafka.md).

### Line-directory

Business service for lines, sublines and timetable field numbers. All of these business objects use
the atlas own versioning.
See [Line-directory documentation](line-directory/README.md);

### Business-organisation-directory

Business service for business organisations. All of these business objects use the atlas own
versioning.
See [Business-Organisation-directory documentation](business-organisation-directory/README.md);

### Mail Service

Service used by Atlas to send emails. See [Mail Service Documentation](mail/README.md)

### Workflow

Service used to implement ATLAS Workflows. See [documentation](workflow/README.md)

### User Administration

User Administration provides the backend for creating and maintaining role and business organisation assignments for user.
See [UserAdministration Documentation](user-administration/README.md) for more.

### Service-Point-Directory

Business service for `ServicePoints`, `TrafficPointElements` and `LoadingPoints`. All of these business objects use the atlas own
versioning.
See [Service-Point-Directory documentation](service-point-directory/README.md);

### Prm-Directory

Business service for PRM (Person with Reduced Mobility) Data. All of these business objects use the atlas own
versioning.
See [Prm-Directory documentation](prm-directory/README.md);

### Import Service-Point

Spring Batch Job to import CSV from Amazon or provided files to [Service-Point-Directory](#service-point-directory)
See [Import-Service-Point documentation](import-service-point/README.md);

### Base Service lib

Libraries used to perform:

* business object **versioning** according to
  the [documentation](https://confluence.sbb.ch/pages/viewpage.action?spaceKey=ATLAS&title=%5BATLAS%5D+8.7+Versionierung)
  See [Versioning documentation](base-atlas/documentation/versioning/README.md);
* CSV and ZIP exports. See [Export documentation](base-atlas/documentation/export/README.md);
* Amazon REST Client operations.
  See [Amazon documentation](base-atlas/documentation/amazon/README.md);

### Frontend

ATLAS Angular App. See [Frontend documentation](frontend/README.md);

## Troubleshooting

* [Sonarqube](documentation/Troubleshooting.md)
