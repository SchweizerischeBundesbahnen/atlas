import {
  ApplicationRole,
  ApplicationType,
  BusinessOrganisation,
  BusinessOrganisationsService,
  UserPermissionCreateModel,
  UserPermissionModel,
} from '../../api';
import { Observable, take } from 'rxjs';
import { map } from 'rxjs/operators';

export class UserPermissionManager {
  constructor(private readonly boService: BusinessOrganisationsService) {}

  private readonly userPermission: UserPermissionCreateModel = {
    sbbUserId: '',
    permissions: [
      {
        application: 'TTFN',
        role: 'WRITER',
        sboids: [],
      },
      {
        application: 'LIDI',
        role: 'WRITER',
        sboids: [],
      },
    ],
  };

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

  getPermissions(): UserPermissionModel[] {
    return this.userPermission.permissions;
  }

  getCurrentRole(application: ApplicationType): ApplicationRole {
    const permissionIndex = this.getPermissionIndexFromApplication(application);
    return this.userPermission.permissions[permissionIndex].role;
  }

  setSbbUserId(userId: string): void {
    this.userPermission.sbbUserId = userId;
  }

  setPermissions(permissions: UserPermissionModel[]): void {
    this.userPermission.permissions = permissions;
  }

  changePermissionRole(application: ApplicationType, newRole: ApplicationRole): void {
    const permissionIndex = this.getPermissionIndexFromApplication(application);
    this.userPermission.permissions[permissionIndex].role = newRole;
  }

  removeSboidFromPermission(application: ApplicationType, sboidIndex: number): void {
    const permissionIndex = this.getPermissionIndexFromApplication(application);
    this.userPermission.permissions[permissionIndex].sboids.splice(sboidIndex, 1);
  }

  addSboidToPermission(
    application: ApplicationType,
    sboid: string
  ): Observable<BusinessOrganisation | undefined> {
    const permissionIndex = this.getPermissionIndexFromApplication(application);

    return this.boService
      .getAllBusinessOrganisations([sboid], undefined, undefined, 0, 1, ['sboid,ASC'])
      .pipe(
        take(1),
        map((result) => {
          if (!result.objects || result.objects.length === 0) {
            console.error('Could not resolve selected bo');
            return;
          }
          if (this.userPermission.permissions[permissionIndex].sboids.includes(sboid)) {
            console.error('Already added');
            return;
          }
          this.userPermission.permissions[permissionIndex].sboids.push(sboid);
          return result.objects[0];
        })
      );
  }

  private getPermissionIndexFromApplication(application: ApplicationType): number {
    return this.userPermission.permissions.findIndex(
      (permission) => permission.application === application
    );
  }
}
