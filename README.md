# Atlas

This is the main repository for business relevant services for ATLAS.

## Build Status

[![Build Status](https://ci.sbb.ch/job/KI_ATLAS/job/line-directory-backend/job/master/badge/icon)](https://ci.sbb.ch/job/KI_ATLAS/job/line-directory-backend/job/master/)
[![Quality Gate Status](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb%3Aline-directory-backend&metric=alert_status)](https://codequality.sbb.ch/dashboard?id=ch.sbb%3Aline-directory-backend)


## Run locally

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

### gateway

Module to handle routing of API endpoints to the respective business applications.
Start this locally, if you want to run the angular UI.

### line-directory

Business service for lines, sublines and timetable field numbers. All of these business objects use the atlas own versioning.

### versioning

Library used to perform business object versioning according to the [documentation](https://confluence.sbb.ch/pages/viewpage.action?spaceKey=ATLAS&title=%5BATLAS%5D+8.7+Versionierung)