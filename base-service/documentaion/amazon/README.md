# Atlas Amazon S3 Client Library

<!-- toc -->

- [Links](#links)
- [Atlas Amazon S3 Client](README.md#atlas-amazon-s3-client)
- [Atlas Users onboarding](#atlas-users-onboarding)
- [Development](#development)
    * [S3 Bucket](#s3-bucket)
        + [Naming convention](#naming-convention)
            - [Buckets name](#buckets-name)
            - [Buckets directory](#buckets-directory)
    * [Intellij Plugin](#intellij-plugin)

<!-- tocstop -->

## Links

* [How To create AWS S3 bucket](https://confluence.sbb.ch/display/NOVA/How+To+create+AWS+S3+bucket)
* [Amazon S3 CLEW Documentation](https://confluence.sbb.ch/display/CLEW/Amazon+S3)
* [Amazon S3 Storage Slef Service Portal](https://confluence.sbb.ch/display/SVS/Amazon+S3+Storage)

## Atlas Amazon S3 Client

To be able to put or get a file on SBB Amazon S3 Bucket we need to execute the following steps:

1. [Create S3 Bucket](https://self.sbb-cloud.net/tools/aws/s3/new)
2. [Create S3 Bucket User](https://self.sbb-cloud.net/tools/aws/s3/user/new):
    * we can create **read-only** or **read-write** users. A correct user creation will generate the
      credentials. With these credentials we are able to access the Bucket and its content.

## Atlas Users onboarding

See [Amazon S3 Bucket Users Onboarding](Amazon_S3_Buckets_Users_Onboarding.adoc)

## Development

Already created Amazon S3 credentials are stored
in: [Restricted passwords](https://confluence.sbb.ch/pages/viewpage.action?pageId=1881802050#RestrictedAccess:Passw%C3%B6rter,Hashcodes-DataBroker:Atlasexport/importfilesstorage)

### S3 Bucket

#### Naming convention

##### Buckets name

Atlas Buckets name definition (
see [Atlas S3 Buckets](https://self.sbb-cloud.net/tools/aws/s3/list)):

* prefix: **atlas-data-export-**
* env postfix:
    * dev: **dev-dev**
    * test: **test-dev**
    * int: **int-dev**
    * prod: **prod**

:warning: Disclaimer: the postfix for dev, test and int is always following by dev because of
the [SBB bucket name generation](https://self.sbb-cloud.net/tools/aws/s3/new), for more information
see [AmazonServiceImpl.java](src/main/java/ch/sbb/atlas/amazon/service/AmazonServiceImpl.java)
#getBucketNameFromActiveProfile()

##### Buckets directory

For each service we define a directory in our bucket. E.g. for the LineDirectory Service:

```
atlas-data-export-prod
│
└───line
│   │   full_line_versions_2022-08-16.csv
│   │   file012.txt
```

Each Service must define the property [**
amazon.bucket.dir**](src/main/java/ch/sbb/atlas/amazon/service/AmazonServiceImpl.java)#bucketDir in
its properties file.

### Intellij Plugin

To browse on a Bucket
install [AWS Toolkit IntelliJ Plugin](https://blog.jetbrains.com/idea/2022/02/aws-in-intellij-idea/)
