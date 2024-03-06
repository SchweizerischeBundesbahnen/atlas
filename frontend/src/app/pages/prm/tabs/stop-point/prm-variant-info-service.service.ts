import {Injectable} from '@angular/core';
import {AuthService} from "../../../../core/auth/auth.service";
import {ApplicationType, MeanOfTransport} from "../../../../api";
import {
  completeMeansOfTransport,
  PrmMeanOfTransportHelper,
  reducedMeansOfTransport
} from "../../util/prm-mean-of-transport-helper";

export const prmMeansOfTransport: MeanOfTransport[] = Object.values(MeanOfTransport)
  .filter((value) => value !== MeanOfTransport.Unknown);

@Injectable({
  providedIn: 'root'
})
export class PrmVariantInfoServiceService {

  constructor(private authService: AuthService) {
  }

  getPrmMeansOfTransportToShow(meansOfTransport: MeanOfTransport[]): MeanOfTransport[] | undefined {
    const isAtLeastSupervisor = this.authService.isAtLeastSupervisor(ApplicationType.Prm);
    if (isAtLeastSupervisor) {
      return prmMeansOfTransport;
    }
    const isReduced = PrmMeanOfTransportHelper.isReduced(meansOfTransport);
    return isReduced ? reducedMeansOfTransport : completeMeansOfTransport
  }

}
