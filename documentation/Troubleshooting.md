# Sonarqube

<!-- toc -->

- [Sonar Exclusions on Esta Cloud Pipeline for Frontend Project](#sonar-exclusions-on-esta-cloud-pipeline-for-frontend-project)
- [Run sonarqube from your local host](#run-sonarqube-from-your-local-host)

<!-- tocstop -->

## Sonar Exclusions on Esta Cloud Pipeline for Frontend Project

For some reason the ```<sonar.exclusions>``` property is not working correctly. In our case the
directory ``` **/src/app/api/**``` is not excluded even if it is declared, see [frontend/pom.xml](../frontend/pom.xml).

The workaround is to pass in the [estaCloudPipeline.json](../old_estaCloudPipeline.json)
as ```"additionalBuildParamsForSonarScan"``` the files and directories to be excluded:

~~~json
  "additionalBuildParamsForSonarScan": "-DskipTests=true -Dskip.npm.exec.ci=true -Dskip.npm.exec.build=true -Dskip.npm.exec.publish=true -DskipITs -Dsonar.exclusions=**/node_modules/**,**/src/app/api/**,**/*.spec.ts,**/*.module.ts,**/*.routes.ts,**/karma.conf.js",
~~~

## Run sonarqube from your local host

~~~shell
mvn sonar:sonar -Dsonar.host.url=https://codequality.sbb.ch  -Dsonar.login={your-personal-token}
~~~