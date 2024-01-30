# Atlas API Auth Gateway

<!-- toc -->

- [ATLAS](#atlas)
- [Versioning](#versioning)
- [Gateway](#gateway)

<!-- tocstop -->

## ATLAS

This application is part of ATLAS. General documentation is
available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/README.md).

## Versioning

This project uses [Semantic Versioning](https://semver.org/).

## Gateway

This project functions as a gateway between our Frontend and the API published on developer.sbb.ch.

This gateway will proxy requests to the ATLAS API.

- Requests with authorization will get passed without mutation.
- Unauthorized requests will be mutated to be authorized by client_credential token. 
