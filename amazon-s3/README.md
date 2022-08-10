# Atlas Amazon S3 Client Library

<!-- toc -->

- [Links](#links)
- [Atlas Amazon S3 Client](#atlas-amazon-s3-client)
- [Atlas Users onboarding](#atlas-users-onboarding)

<!-- tocstop -->

## Links

* [How To create AWS S3 bucket](https://confluence.sbb.ch/display/NOVA/How+To+create+AWS+S3+bucket)
* [Amazon S3 CLEW Documentation](https://confluence.sbb.ch/display/CLEW/Amazon+S3)
* [Amazon S3 Storage Slef Service Portal](https://confluence.sbb.ch/display/SVS/Amazon+S3+Storage)

## Atlas Amazon S3 Client

To be able to put or get a file on SBB Amazon S3 Bucket we need to wxecute the following steps:

1. [Create S3 Bucket](https://self.sbb-cloud.net/tools/aws/s3/new)
2. [Create S3 Bucket User](https://self.sbb-cloud.net/tools/aws/s3/user/new):
    * we can create **read-only** or **read-write** users. A correct user creation will generate the
      credentials. With these credentials we are able to access the Bucket and its content.

## Atlas Users onboarding

See [Amazon S3 Bucket Users Onboarding](documentaion/AMAZON_S3_BUCKET_USERS_ONBOARDING.md)

## Development

To browse on a Bucket
install [AWS Toolkit IntelliJ Plugin](https://blog.jetbrains.com/idea/2022/02/aws-in-intellij-idea/)

