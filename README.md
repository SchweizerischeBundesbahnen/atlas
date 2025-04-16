# Atlas

ATLAS is the SKI business application for public transport in Switzerland.

It enables modular digitization of SKI business processes, which are checked, optimized, standardized and then digitized using workflows. This enables more efficient collaboration between the BAV, infrastructure operators, transport companies and SKI.

Thanks to the modular structure implemented using state-of-the-art technologies, the platform can be expanded with additional business processes within a short period of time and cost-effectively in the coming years.

:construction: **At this point we do not accept opensource contributions or issues.** :construction:

<!-- toc -->

- [Big Picture](#big-picture)
- [Links](#links)
  * [Api Auth Gateway](#api-auth-gateway)
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
  * [Location Service](#location-service)
  * [Base Service lib](#base-service-lib)
  * [Frontend](#frontend)
- [How to](#how-to)
  * [Prerequisite](#prerequisite)
  * [Development](#development)
  * [Start atlas locally](#start-atlas-locally)
  * [Transfer Pull Request from GitHub to BitBucket](#transfer-pull-request-from-github-to-bitbucket)
    + [Setup](#setup)
    + [Transfer Pull Request](#transfer-pull-request)
- [Mocks](#mocks)
  * [SMTP Server](#smtp-server)
  * [Wiremock](#wiremock)
  * [DB local](#db-local)

<!-- tocstop -->

## Big Picture

ATLAS shall be a platform, on which relevant data for customer information can be managed. 
Applications on this platform share their tech stack and have the same monitoring and logging.

## Links

* [atlas web application](https://atlas.app.sbb.ch/)
* [atlas release notes](https://atlas-info.app.sbb.ch/static/atlas-release-notes.html)
* [atlas API](https://developer.sbb.ch/apis/atlas/information)

### Api Auth Gateway

Gateway used by the frontend to fake authenticate read access to the atlas platform.
See [Api Auth Gateway documentation](api-auth-gateway/README.md);

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

### Location Service

Service to assign SLOIDs centrally for all ATLAS applications.
See [Location Service documentation](location/README.md);

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

## How to

### Prerequisite

To run atlas locally, the following tools are required:

1. [Java JDK 21](https://bell-sw.com/pages/downloads/#jdk-21-lts)
2. [Node 20](https://nodejs.org/en/download)
3. [Docker](https://docs.docker.com/engine/install/)
4. [docker-compose](https://docs.docker.com/compose/install/)

### Development

:warning: **At this point atlas can be started, but some issues still exist.
We are working to resolve the issues as soon as possible.** :warning:

Atlas uses [Gradle](https://gradle.org/).

1. Build: ```./gradlew build```
2. Clean: ```./gradlew clean```
3. Clean: ```./gradlew check```
4. Build a single service: ```./gradlew :line-directory:build```. Generally to execute some Gradle task on a specific module use: ```./gradlew :{module}:{task}```
5. Start app: ```./gradlew :{module}:bootRun```, e.g. ```./gradlew :line-directory:bootRun``` 

:warning:
**Notice** to start Spring Boot services use the **github** profile:
1. ```./gradlew :{module}:bootRun --args='--spring.profiles.active=github'```, e.g.: ```./gradlew :line-directory:bootRun --args='--spring.profiles.active=github'```

### Start atlas locally

1. ```./gradlew build```
2. ```docker-compose up```
3. ```./gradlew :{module}:bootRun --args='--spring.profiles.active=github'```: run for each service
4. Change dir to frontend: ```cd frontend```
5. ```npm start```


### Transfer Pull Request from GitHub to BitBucket

#### Setup

1. Add GitHub remote in local atlas repo

```
cd atlas
git remote add github <github-repo-clone-url>
```

#### Transfer Pull Request

1. Checkout PR-Branch

```
git fetch github
git checkout github/<pr-branch>
```

2. Push PR-Branch to Bitbucket

```
git push origin <pr-branch>
```

3. Create PR on BitBucket from <pr-branch> to master

## Mocks

### SMTP Server

See [Free SMTP Server for Testing](https://www.wpoven.com/tools/free-smtp-server-for-testing)

### Wiremock

To run atlas locally, the Wiremock image must be started: 
```docker-compose up wiremock -d```

### DB local

To run atlas locally, the DB images must be started: 

```docker-compose up -d```
