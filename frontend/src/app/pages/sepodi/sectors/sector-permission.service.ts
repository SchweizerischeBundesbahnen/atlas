import { inject, Injectable } from '@angular/core';
import { Countries } from '../../../core/country/Countries';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import {
  ApplicationType,
  MeanOfTransport,
  ReadServicePointVersion,
} from '../../../api';

@Injectable({ providedIn: 'root' })
export class SectorPermissionService {
  private readonly permissionService = inject(PermissionService);

  showCreateButton(servicePoint: ReadServicePointVersion[]) {
    const hasVersionWithMotTrain = servicePoint.some((i) =>
      i.meansOfTransport?.includes(MeanOfTransport.Train)
    );

    const hasPermissionsForOneVersion = servicePoint.some((i) => {
      return (
        this.permissionService.hasPermissionsToWrite(
          ApplicationType.Sepodi,
          i.businessOrganisation
        ) &&
        this.permissionService.hasPermissionsToWrite(
          ApplicationType.Sepodi,
          Countries.fromUicCode(i.number.uicCountryCode).enumCountry
        )
      );
    });

    return hasVersionWithMotTrain && hasPermissionsForOneVersion;
  }
}
