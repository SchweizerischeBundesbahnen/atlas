import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisation,
  BusinessOrganisationsService,
  UserPermissionCreateModel,
  UserPermissionModel,
} from '../../api';
import { BehaviorSubject, firstValueFrom } from 'rxjs';

export class UserPermissionManager {
  constructor(private readonly boService: BusinessOrganisationsService) {}

  readonly userPermission: UserPermissionCreateModel = {
    sbbUserId: '',
    permissions: [
      {
        application: 'TTFN',
        role: 'READER',
        sboids: [],
      },
      {
        application: 'LIDI',
        role: 'READER',
        sboids: [],
      },
      {
        application: 'BODI',
        role: 'READER',
        sboids: [],
      },
    ],
  };

  private readonly availableApplicationRolesConfig: {
    [application in ApplicationType]: ApplicationRole[];
  } = {
    TTFN: Object.values(ApplicationRole),
    LIDI: Object.values(ApplicationRole),
    BODI: [ApplicationRole.Reader, ApplicationRole.SuperUser, ApplicationRole.Supervisor],
  };

  readonly businessOrganisationsOfApplication: {
    [application in ApplicationType]: BusinessOrganisation[];
  } = {
    TTFN: [],
    LIDI: [],
    BODI: [],
  };

  readonly boOfApplicationsSubject$: BehaviorSubject<{
    [application in ApplicationType]: BusinessOrganisation[];
  }> = new BehaviorSubject<{ [application in ApplicationType]: BusinessOrganisation[] }>(
    this.businessOrganisationsOfApplication
  );

  getAvailableApplicationRolesOfApplication(application: ApplicationType): ApplicationRole[] {
    return this.availableApplicationRolesConfig[application];
  }

  clearSboidsIfNotWriter(): void {
    this.userPermission.permissions.forEach((permission) => {
      if (permission.role !== 'WRITER') {
        permission.sboids = [];
      }
    });
  }

  getUserPermission(): UserPermissionCreateModel {
    return this.userPermission;
  }

  getSbbUserId(): string {
    return this.userPermission.sbbUserId;
  }

  getCurrentRole(application: ApplicationType): ApplicationRole {
    const permissionIndex = this.getPermissionIndexFromApplication(application);
    return this.userPermission.permissions[permissionIndex].role;
  }

  setSbbUserId(userId: string): void {
    this.userPermission.sbbUserId = userId;
  }

  setPermissions(permissions: UserPermissionModel[]): void {
    console.log('test');
    permissions.forEach((permission) => {
      const application = permission.application;
      const permissionIndex = this.getPermissionIndexFromApplication(application);
      this.userPermission.permissions[permissionIndex].role = permission.role;
      this.userPermission.permissions[permissionIndex].sboids = [];
      this.businessOrganisationsOfApplication[application] = [];
      permission.sboids.forEach((sboid) => {
        this.addSboidToPermission(application, sboid);
      });
    });
  }

  changePermissionRole(application: ApplicationType, newRole: ApplicationRole): void {
    const permissionIndex = this.getPermissionIndexFromApplication(application);
    this.userPermission.permissions[permissionIndex].role = newRole;
  }

  removeSboidFromPermission(application: ApplicationType, sboidIndex: number): void {
    const permissionIndex = this.getPermissionIndexFromApplication(application);
    const sboidToDelete = this.businessOrganisationsOfApplication[application].filter(
      (_, index) => index === sboidIndex
    )[0].sboid;
    this.userPermission.permissions[permissionIndex].sboids = this.userPermission.permissions[
      permissionIndex
    ].sboids.filter((sboid) => sboid !== sboidToDelete);
    this.businessOrganisationsOfApplication[application] = this.businessOrganisationsOfApplication[
      application
    ].filter((_, index) => index !== sboidIndex);
    this.boOfApplicationsSubject$.next(this.businessOrganisationsOfApplication);
  }

  addSboidToPermission(application: ApplicationType, sboid: string): void {
    const permissionIndex = this.getPermissionIndexFromApplication(application);

    firstValueFrom(
      this.boService.getAllBusinessOrganisations([sboid], undefined, undefined, 0, 1, ['sboid,ASC'])
    ).then((result) => {
      if (!result.objects || result.objects.length === 0) {
        console.error('Could not resolve selected bo');
        return;
      }
      if (this.userPermission.permissions[permissionIndex].sboids.includes(sboid)) {
        console.error('Already added');
        return;
      }
      this.userPermission.permissions[permissionIndex].sboids.push(sboid);
      this.businessOrganisationsOfApplication[application] = [
        ...this.businessOrganisationsOfApplication[application],
        result.objects[0],
      ];
      this.boOfApplicationsSubject$.next(this.businessOrganisationsOfApplication);
    });
  }

  private getPermissionIndexFromApplication(application: ApplicationType): number {
    return this.userPermission.permissions.findIndex(
      (permission) => permission.application === application
    );
  }
}
