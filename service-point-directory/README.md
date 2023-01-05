# Service Point Directory

## Legacy Documentation: DB Schema - Didok

- Model: https://confluence.sbb.ch/display/ADIDOK/Datenbank
- Migration plan: https://sbb.sharepoint.com/:x:/s/didok-atlas/ERrMJki5bFtMqGjShTeKSOQBkUqI2hq4cPixMOXZHqUucg?e=etg8dr
- ServicePoint Category Tree: https://confluence.sbb.ch/display/ADIDOK/Big+Picture#BigPicture-Kategorienbaum


## Service Point Directory in ATLAS

The main goal is to serve the following business objects:

- Service Points (Dienststellen)
- Traffic Point Elements (Verkehrspunktelemente)
- Loading Points (Ladestellen)

Each of these business objects has its own versioning.

`TrafficPoints` and `LoadingPoints` always belong to a `ServicePoint`, referencing it via `servicePointNumber`.

These objects are stored and represented in our [Database Schema](src/docs/db-schema.md)

A glossary of used abbreviations may be found here: https://confluence.sbb.ch/display/ATLAS/%5BATLAS%5D+Glossar