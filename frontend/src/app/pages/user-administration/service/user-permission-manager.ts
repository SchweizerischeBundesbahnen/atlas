import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisation,
  BusinessOrganisationsService,
  UserPermission,
  UserPermissionCreate,
} from '../../../api';
import { BehaviorSubject, firstValueFrom, Subject } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable()
export class UserPermissionManager {
  readonly userPermission: UserPermissionCreate = {
    sbbUserId: '',
    permissions: [
      {
        application: 'TTFN',
        role: 'READER',
        sboids: [],
        swissCantons: [],
      },
      {
        application: 'LIDI',
        role: 'READER',
        sboids: [],
        swissCantons: [],
      },
      {
        application: 'BODI',
        role: 'READER',
        sboids: [],
        swissCantons: [],
      },
      {
        application: 'TIMETABLE_HEARING',
        role: 'READER',
        sboids: [],
        swissCantons: [],
      },
    ],
  };

  readonly businessOrganisationsOfApplication: {
    [application in ApplicationType]: BusinessOrganisation[];
  } = {
    TTFN: [],
    LIDI: [],
    BODI: [],
    TIMETABLE_HEARING: [],
  };

  readonly boOfApplicationsSubject$: BehaviorSubject<{
    [application in ApplicationType]: BusinessOrganisation[];
  }> = new BehaviorSubject<{ [application in ApplicationType]: BusinessOrganisation[] }>(
    this.businessOrganisationsOfApplication
  );

  private readonly availableApplicationRolesConfig: {
    [application in ApplicationType]: ApplicationRole[];
  } = {
    TTFN: [
      ApplicationRole.Reader,
      ApplicationRole.Writer,
      ApplicationRole.SuperUser,
      ApplicationRole.Supervisor,
    ],
    LIDI: [
      ApplicationRole.Reader,
      ApplicationRole.Writer,
      ApplicationRole.SuperUser,
      ApplicationRole.Supervisor,
    ],
    BODI: [ApplicationRole.Reader, ApplicationRole.SuperUser, ApplicationRole.Supervisor],
    TIMETABLE_HEARING: [
      ApplicationRole.Reader,
      ApplicationRole.ExplicitReader,
      ApplicationRole.Writer,
      ApplicationRole.Supervisor,
    ],
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

  clearSboidsAndCantonsIfNotWriter(): void {
    this.userPermission.permissions.forEach((permission) => {
      if (permission.role !== 'WRITER') {
        permission.sboids = [];
        permission.swissCantons = [];
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

  setPermissions(permissions: UserPermission[]): void {
    permissions.forEach((permission) => {
      const application = permission.application;
      const permissionIndex = this.getPermissionIndexFromApplication(application);
      this.userPermission.permissions[permissionIndex].role = permission.role;
      this.userPermission.permissions[permissionIndex].sboids = [];
      this.userPermission.permissions[permissionIndex].swissCantons = permission.swissCantons;
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

  getPermissionByApplication(application: ApplicationType) {
    const permissionIndex = this.getPermissionIndexFromApplication(application);
    return this.userPermission.permissions[permissionIndex];
  }
}
