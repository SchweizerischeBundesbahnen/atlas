# API Management ATLAS

## ATLAS Links
- Openshift project atlas-dev : https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/cluster/projects/atlas-dev
- Repositories: https://code.sbb.ch/projects/KI_ATLAS
- APIM
  - https://developer-int.sbb.ch/apis/atlas-dev/information
  - https://developer-int.sbb.ch/apis/atlas-test/information
  - https://developer-int.sbb.ch/apis/atlas-int/information
  - https://developer.sbb.ch/apis/atlas/information

## HowTo Update
https://api-deploy.int.app.ose.sbb-aws.net/swagger-ui.html#/API/updateServiceUsingPUT mit folgenden Parameter
- artifactId: timetable-field-number-apim-configuration
- classifier: dev
- groupId: ch.sbb.atlas
- version 0.x.0 (siehe Jenkins Job)

## HowTo create Client
Siehe https://confluence.sbb.ch/display/~u222997/%5BATLAS%5D+APIM+Configuration+and+CloudWAF

# Dokumentation APIM CLEW

## dev, test, int

### [Admin Portal](https://3scale-admin.int.app.ose.sbb-aws.net/p/login)
(use SSO-Login):\
View Configuration in the admin panel. Lists limits, application plans, traffic and configuration in a GUI.

### [Access Management Self Service Portal](https://am-ssp-int.sbb-cloud.net/#/home)

### Result
of API Configuration publishing:
[ATLAS on developer portal](https://developer-int.sbb.ch/apis?all=&text=atlas&scopes=PUBLIC;INTERNAL;PRIVATE)

## Offizielle Dokumentation ##

**[API Management Plattform](https://confluence.sbb.ch/display/AITG/API+Management)**

### Allgemeine Informationen und Links ###
* [Jenkins](https://ci.sbb.ch/job/KD_ESTA_BLUEPRINTS/job/aitg-apim-configuration/)
* [Developer Portal](https://developer-int.sbb.ch/api/201/blueprint)
* [ESTA Pipeline Documentation](https://confluence.sbb.ch/display/ESTA/Esta+Cloud+Pipeline)


## Checkliste
- Forke das Repository in dein Projekt
- Ändere die GroupId / ArtifactId / Version im POM
- Trage dein Repository im Jenkinsfile ein
- Konfiguriere deine APIM Config in der **conf.json** Datei
    - mehr unter: [01. API Definition](https://confluence.sbb.ch/display/AITG/01.+API+Definition)  
    - in den meisten Fällen macht es Sinn den ````default_oauth_flow```` auf ````clientCredentials```` zu ändern
    - Schema unter: https://code.sbb.ch/projects/KD_APIM/repos/api-deploy/browse/src/main/resources/api-config-schema-v2.json
        - das Schema kann wie bei Esta beschrieben im IntelliJ hinterlegt werden [Esta+Cloud+Pipeline#EstaCloudPipeline-JSonSchema](https://confluence.sbb.ch/display/ESTA/Esta+Cloud+Pipeline#EstaCloudPipeline-JSonSchema)
            - URL für IntelliJ: https://code.sbb.ch/projects/KD_APIM/repos/api-deploy/browse/src/main/resources/api-config-schema-v2.json?at=refs%2Fheads%2Fmaster
- Füge deine Umgebungsvariabeln in die int / prod .properties Dateien ein
- Wenn du keine DEV-Umgebung auf der APIM-Plattform deployen willst, kannst du folgende Dateien entfernen
    - alles unter src\main\resources\api-dev
    - das assembly-dev.xml
    - im POM die execution mit der ID dev

## Dev Umgebung
Da es keine DEV Umgebung gibt, muss ebenfalls die DEV Umgebung (und alle anderen die es noch gibt) auf der Integration ausgerollt werden.
Dafür wird ein separates Zip benötigt, welches den Identifier "dev" nutzt

```
+---------------------------+
|                           |
|                           |
|    API Blueprint DEV      +--------+
|                           |        |         +------------------------------+
|                           |        |         |                              |
+---------------------------+        +--------->                              |
                                               |                              |
+---------------------------+                  |      API Management INT      |
|                           |        +--------->                              |
|                           |        |         |                              |
|    API Blueprint INT      +--------+         |                              |
|                           |                  +------------------------------+
|                           |
+---------------------------+
                                               +------------------------------+
+---------------------------+                  |                              |
|                           |                  |                              |
|                           |                  |                              |
|    API Blueprint PROD     +------------------>     API Management PROD      |
|                           |                  |                              |
|                           |                  |                              |
+---------------------------+                  |                              |
                                               +------------------------------+
````
## Deployment
Siehe: [02. Deployment](https://confluence.sbb.ch/display/AITG/02.+Deployment)
 

## Update der API Konfiguration
**Die Konfiguration kann nur updated werden und muss bereits vorgängig ausgerollt sein. Siehe: [02. Deployment](https://confluence.sbb.ch/display/AITG/02.+Deployment)**

 - erstellen eines Service-Accounts, welcher das Deployment ausführen kann. 
    - [Dokumentation](https://confluence.sbb.ch/display/AITG/22.+Update+der+API+Konfiguration#id-22.UpdatederAPIKonfiguration-1)APIClientanlegenimAccessManagementSelfServicePortal(AM-SSP))
 - im estaCloudPipeline.json unter der gewünschten Stage folgenden Block einfügen 
    -  
    ````
      "api": {
        "instance": "STAGE<int/prod>",
        "clientSecret": "CLIENT_SECRET_VOM_AM_SSP",
        "clientId": "CLIENT_NAME_VOM_AM_SSP",
        "classifier": "CLASSIFIER_ODER_LEER"
      }
    ````
 - [Beispiel](https://code.sbb.ch/projects/KD_APIM/repos/elevator-api/browse/estaCloudPipeline.json#11-23)
 
