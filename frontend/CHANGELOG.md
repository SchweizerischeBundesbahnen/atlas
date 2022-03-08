# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

## (2021-06-11)

- Added command to Dockerfile to use the "location /" rule in nginx configuration.

## (2021-06-03)

- Updated sbb-angular library to version 12.1.0

## (2021-05-25)

- Updated to Angular 12
- Recreated the blueprint according to the new strict mode from Angular CLI which is the new default with Angular 12.
- Changed authentication from sso.sbb.ch to Azure AD.
- Added Helm templates for deployment configuration.
- Updated eslint rules and added recommended rulesets.
- Switched usage from xlf version 1.2 to version 2.0 and moved files to src/locales.
- Added fix for single language nginx configuration.

See README.md for details.

## (2021-04-26)

- rename token claim from sbbuid_ad to sbbuid

## (2021-03-23)

- update to version 11.1.3

## (2021-03-02)

- update to version 11.1.0

## (2021-02-17)

### Features

- update to 1.16.1 nginx image
- update to version 11.2 of Angular

## (2020-12-10)

### Features

- change coverage from karma-coverage-istanbul-reporter (deprecated) to karma-coverage

## (2020-12-04)

### Features

- update to version 11, switch to angular-eslint and integrate prettier

## (2020-10-26)

### Features

- update @angular, angular-server-side-configuration and angular-t9n

## (2020-10-22)

### Features

- update @angular and @sbb-esta packages

## (2020-09-23)

### Features

- update @angular and @sbb-esta packages

## (2020-09-17)

### Features

- update @angular and @sbb-esta packages and configure Ivy i18n with $localize

## (2020-09-11)

### Features

- update @angular and @sbb-esta packages and authentication configuration

## (2020-08-11)

### Features

- import BrowserAnimationsModule in AppModule because it's needed by sbb-angular library

### Bug Fixes

- Return to current url after login (including query params)

## (2020-08-05)

### Features

- update to sbb angular 10.1.2

## (2020-07-15)

### Features

- update to sbb angular 10.1.0
- add angular-t9n for translation ([56de059](http://code.sbb.ch:7999/kd_esta_blueprints/esta-cloud-angular/commit/56de05915ce756c08e180cf1bbf6c872a0d1055f))

## (2020-07-03)

### Features

- update to angular 10

## (2020-05-13)

### Features

- use angular-business instead of angular-public library

## Previously

### Features

- add proxy info to browserstack protractor configuration ([d398690](http://code.sbb.ch:7999/kd_esta_blueprints/esta-cloud-angular/commit/d3986907d9d20b80a5befea5b5acd2f2fcc4f44d))
- add testing configuration for sonar ([afe373c](http://code.sbb.ch:7999/kd_esta_blueprints/esta-cloud-angular/commit/afe373cb14853363e87d0eb219891e96b85d1dda))
- provide ng serve configuration for IE11 ([596f5f2](http://code.sbb.ch:7999/kd_esta_blueprints/esta-cloud-angular/commit/596f5f2a6b105916c83c048f2b9597af7fce9231))
- refactor to use angular-oauth2-oidc ([c16c60c](http://code.sbb.ch:7999/kd_esta_blueprints/esta-cloud-angular/commit/c16c60cefed0d509948d52df644ed9492be8771d))
