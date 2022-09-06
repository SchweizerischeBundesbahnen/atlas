# Implementation of UserAdministration for business services

Based on Specification: https://confluence.sbb.ch/pages/viewpage.action?pageId=2118124295

![Role Hierarchy](documentation/user-administration.png)

As illustrated in the diagram, every role has the priviledges of the role below.

Everyone may:
- Read business objects on the atlas platform.

The writer additionally may:
- Create and update business objects, belonging to a business organisation he manages

The super user additionally may:
- Create and update business objects, belonging to any business organisation

The supervisor additionally may:
- Interfere with workflow steps

The admin additionally may:
- See and manage users for the atlas platform