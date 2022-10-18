import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisation,
  BusinessOrganisationsService,
  UserPermissionCreateModel,
  UserPermissionVersionModel,
} from '../../../api';
import { BehaviorSubject, firstValueFrom, Subject } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable()
export class UserPermissionManager {
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

  private readonly availableApplicationRolesConfig: {
    [application in ApplicationType]: ApplicationRole[];
  } = {
    TTFN: Object.values(ApplicationRole),
    LIDI: Object.values(ApplicationRole),
    BODI: [ApplicationRole.Reader, ApplicationRole.SuperUser, ApplicationRole.Supervisor],
  };

  constructor(private readonly boService: BusinessOrganisationsService) {}

  private boFormResetEventSource = new Subject<void>();
  readonly boFormResetEvent$ = this.boFormResetEventSource.asObservable();

  emitBoFormResetEvent(): void {
    this.boFormResetEventSource.next();
  }

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

  setPermissions(permissions: UserPermissionVersionModel[]): void {
    permissions.forEach((permission) => {
      const application = permission.application;
      const permissionIndex = this.getPermissionIndexFromApplication(application);
      this.userPermission.permissions[permissionIndex].role = permission.role;
      this.userPermission.permissions[permissionIndex].sboids = [];
      this.businessOrganisationsOfApplication[application] = [];
      this.boOfApplicationsSubject$.next(this.businessOrganisationsOfApplication);
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
    const sboidToDelete = this.businessOrganisationsOfApplication[application][sboidIndex].sboid;
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
      this.boService.getAllBusinessOrganisations([sboid], undefined, undefined, undefined, 0, 1, [
        'sboid,ASC',
      ])
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
