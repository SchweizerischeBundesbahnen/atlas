import { Injectable } from '@angular/core';
import { Data, Router } from '@angular/router';
import { ReadStopPointVersion } from '../../api';
import { PrmMeanOfTransportHelper } from './prm-mean-of-transport-helper';
import { Pages } from '../pages';

@Injectable({
  providedIn: 'root',
})
export abstract class BasePrmComponentService {
  get isStopPointExisting() {
    return this._isStopPointExisting;
  }

  _isStopPointExisting!: boolean;

  protected router: Router;

  protected constructor(router: Router) {
    this.router = router;
  }

  protected closeSidePanel() {
    this.router.navigate([Pages.PRM.path]).then();
  }

  protected checkIsReducedOrComplete(data: Data): void {
    const stopPointVersions: ReadStopPointVersion[] = data.stopPoints;
    const servicePoints: ReadStopPointVersion[] = data.servicePoints;
    if (stopPointVersions.length === 0) {
      this.navigateToStopPoint(servicePoints);
    } else {
      this._isStopPointExisting = true;
      const isReduced = PrmMeanOfTransportHelper.isReduced(stopPointVersions[0].meansOfTransport);
      if (isReduced) {
        this.navigateToStopPoint(servicePoints);
      }
    }
  }

  protected checkStopPointExists(data: Data) {
    const stopPointVersions: ReadStopPointVersion[] = data.stopPoints;
    if (stopPointVersions.length === 0) {
      const servicePoints: ReadStopPointVersion[] = data.servicePoints;
      this.navigateToStopPoint(servicePoints);
    }
  }

  protected navigateToStopPoint(servicePoints: ReadStopPointVersion[]) {
    this.router
      .navigate([
        Pages.PRM.path,
        Pages.STOP_POINTS.path,
        servicePoints[0].sloid,
        Pages.PRM_STOP_POINT_TAB.path,
      ])
      .then();
  }
}
