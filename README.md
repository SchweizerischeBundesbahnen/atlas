# Atlas

This is the repository for business relevant services for ATLAS.

<!-- toc -->

- [Big Picture](#big-picture)
- [Links](#links)
- [Stages and their purpose](#stages-and-their-purpose)
- [Monitoring and Logging](#monitoring-and-logging)
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
    * [Versioning lib](#versioning-lib)
    * [Frontend](#frontend)
- [Troubleshooting](#troubleshooting)

<!-- tocstop -->

Build
Status: [![Build Status](https://ci.sbb.ch/job/KI_ATLAS/job/atlas/job/master/badge/icon)](https://ci.sbb.ch/job/KI_ATLAS/job/atlas/job/master/)

E2E
Status: [![Build Status E2E](https://ci.sbb.ch/job/KI_ATLAS_E2E/job/atlas/job/master/badge/icon)](https://ci.sbb.ch/job/KI_ATLAS_E2E/job/atlas/job/master/)

Quality
Gate: [![Quality Gate Status](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb.atlas%3Aatlas&metric=alert_status)](https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas)

## Big Picture

ATLAS shall be a platform, on which relevant data for customer information can be managed. \
Applications on this platform share their tech stack and have the same monitoring and logging.

It's applications share the following architectural goal:

![ATLAS Big Picture](documentation/ATLAS_Infrastruktur.svg)

## Links

- **Jenkins**: https://ci.sbb.ch/job/KI_ATLAS/job/atlas/job/master/
- **Jenkins-E2E**: https://ci.sbb.ch/job/KI_ATLAS_E2E/job/atlas/
- **Sonarqube**: https://codequality.sbb.ch/dashboard?id=ch.sbb.atlas%3Aatlas&branch=master
- **JFrog Artifactory**:
    - **npm**: https://bin.sbb.ch/ui/repos/tree/General/atlas.npm%2Fatlas-frontend
    - **docker**: https://bin.sbb.ch/ui/repos/tree/General/atlas.docker%2Fatlas-frontend
- **Openshift**:
    - **
      Dev**: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
    - **
      Test**: https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-test
    - **
      Int**: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-int
    - **
      Prod**: https://console-openshift-console.apps.maggie.sbb-aws.net/k8s/cluster/projects/atlas-prod
- **Deployment**:
    - **Dev**: https://atlas.dev.sbb-cloud.net
    - **Test**: https://atlas.test.sbb-cloud.net
    - **Int**: https://atlas.int.sbb-cloud.net
    - **Prod**: https://atlas.sbb-cloud.net

## Stages and their purpose

* [Stages](documentation/stages.md)

## Monitoring and Logging

* [Monitoring (Instana and Actuator)](documentation/Monitoring.md)
* [Logging to Splunk](documentation/Logging.md)

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

ATLAS has a Monorepo project structure. The CI/CD is execute on each module. For each push event on
master
a Jenkins pipiline is executed. The modules are versionied and deployed with the same version
number.

![ATLAS Monorepo](documentation/ATLAS-Mono-Repo-Migration.png)

See the original
file: [ATLAS-Monorepo drawio](https://confluence.sbb.ch/display/~e539196/ATLAS+Mono+Repo+migration)

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

### Versioning lib

Library used to perform business object versioning according to
the [documentation](https://confluence.sbb.ch/pages/viewpage.action?spaceKey=ATLAS&title=%5BATLAS%5D+8.7+Versionierung)
See [Versioning documentation](versioning/README.md);

### Amazon S3 Lib

Library used to perform REST Request to Amazon S3 according to
the [documentaion](amazon-s3/README.md#DataSourceSettings#
#LocalDataSource: bo@dev
#BEGIN#
<data-source source="LOCAL" name="bo@dev" uuid="676d707d-56a8-4c9d-b1f0-37047ac30878"><database-info
product="PostgreSQL" version="14.2" jdbc-version="4.2" driver-name="PostgreSQL JDBC Driver"
driver-version="42.3.3" dbms="POSTGRES" exact-version="14.2" exact-driver-version="42.3"><
identifier-quote-string>&quot;</identifier-quote-string></database-info><case-sensitivity
plain-identifiers="lower" quoted-identifiers="exact"/><driver-ref>postgresql<
/driver-ref><synchronize>true</synchronize><jdbc-driver>org.postgresql.Driver</jdbc-driver><
jdbc-url>jdbc:postgresql:
//business-organisation-directory-dev.cdv0rjtmldns.eu-central-1.rds.amazonaws.com:
5432/business_organisation_directory</jdbc-url><secret-storage>master_key</secret-storage><
user-name>business_organisation_directory</user-name><schema-mapping><
introspection-scope><node kind="database" qname="@"><node kind="schema" qname="@"/></node><
/introspection-scope></schema-mapping><working-dir>$ProjectFileDir$</working-dir></data-source>
#END#

#LocalDataSource: bo@int
#BEGIN#
<data-source source="LOCAL" name="bo@int" uuid="f445e980-703a-4b9d-8e59-78a92a16529a"><database-info
product="PostgreSQL" version="14.2" jdbc-version="4.2" driver-name="PostgreSQL JDBC Driver"
driver-version="42.3.3" dbms="POSTGRES" exact-version="14.2" exact-driver-version="42.3"><
identifier-quote-string>&quot;</identifier-quote-string></database-info><case-sensitivity
plain-identifiers="lower" quoted-identifiers="exact"/><driver-ref>postgresql<
/driver-ref><synchronize>true</synchronize><jdbc-driver>org.postgresql.Driver</jdbc-driver><
jdbc-url>jdbc:postgresql:
//business-organisation-directory-int.cdv0rjtmldns.eu-central-1.rds.amazonaws.com:
5432/business_organisation_directory</jdbc-url><secret-storage>master_key</secret-storage><
user-name>business_organisation_directory</user-name><schema-mapping><
introspection-scope><node kind="database" qname="@"><node kind="schema" qname="@"/></node><
/introspection-scope></schema-mapping><working-dir>$ProjectFileDir$</working-dir></data-source>
#END#

#LocalDataSource: bo@localhost
#BEGIN#
<data-source source="LOCAL" name="bo@localhost" uuid="e30bba29-7547-4416-9baa-40edfad1e184"><
database-info product="PostgreSQL" version="14.2 (Debian 14.2-1.pgdg110+1)" jdbc-version="4.2"
driver-name="PostgreSQL JDBC Driver" driver-version="42.3.3" dbms="POSTGRES" exact-version="14.2"
exact-driver-version="42.3"><identifier-quote-string>&quot;</identifier-quote-string><
/database-info><case-sensitivity plain-identifiers="lower" quoted-identifiers="exact"/><driver-ref>
postgresql</driver-ref><synchronize>true</synchronize><jdbc-driver>org.postgresql.Driver<
/jdbc-driver><jdbc-url>jdbc:postgresql://localhost:5435/business-organisation-directory</jdbc-url><
user-name>user</user-name><schema-mapping><
introspection-scope><node kind="database" qname="@"><node kind="schema" qname="@"/></node><
/introspection-scope></schema-mapping><working-dir>$ProjectFileDir$</working-dir></data-source>
#END#

#LocalDataSource: bo@prod
#BEGIN#
<data-source source="LOCAL" name="bo@prod" uuid="c6da0219-5dc2-4168-b88b-dba89ebcdff2"><
database-info product="PostgreSQL" version="14.3" jdbc-version="4.2" driver-name="PostgreSQL JDBC
Driver" driver-version="42.3.3" dbms="POSTGRES" exact-version="14.3" exact-driver-version="42.3"><
identifier-quote-string>&quot;</identifier-quote-string></database-info><case-sensitivity
plain-identifiers="lower" quoted-identifiers="exact"/><driver-ref>postgresql<
/driver-ref><synchronize>true</synchronize><jdbc-driver>org.postgresql.Driver</jdbc-driver><
jdbc-url>jdbc:postgresql:
//business-organisation-directory-prod.czdfgxj0x1zx.eu-central-1.rds.amazonaws.com:
5432/business_organisation_directory</jdbc-url><secret-storage>master_key</secret-storage><
user-name>business_organisation_directory</user-name><schema-mapping><
introspection-scope><node kind="database" qname="@"><node kind="schema" qname="@"/></node><
/introspection-scope></schema-mapping><working-dir>$ProjectFileDir$</working-dir></data-source>
#END#

#LocalDataSource: bo@test
#BEGIN#
<data-source source="LOCAL" name="bo@test" uuid="418a115e-df2a-472c-bb39-f407bd8caec0"><
database-info product="PostgreSQL" version="14.2" jdbc-version="4.2" driver-name="PostgreSQL JDBC
Driver" driver-version="42.3.3" dbms="POSTGRES" exact-version="14.2" exact-driver-version="42.3"><
identifier-quote-string>&quot;</identifier-quote-string></database-info><case-sensitivity
plain-identifiers="lower" quoted-identifiers="exact"/><driver-ref>postgresql<
/driver-ref><synchronize>true</synchronize><jdbc-driver>org.postgresql.Driver</jdbc-driver><
jdbc-url>jdbc:postgresql:
//business-organisation-directory-test.cdv0rjtmldns.eu-central-1.rds.amazonaws.com:
5432/business_organisation_directory</jdbc-url><secret-storage>master_key</secret-storage><
user-name>business_organisation_directory</user-name><schema-mapping><
introspection-scope><node negative="1"><node kind="database" qname="@"><node kind="schema" qname="@"/></node><node kind="database"><name qname="business_organisation_directory"/><name qname="postgres"/></node></node><
/introspection-scope></schema-mapping><working-dir>$ProjectFileDir$</working-dir></data-source>
#END#

#LocalDataSource: didok@didok.gsharp-dev.private.postgres.database.azure.com
#BEGIN#
<data-source source="LOCAL" name="didok@didok.gsharp-dev.private.postgres.database.azure.com" uuid="
57c3ee80-eec8-437f-8537-9b89f50ee18f"><database-info product="PostgreSQL" version="13.4"
jdbc-version="4.2" driver-name="PostgreSQL JDBC Driver" driver-version="42.3.3" dbms="POSTGRES"
exact-version="13.4" exact-driver-version="42.3"><identifier-quote-string>&quot;<
/identifier-quote-string></database-info><case-sensitivity plain-identifiers="lower"
quoted-identifiers="exact"/><driver-ref>postgresql</driver-ref><synchronize>true</synchronize><
jdbc-driver>org.postgresql.Driver</jdbc-driver><jdbc-url>jdbc:postgresql:
//didok.gsharp-dev.private.postgres.database.azure.com:5432/didok</jdbc-url><secret-storage>
master_key</secret-storage><user-name>didok</user-name><schema-mapping><
introspection-scope><node kind="database" qname="@"><node kind="schema" qname="@"/></node><
/introspection-scope></schema-mapping><working-dir>$ProjectFileDir$</working-dir></data-source>
#END#

#LocalDataSource: lidi@dev
#BEGIN#
<data-source source="LOCAL" name="lidi@dev" uuid="e5cda539-4b12-4640-ac61-eceab20e2629"><
database-info product="PostgreSQL" version="14.1" jdbc-version="4.2" driver-name="PostgreSQL JDBC
Driver" driver-version="42.3.3" dbms="POSTGRES" exact-version="14.1" exact-driver-version="42.3"><
identifier-quote-string>&quot;</identifier-quote-string></database-info><case-sensitivity
plain-identifiers="lower" quoted-identifiers="exact"/><driver-ref>postgresql<
/driver-ref><synchronize>true</synchronize><jdbc-driver>org.postgresql.Driver</jdbc-driver><
jdbc-url>jdbc:postgresql://line-directory-dev.cdv0rjtmldns.eu-central-1.rds.amazonaws.com:
5432/line_directory</jdbc-url><secret-storage>master_key</secret-storage><user-name>line_directory<
/user-name><schema-mapping><
introspection-scope><node kind="database" qname="@"><node kind="schema" qname="@"/></node><
/introspection-scope></schema-mapping><working-dir>$ProjectFileDir$</working-dir></data-source>
#END#

#LocalDataSource: lidi@int
#BEGIN#
<data-source source="LOCAL" name="lidi@int" uuid="58a2d887-d206-4f66-bc08-8d0bf6730978"><
database-info product="PostgreSQL" version="14.2" jdbc-version="4.2" driver-name="PostgreSQL JDBC
Driver" driver-version="42.3.3" dbms="POSTGRES" exact-version="14.2" exact-driver-version="42.3"><
identifier-quote-string>&quot;</identifier-quote-string></database-info><case-sensitivity
plain-identifiers="lower" quoted-identifiers="exact"/><driver-ref>postgresql<
/driver-ref><synchronize>true</synchronize><jdbc-driver>org.postgresql.Driver</jdbc-driver><
jdbc-url>jdbc:postgresql://line-directory-int.cdv0rjtmldns.eu-central-1.rds.amazonaws.com:
5432/line_directory</jdbc-url><secret-storage>master_key</secret-storage><user-name>line_directory<
/user-name><schema-mapping><
introspection-scope><node kind="database" qname="@"><node kind="schema" qname="@"/></node><
/introspection-scope></schema-mapping><working-dir>$ProjectFileDir$</working-dir></data-source>
#END#

#LocalDataSource: lidi@localhost
#BEGIN#
<data-source source="LOCAL" name="lidi@localhost" uuid="a467ba27-81ff-4600-8b55-8a25e27101db"><
database-info product="PostgreSQL" version="14.2 (Debian 14.2-1.pgdg110+1)" jdbc-version="4.2"
driver-name="PostgreSQL JDBC Driver" driver-version="42.3.3" dbms="POSTGRES" exact-version="14.2"
exact-driver-version="42.3"><identifier-quote-string>&quot;</identifier-quote-string><
/database-info><case-sensitivity plain-identifiers="lower" quoted-identifiers="exact"/><driver-ref>
postgresql</driver-ref><synchronize>true</synchronize><jdbc-driver>org.postgresql.Driver<
/jdbc-driver><jdbc-url>jdbc:postgresql://localhost:5433/line-directory</jdbc-url><secret-storage>
master_key</secret-storage><user-name>user</user-name><schema-mapping><
introspection-scope><node kind="database" qname="@"><node kind="schema" qname="@"/></node><
/introspection-scope></schema-mapping><working-dir>$ProjectFileDir$</working-dir></data-source>
#END#

#LocalDataSource: lidi@prod
#BEGIN#
<data-source source="LOCAL" name="lidi@prod" uuid="02bd7fb7-2a9c-410b-abba-5936aa327fa6"><
database-info product="PostgreSQL" version="14.1" jdbc-version="4.2" driver-name="PostgreSQL JDBC
Driver" driver-version="42.3.3" dbms="POSTGRES" exact-version="14.1" exact-driver-version="42.3"><
identifier-quote-string>&quot;</identifier-quote-string></database-info><case-sensitivity
plain-identifiers="lower" quoted-identifiers="exact"/><driver-ref>postgresql<
/driver-ref><synchronize>true</synchronize><jdbc-driver>org.postgresql.Driver</jdbc-driver><
jdbc-url>jdbc:postgresql:
//line-directory-prod.czdfgxj0x1zx.eu-central-1.rds.amazonaws.com/line_directory</jdbc-url><
secret-storage>master_key</secret-storage><user-name>line_directory</user-name><schema-mapping><
introspection-scope><node kind="database" qname="@"><node kind="schema" qname="@"/></node><
/introspection-scope></schema-mapping><working-dir>$ProjectFileDir$</working-dir></data-source>
#END#

#LocalDataSource: lidi@test
#BEGIN#
<data-source source="LOCAL" name="lidi@test" uuid="eecf76c6-9cfc-434a-861f-c84fb576f7ee"><
database-info product="PostgreSQL" version="14.1" jdbc-version="4.2" driver-name="PostgreSQL JDBC
Driver" driver-version="42.3.3" dbms="POSTGRES" exact-version="14.1" exact-driver-version="42.3"><
identifier-quote-string>&quot;</identifier-quote-string></database-info><case-sensitivity
plain-identifiers="lower" quoted-identifiers="exact"/><driver-ref>postgresql<
/driver-ref><synchronize>true</synchronize><jdbc-driver>org.postgresql.Driver</jdbc-driver><
jdbc-url>jdbc:postgresql://line-directory-test.cdv0rjtmldns.eu-central-1.rds.amazonaws.com:
5432/line_directory</jdbc-url><secret-storage>master_key</secret-storage><user-name>line_directory<
/user-name><schema-mapping><
introspection-scope><node kind="database" qname="@"><node kind="schema" qname="@"/></node><
/introspection-scope></schema-mapping><working-dir>$ProjectFileDir$</working-dir></data-source>
#END#

)

### Frontend

ATLAS Angular App. See [Frontend documentation](frontend/README.md);

## Troubleshooting

* [Sonarqube](documentation/Troubleshooting.md)
