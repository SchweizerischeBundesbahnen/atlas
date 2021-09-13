# Timetable field number Frontend

[![Build Status](https://ci.sbb.ch/job/KI_ATLAS/job/timetable-field-number-frontend/job/master/badge/icon)](https://ci.sbb.ch/job/KI_ATLAS/job/timetable-field-number-frontend/job/master/)
[![Quality Gate Status](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb%3Atimetable-field-number-frontend&metric=alert_status)](https://codequality.sbb.ch/dashboard?id=ch.sbb%3Atimetable-field-number-frontend)
This project was generated from [esta-cloud-angular](https://code.sbb.ch/projects/KD_ESTA_BLUEPRINTS/repos/esta-cloud-angular/browse).
See [ESTA Documentation](https://confluence.sbb.ch/display/CLEW/ESTA-Web).

<!-- toc -->

- [Links](#links)
- [Development](#development)
  - [Set SBB Artifactory as npm registry](#set-sbb-artifactory-as-npm-registry)
  - [Development server](#development-server)
  - [Code scaffolding](#code-scaffolding)
  - [Running unit tests](#running-unit-tests)
  - [Linting](#linting)
  - [Prettier](#prettier)
- [Monitoring and Logging](#monitoring-and-logging)

<!-- tocstop -->

## Links

- **Jenkins**: https://ci.sbb.ch/job/KI_ATLAS/job/timetable-field-number-frontend/
- **Sonarqube**: https://codequality.sbb.ch/dashboard?id=ch.sbb%3Atimetable-field-number-frontend
- **JFrog Artifactory**:
  - **npm**: https://bin.sbb.ch/ui/repos/tree/General/atlas.npm%2Ftimetable-field-number-frontend
  - **docker**: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker%2Ftimetable-field-number-frontend
- **Openshift**:
  - **Dev**: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/ns/atlas-dev/routes
- **Deployment**:
  - **Dev**: https://timetable-field-number-frontend.apps.aws01t.sbb-aws-test.net

## Development

### Set SBB Artifactory as npm registry

See [set SBB Artifactory as npm registry](https://confluence.sbb.ch/display/CLEW/Configuration+Artifactory+7.x+as+NPM+Registry)

### Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

### Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

### Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

### Linting

This project uses [angular-eslint](https://github.com/angular-eslint/angular-eslint) for linting purposes,
which is the recommended replacement for tslint and codelyzer. Use the eslint plugin for
[VS Code](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint) or for
[IntelliJ](https://www.jetbrains.com/help/idea/eslint.html).

### Prettier

This project is configured with [prettier](https://prettier.io/), which is an opinionated code formatter.
Run it with `npm run format`. It is also configured as a pre-commit git hook, which will be applied to changed files.

## Monitoring and Logging

- [Logging to Splunk](documentation/Logging.md)
