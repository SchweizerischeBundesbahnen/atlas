import {Injectable} from '@angular/core';
import {ApplicationType, MeanOfTransport} from "../../../../api";
import {
  completeMeansOfTransport,
  PrmMeanOfTransportHelper,
  reducedMeansOfTransport
} from "../../util/prm-mean-of-transport-helper";
import {PermissionService} from "../../../../core/auth/permission.service";

export const prmMeansOfTransport: MeanOfTransport[] = Object.values(MeanOfTransport)
  .filter((value) => value !== MeanOfTransport.Unknown);

@Injectable({
  providedIn: 'root'
})
export class PrmVariantInfoServiceService {

  constructor(private permissionService: PermissionService) {
  }

  getPrmMeansOfTransportToShow(meansOfTransport: MeanOfTransport[]): MeanOfTransport[] | undefined {
    const isAtLeastSupervisor = this.permissionService.isAtLeastSupervisor(ApplicationType.Prm);
    if (isAtLeastSupervisor) {
      return prmMeansOfTransport;
    }
    const isReduced = PrmMeanOfTransportHelper.isReduced(meansOfTransport);
    return isReduced ? reducedMeansOfTransport : completeMeansOfTransport
  }

}
