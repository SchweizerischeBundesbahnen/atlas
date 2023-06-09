# UserAdministration Backend

<!-- toc -->

- [Project Versioning](#project-versioning)
- [GraphAPI](#graphapi)

<!-- tocstop -->

## Project Versioning

This project uses [Semantic Versioning](https://semver.org/).

## GraphAPI

This project stores roles and BusinessOrganisation responsibilities of our users.
In our database we only store SBBUIDs.


We use Microsoft Graph API to retrieve additional account information for each SBBUIDS. This way we
may retrieve the current mail address, name and account status on the fly, as they are subject to
change.

We use the ApplicationRegistration of our API to perform the requests via Graph API.
The relevant properties are:

```yaml
azure-config:
  tenant-id: 2cda5d11-f0ac-46b3-967d-af1b2e1bd01a
  azure-ad-secret: ${AZURE_AD_SECRET}
  app-registration-id: 87e6e634-6ba1-4e7a-869d-3348b4c3eafc
```

These are included and initialized with `GraphClientConfig` to provide an
injectable `GraphServiceClient` bean.
In order to perform the requests the application was permissioned the `User.Read`
and `User.Read.All` in Azure.

This can be checked out on
e.g. https://portal.azure.com/#view/Microsoft_AAD_RegisteredApps/ApplicationMenuBlade/~/CallAnAPI/appId/f3cdcb3e-1e95-4591-b664-4526d00f5d66
for our prod environment.
