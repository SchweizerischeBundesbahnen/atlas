# Atlas versioning library

[![Build Status](https://ci.sbb.ch/job/KI_ATLAS/job/atlas-versioning/job/master/badge/icon)](https://ci.sbb.ch/job/KI_ATLAS/job/atlas-versioning/job/master/)
[![Quality Gate Status](https://codequality.sbb.ch/api/project_badges/measure?project=ch.sbb%3Aatlas-versioning&metric=alert_status)](https://codequality.sbb.ch/dashboard?id=ch.sbb%3Aatlas-versioning)

<!-- toc -->

- [ATLAS](#atlas)
- [Atlas Versioning Library](#atlas-versioning-library)
    * [How to](#how-to)
    * [Add atlas-library to the pom.xml](#add-atlas-library-to-the-pomxml)
    * [Integrate atlas-library](#integrate-atlas-library)
    * [Define VersionableService as Bean for SpringBoot](#define-versionableservice-as-bean-for-springboot)
    * [How to use VersionableService](#how-to-use-versionableservice)

<!-- tocstop -->

## ATLAS

This library is part of ATLAS. General documentation is
available [here](https://code.sbb.ch/projects/KI_ATLAS/repos/atlas-backend/browse/README.md#big-picture)
.

## Atlas Versioning Library

This library tries to generify the versioning of certain entity for ATLAS projects. The versioning
is based on the following
document: [Versionirung](https://confluence.sbb.ch/pages/viewpage.action?spaceKey=ATLAS&title=%5BATLAS%5D+8.7+Versionierung)
.

:warning: **Please note that this library implements only the scenarios
in [Versionirung](https://confluence.sbb.ch/pages/viewpage.action?spaceKey=ATLAS&title=%5BATLAS%5D+8.7+Versionierung)
defined!** :warning:

### How to

### Add atlas-library to the pom.xml

Add in the **pom.xml**:

```
<dependencies>
....
  <dependency>
    <groupId>ch.sbb</groupId>
	<artifactId>atlas-versioning</artifactId>
	<version>0.8.0</version>
  </dependency>
<dependencies>
```

### Integrate atlas-library

1. Implement the
   interface [Versionable](src/main/java/ch/sbb/atlas/versioning/model/Versionable.java):
2. Annotate the entity
   with [@AtlasVersionable](src/main/java/ch/sbb/atlas/versioning/annotation/AtlasVersionable.java)
3. Annotate the properties to be versioned
   with [@AtlasVersionableProperty](src/main/java/ch/sbb/atlas/versioning/annotation/AtlasVersionableProperty.java)

E.g:

```java

@AtlasVersionable
public class VersionableObject implements Versionable {

  private LocalDate validFrom;
  private LocalDate validTo;
  private Long id;

  private String name;

  @AtlasVersionableProperty
  private String property;

  @AtlasVersionableProperty(relationType = RelationType.ONE_TO_MANY, relationsFields = {"value"})
  private List<Relation> oneToManyRelation;

  public class Relation {

    private Long id;
    private String value;
  }

}
```

### Define VersionableService as Bean for SpringBoot

```java
@Bean
public VersionableService versionableService(){
    return new VersionableServiceImpl();
    }
```

### How to use VersionableService

1. Get all versions of the given object

````java
List<Version> currentVersions=versionRepository.getAllVersionsVersioned(currentVersion.getTtfnid());
````

2. Call the
   method ````versioningObjects(Versionable current, Versionable edited, List<T> currentVersions);````
    1. **current** is the entity already persist on the db
    2. **edited** is the current entity edited (the difference)
    3. **currentVersion** are the versions of the current entity
3. The result is a list
   of [VersionedObject](src/main/java/ch/sbb/atlas/versioning/model/VersionedObject.java) which
   defines for each VersionedObject whether it should be updated, deleted or created.

E.g.

````java
public List<VersionedObject> updateVersion(Version currentVersion, Version editedVersion) {
    List<Version> currentVersions = versionRepository.getAllVersionsVersioned(
    currentVersion.getTtfnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
    editedVersion, currentVersions);

    versionableService.applyVersioning(Version.class, versionedObjects, this::save,
    this::deleteById);
    return versionedObjects;
}
````
