# ATLAS Frontend

This project was generated
from [esta-cloud-angular](https://code.sbb.ch/projects/KD_ESTA_BLUEPRINTS/repos/esta-cloud-angular/browse).
See [ESTA Documentation](https://confluence.sbb.ch/display/CLEW/ESTA-Web).

<!-- toc -->

- [Development](#development)
  * [Node.js](#nodejs)
  * [Angular update](#angular-update)
  * [Patch all used packages](#patch-all-used-packages)
  * [Cypress E2E](#cypress-e2e)
    + [Run cypress test locally](#run-cypress-test-locally)
      - [Release-Tests (Used by Tester)](#release-tests-used-by-tester)
      - [Cypress Tests results for troubleshooting](#cypress-tests-results-for-troubleshooting)
  * [Set SBB Artifactory as npm registry](#set-sbb-artifactory-as-npm-registry)
  * [Azure AD App Registration](#azure-ad-app-registration)
  * [Test Service Worker:](#test-service-worker)
- [Monitoring and Logging](#monitoring-and-logging)

<!-- tocstop -->

## Development

### Node.js

This project requires Node.js Version **16.13.2**. It has to be a LTS version.

Use Node Version Manager to easily switch the NodeJS version between your angular projects.

- Install NVM on Windows: https://github.com/coreybutler/nvm-windows/
- Install NVM on macOS: `brew install nvm` => https://tecadmin.net/install-nvm-macos-with-homebrew/

You should have a NVM_DIR environment variable, which points to a directory (like `./nvm`) with the current selected node version.

To install new Node.js version and set it as active using nvm, type this in the console:

```bash
 nvm install 16.13.2
```

Now you should be ready to install all required angular packages for this project. Just type:

```bash
 npm install
```

### Angular update

To update the angular packages:

```bash
 ng update
```

For more information see:

* [ng update](https://docs.angular.lat/cli/update)
* [Angular Update Guide](https://update.angular.io/)

### Patch all used packages

```bash
npm install -g npm-check-updates
ncu -u
npm install
```

### Cypress E2E

#### Run cypress test locally

1. Create the file `cypress.env.json`
2. Paste the json from the [credentials site](https://confluence.sbb.ch/pages/viewpage.action?pageId=1881802050). This is excluded
   from git, so the credentials are not commited.
3. run cypress:
  1. with the console for debugging: `npm run cypress:open` or `cypress open`
  2. as headless test: `npm run cypress:run` or `cypress run`

##### Release-Tests (Used by Tester)

E2E Release-Tests are not automatically executed within the standard pipeline.
They are located in the cypress/release folder.
To run the Release-Tests separately, you can choose the "release" option in the E2ETests-choice on this page
[ATLAS_Cypress_E2E/atlas-frontend/master/build](https://ci.sbb.ch/job/KI_ATLAS_E2E/job/atlas-frontend/job/master/build/).

To start the Release-Tests locally:

- Headless (Only console): run `npm run cypress:run-e2e-release`
- With Browser-View: run `npm run cypress:open-e2e-release`

##### Cypress Tests results for troubleshooting

After each job execution a cypress video is captured and stored as **Build Artifacts**.

In case of a failure under **Build Artifacts** are stored 2 directories, one with the logs and the second with the screenshots.

### Set SBB Artifactory as npm registry

See [set SBB Artifactory as npm registry](https://confluence.sbb.ch/display/CLEW/Configuration+Artifactory+7.x+as+NPM+Registry)

### Azure AD App Registration

So you want to use AzureAD to login your users?

1. Create
   azure-app-registration.yml ([Dokumentation](https://confluence.sbb.ch/display/IAM/Azure+AD+API%3A+Self-Service+API+for+App+Registrations+with+Azure+AD#AzureADAPI:SelfServiceAPIforAppRegistrationswithAzureAD-1.1.Createapp-registrationsusingthefile-basedAPIendpoint))
2. Use the [REST-API](https://azure-ad.api.sbb.ch/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#) to POST/create
   your application
3. The configured owner may edit it with the same REST-API using PUT

Finding an application within the registry is best performed by using the GET /v1/applications and look for a name.

### Test Service Worker:

1. Build application `npm run build`
2. Install http-server `npm install --global http-server`
3. Run the Server via `http-server -p 8080 -c-1 dist/atlas-frontend`
4. Open up Atlas and close the tab again
5. Make some local changes
6. Build and run the server again `npm run build && http-server -p 8080 -c-1 dist/atlas-frontend`
7. You should get the Service Worker Popup now :)

## Monitoring and Logging

- [Logging to Splunk](documentation/Logging.md)
