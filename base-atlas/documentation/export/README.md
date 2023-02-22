# Atlas Export CSV

<!-- toc -->

- [How to export CSV to Amazon S3](#how-to-export-csv-to-amazon-s3)
  * [Add base-service dependency](#add-base-service-dependency)
  * [Configure Amazon Client](#configure-amazon-client)
    + [Add Amazon Client Properties](#add-amazon-client-properties)
    + [Configure Amazon Client Secrets Chart](#configure-amazon-client-secrets-chart)
    + [Add the Secrets to Open Shift](#add-the-secrets-to-open-shift)
    + [Configure Client](#configure-client)
      - [Configure beans](#configure-beans)
  * [Export Data](#export-data)

<!-- tocstop -->

The **Export Module** is used to convert Data into CSV files and put them into
the [SBB Amazon S3](../amazon/README.md).

## How to export CSV to Amazon S3

### Add base-service dependency

In order to be able to export CSV fiels to [SBB Amazon S3](../amazon/README.md) you have to add this
library in your application pom:

~~~xml

<dependency>
   <artifactId>base-service</artifactId>
   <groupId>ch.sbb.atlas</groupId>
   <version>${revision}</version>
</dependency>
~~~

### Configure Amazon Client

#### Add Amazon Client Properties

~~~
amazon:
  accessKey: ${AMAZON_S3_ACCESS_KEY}
  secretKey: ${AMAZON_S3_SECRET_KEY}
  region: "eu-central-1"
  bucketName: "atlas-data-export-dev-dev"
  objectExpirationDays: 30
~~~

#### Configure Amazon Client Secrets Chart

You have to define in the **Chart template** the following properties:

~~~
- name: AMAZON_S3_ACCESS_KEY
  valueFrom:
    secretKeyRef:
        name: amazon-client-{{ .Values.YOUR-APPLICATION.name }}
        key: amazon-access-key
- name: AMAZON_S3_SECRET_KEY
  valueFrom:
    secretKeyRef:
        name: amazon-client-{{ .Values.YOUR-APPLICATION.name }}
        key: amazon-secret-key
~~~

#### Add the Secrets to Open Shift

Remember to store the secrets to our Open Shift for every envoronmentes (e.g.
see [amazon-client-line-directory]https://console-openshift-console.apps.aws01t.sbb-aws-test.net/k8s/ns/atlas-dev/secrets/amazon-client-line-directory)

#### Configure Client

See [AmazonConfig.java](../../../line-directory/src/main/java/ch/sbb/line/directory/configuration/AmazonConfig.java)

##### Configure beans

Configure [FileService.java](../../src/main/java/ch/sbb/atlas/amazon/service/FileService.java)
bean:

~~~java
@Bean
public FileService fileService(){
    return new FileService();
    }  
~~~

### Export Data

1. Extend a CSV model class
   from [VersionCsvModel.java](../../src/main/java/ch/sbb/atlas/export/model/VersionCsvModel.java)
2. Extend the
   class [BaseExportService.java](../../src/main/java/ch/sbb/atlas/export/BaseExportService.java)