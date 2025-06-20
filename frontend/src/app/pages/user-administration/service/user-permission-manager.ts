import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisation,
  BusinessOrganisationsService,
  Permission,
  PermissionRestrictionType,
  UserPermissionCreate,
} from '../../../api';
import { BehaviorSubject, firstValueFrom, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { PermissionPermissionRestrictionsInner } from '../../../api/model/permissionPermissionRestrictionsInner';

@Injectable()
export class UserPermissionManager {
  readonly userPermission: UserPermissionCreate = {
    sbbUserId: '',
    permissions: [
      {
        application: 'TTFN',
        role: 'READER',
        permissionRestrictions: [],
      },
      {
        application: 'LIDI',
        role: 'READER',
        permissionRestrictions: [],
      },
      {
        application: 'BODI',
        role: 'READER',
        permissionRestrictions: [],
      },
      {
        application: 'TIMETABLE_HEARING',
        role: 'READER',
        permissionRestrictions: [],
      },
      {
        application: 'SEPODI',
        role: 'READER',
        permissionRestrictions: [],
      },
      {
        application: 'PRM',
        role: 'READER',
        permissionRestrictions: [],
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
    SEPODI: [],
    PRM: [],
  };

  readonly boOfApplicationsSubject$: BehaviorSubject<{
    [application in ApplicationType]: BusinessOrganisation[];
  }> = new BehaviorSubject<{
    [application in ApplicationType]: BusinessOrganisation[];
  }>(this.businessOrganisationsOfApplication);

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
    BODI: [ApplicationRole.Reader, ApplicationRole.Supervisor],
    TIMETABLE_HEARING: [
      ApplicationRole.Reader,
      ApplicationRole.ExplicitReader,
      ApplicationRole.Writer,
      ApplicationRole.Supervisor,
    ],
    SEPODI: [
      ApplicationRole.Reader,
      ApplicationRole.Writer,
      ApplicationRole.SuperUser,
      ApplicationRole.Supervisor,
    ],
    PRM: [
      ApplicationRole.Reader,
      ApplicationRole.Writer,
      ApplicationRole.SuperUser,
      ApplicationRole.Supervisor,
    ],
  };

  constructor(private readonly boService: BusinessOrganisationsService) {}

  private boFormResetEventSource = new Subject<void>();
  readonly boFormResetEvent$ = this.boFormResetEventSource.asObservable();

  emitBoFormResetEvent(): void {
    this.boFormResetEventSource.next();
  }

  getAvailableApplicationRolesOfApplication(
    application: ApplicationType
  ): ApplicationRole[] {
    return this.availableApplicationRolesConfig[application];
  }

  clearPermisRestrIfNotWriterAndRemoveBOPermisRestrIfSepodiAndSuperUser(): void {
    this.userPermission.permissions.forEach((permission) => {
      const permissionIndex = this.getPermissionIndexFromApplication(
        ApplicationType.Sepodi
      );
      if (
        permission.role === ApplicationRole.SuperUser &&
        permission.application === ApplicationType.Sepodi
      ) {
        this.userPermission.permissions[
          permissionIndex
        ].permissionRestrictions = this.userPermission.permissions[
          permissionIndex
        ].permissionRestrictions.filter(
          (restriction) =>
            restriction.type === PermissionRestrictionType.Country ||
            restriction.type === PermissionRestrictionType.BulkImport ||
            restriction.type ===
              PermissionRestrictionType.InfoPlusTerminationVote ||
            restriction.type === PermissionRestrictionType.NovaTerminationVote
        );
      } else if (
        permission.role === ApplicationRole.Reader &&
        permission.application === ApplicationType.Sepodi
      ) {
        permission.permissionRestrictions =
          permission.permissionRestrictions.filter(
            (restriction) =>
              restriction.type ===
                PermissionRestrictionType.InfoPlusTerminationVote ||
              restriction.type === PermissionRestrictionType.NovaTerminationVote
          );
      } else if (
        permission.role === ApplicationRole.Supervisor &&
        permission.application === ApplicationType.Sepodi
      ) {
        permission.permissionRestrictions =
          permission.permissionRestrictions.filter(
            (restriction) =>
              restriction.type ===
                PermissionRestrictionType.InfoPlusTerminationVote ||
              restriction.type === PermissionRestrictionType.NovaTerminationVote
          );
      } else if (permission.role !== ApplicationRole.Writer) {
        permission.permissionRestrictions = [];
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

  setPermissions(permissions: Permission[]): void {
    permissions.forEach((permission) => {
      const application = permission.application;
      const permissionIndex =
        this.getPermissionIndexFromApplication(application);
      this.userPermission.permissions[permissionIndex].role = permission.role;
      this.boOfApplicationsSubject$.next(
        this.businessOrganisationsOfApplication
      );
      permission.permissionRestrictions
        .filter(
          (restriction) =>
            restriction.type === PermissionRestrictionType.BusinessOrganisation
        )
        .forEach((sboid) => {
          this.addSboidToPermission(application, sboid.valueAsString!);
        });
      permission.permissionRestrictions
        .filter(
          (restriction) => restriction.type === PermissionRestrictionType.Canton
        )
        .forEach((canton) => {
          this.userPermission.permissions[
            permissionIndex
          ].permissionRestrictions.push({
            valueAsString: canton.valueAsString,
            type: PermissionRestrictionType.Canton,
          });
        });

      permission.permissionRestrictions
        .filter(
          (restriction) =>
            restriction.type === PermissionRestrictionType.Country
        )
        .forEach((country) => {
          this.userPermission.permissions[
            permissionIndex
          ].permissionRestrictions.push({
            valueAsString: country.valueAsString,
            type: PermissionRestrictionType.Country,
          });
        });

      const bulkImport = permission.permissionRestrictions.find(
        (r) => r.type === PermissionRestrictionType.BulkImport
      );
      if (bulkImport?.valueAsString != null) {
        this.replaceRestrictions(
          this.userPermission.permissions[permissionIndex]
            .permissionRestrictions,
          PermissionRestrictionType.BulkImport,
          bulkImport.valueAsString
        );
      }

      const nova = permission.permissionRestrictions.find(
        (r) => r.type === PermissionRestrictionType.NovaTerminationVote
      );
      if (nova?.valueAsString != null) {
        this.replaceRestrictions(
          this.userPermission.permissions[permissionIndex]
            .permissionRestrictions,
          PermissionRestrictionType.NovaTerminationVote,
          nova.valueAsString
        );
      }

      const infoPlus = permission.permissionRestrictions.find(
        (r) => r.type === PermissionRestrictionType.InfoPlusTerminationVote
      );
      if (infoPlus?.valueAsString != null) {
        this.replaceRestrictions(
          this.userPermission.permissions[permissionIndex]
            .permissionRestrictions,
          PermissionRestrictionType.InfoPlusTerminationVote,
          infoPlus.valueAsString
        );
      }
    });
  }

  replaceRestrictions(
    restrictions: PermissionPermissionRestrictionsInner[],
    type: PermissionRestrictionType,
    valueAsString: string
  ): void {
    const existing = restrictions.find((r) => r.type === type);
    if (existing) {
      existing.valueAsString = valueAsString;
    } else {
      restrictions.push({ type, valueAsString });
    }
  }

  changePermissionRole(
    application: ApplicationType,
    newRole: ApplicationRole
  ): void {
    const permissionIndex = this.getPermissionIndexFromApplication(application);
    this.userPermission.permissions[permissionIndex].role = newRole;
  }

  removeSboidFromPermission(
    application: ApplicationType,
    sboidIndex: number
  ): void {
    const permissionIndex = this.getPermissionIndexFromApplication(application);
    const sboidToDelete =
      this.businessOrganisationsOfApplication[application][sboidIndex].sboid;
    this.userPermission.permissions[permissionIndex].permissionRestrictions =
      this.userPermission.permissions[
        permissionIndex
      ].permissionRestrictions.filter(
        (sboid) => sboid.valueAsString !== sboidToDelete
      );
    this.businessOrganisationsOfApplication[application] =
      this.businessOrganisationsOfApplication[application].filter(
        (_, index) => index !== sboidIndex
      );
    this.boOfApplicationsSubject$.next(this.businessOrganisationsOfApplication);
  }

  addSboidToPermission(application: ApplicationType, sboid: string): void {
    const permission = this.getPermissionByApplication(application);

    firstValueFrom(
      this.boService.getAllBusinessOrganisations(
        [sboid],
        undefined,
        undefined,
        undefined,
        0,
        1,
        ['sboid,ASC']
      )
    ).then((result) => {
      if (!result.objects || result.objects.length === 0) {
        console.error('Could not resolve selected bo');
        return;
      }
      if (this.getRestrictionValues(permission).includes(sboid)) {
        console.error('Already added');
        return;
      }
      permission.permissionRestrictions.push({
        valueAsString: sboid,
        type: PermissionRestrictionType.BusinessOrganisation,
      });
      this.businessOrganisationsOfApplication[application] = [
        ...this.businessOrganisationsOfApplication[application],
        result.objects[0],
      ];
      this.boOfApplicationsSubject$.next(
        this.businessOrganisationsOfApplication
      );
    });
  }

  private getPermissionIndexFromApplication(
    application: ApplicationType
  ): number {
    return this.userPermission.permissions.findIndex(
      (permission) => permission.application === application
    );
  }

  getPermissionByApplication(application: ApplicationType) {
    const permissionIndex = this.getPermissionIndexFromApplication(application);
    return this.userPermission.permissions[permissionIndex];
  }

  getRestrictionValues(userPermission: Permission) {
    return userPermission.permissionRestrictions.map(
      (restriction) => restriction.valueAsString
    );
  }
}
