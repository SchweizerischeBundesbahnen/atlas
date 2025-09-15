# ATLAS Release Tests

*This TOC is generated using https://www.npmjs.com/package/markdown-toc*

<!-- toc -->

- [Execution frequency](#execution-frequency)
- [API-Tests](#api-tests)
    * [Export API-Tests](#export-api-tests)
        + [API-version-1 Export API-Tests](#api-version-1-export-api-tests)
        + [API-version-2 Export API-Tests](#api-version-2-export-api-tests)
    * [Line-directory API-Tests](#line-directory-api-tests)
    * [Person-with-reduced-mobility API-Tests](#person-with-reduced-mobility-api-tests)
    * [Service-Point-Directory API-Tests](#service-point-directory-api-tests)
    * [Stop-Point-Workflow API-Tests](#stop-point-workflow-api-tests)
    * [Time-Table-Hearing API-Tests](#time-table-hearing-api-tests)
    * [Unauthorized API-Tests](#unauthorized-api-tests)
- [Release e2e tests](#release-e2e-tests)

<!-- tocstop -->

## Execution frequency

All the release tests (incl. in this directory) are currently run twice a day:

1. at 2am
2. at 2pm

This can be changed in the [atlas-e2e-test repository](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas-e2e-test/browse/estaTektonPipeline.json#12).

## API-Tests

The biggest part of the release tests are the REST-API tests. They are very stable, fast and don't use a GUI. Most of these tests are Create-Read-Update, so happy-path tests. They are located in the api-folder.

### Export API-Tests

The export-tests need to be connected with the TEST-stage/-environment, to be able to succeed, because they rely on normal levels of exported objects (similar to the PROD stage), hence when run locally the cypress.env.json needs to be adjusted accordingly.

So far there are 2 versions of export-JSON-APIs that atlas provides. All these export-tests have a similar structure:

* Start a downloadGZip()-call using
    * Export file name (and type of the object to be downloaded)
    * ExportType (can be version 2)
    * minimalObjectCount (this is the minimal expected amount of objects that the export-API-endpoint should return, otherwise it fails)
    * URL (this is the middle section of the API which contains its version and is always the latest endpoint so that at each time data is returned also when the last export didn't succeed, because than the pervious is used)

This downloadGZip()-call uses 7za to extract the *.gz-files temporarily and count the objects inside.

#### API-version-1 Export API-Tests

The PRM and SePoDi export tests are in this section, but there are also v2-export-tests for both.

#### API-version-2 Export API-Tests

In addition to the PRM and SePoDi export tests there are also some for BoDi and LiDi.

Hint: The transportCompany export-test only consist of 1 downloadGZip()-call, because atlas only exports the FULL version, so all data (no ACTUAL or FUTURE versions).

### Line-directory API-Tests

These are standard CRU(D)-tests. To create a subline a line is needed.

### Person-with-reduced-mobility API-Tests

PRM comes in 2 flavors: complete and reduced. Most of the PRM-stop-points are reduced, but the train stations use the complete settings. For both (complete and reduced), the api-tests do one create-update-read cycle per object (incl. platform, toilet etc.).

### Service-Point-Directory API-Tests

The SePo-tests look at the behavior of skipping the SePo-name-workflow, the status-changes of a service-point and the different types a SePo can have. The status is quite complex. Further documentation can be found on the page [SePo-Status](https://confluence.sbb.ch/x/pS4ynw)

### Stop-Point-Workflow API-Tests

The stop-point workflow can be tested in 4 different ways automatically:

1. Creating the workflow and then the FOT (BAV) rejects it
2. Creating the workflow, restarting it and then cancelling it.
3. Creating the workflow, restarting it and then the FOT (BAV) forces it to be approved
4. Creating the workflow, restarting it and then the FOT (BAV) forces it to be rejected

### Time-Table-Hearing API-Tests

The TTH-test first creates the environment for a TTH-statement to be created:

1. business organisation
2. potentially closing old timetable years
3. creating a dependent TTFN
4. creating and starting a new timetable year

and then a statement (with 10 emails - which was a new feature) is created.

### Unauthorized API-Tests

When a user is not logged in, it is important that certain sensitive information are not shown to him. This information is separated with 5 stars and are the following:

1. business-organisation.contactEnterpriseEmail
2. user.displayName
3. user-administration.user.displayName
4. current-user.sbbUserId
5. current-user.userId

## Release e2e tests

There are some release tests which interact with the frontend and hence are end-to-end tests. These basic tests are located in:

* lidi/lines
* ttfn
* directly in this directory named release

They focus on the table settings (search and filter on the overview is stored when coming back from the details view), versioning scenarios and navigation in the frontend.

:warning: These tests don't clean up there created objects, because on tekton before each execution these objects are deleted with a DB-job, hence when a test is executed more than once the line, ttfn or so needs to be deleted manually (in the frontend) :warning:
