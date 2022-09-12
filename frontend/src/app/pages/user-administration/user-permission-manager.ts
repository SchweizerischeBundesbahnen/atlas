import {
  BusinessOrganisation,
  BusinessOrganisationsService,
  UserPermissionCreateModel,
  UserPermissionModel,
} from '../../api';
import { catchError, mapTo, Observable, of, tap } from 'rxjs';
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

  getPermissions(): UserPermissionModel[] {
    return this.userPermission.permissions;
  }

  addSboidToPermission(
    application: string,
    sboid: string
  ): Observable<BusinessOrganisation | undefined> {
    const permissionIndex = this.userPermission.permissions.findIndex(
      (permission) => permission.application === application
    );

    return this.boService
      .getAllBusinessOrganisations([sboid], undefined, undefined, 0, 1, ['sboid,ASC'])
      .pipe(
        map((result) => {
          if (!result.objects || result.objects.length === 0) {
            console.error('Could not resolve selected bo');
            return;
          }
          if (this.userPermission.permissions[permissionIndex].sboids.includes(sboid)) {
            console.error('Already added');
            return;
          }
          // this.relationComponent.table.renderRows();
          this.userPermission.permissions[permissionIndex].sboids.push(sboid);
          return result.objects[0];
        })
      );
  }
}
