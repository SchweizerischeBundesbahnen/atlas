import { Injectable } from '@angular/core';
import { Data, Router } from '@angular/router';
import { ReadStopPointVersion } from '../../api';
import { PrmMeanOfTransportHelper } from './prm-mean-of-transport-helper';
import { Pages } from '../pages';
import { PrmTab } from './prm-panel/prm-tab';
import { Tab } from '../tab';

@Injectable({
  providedIn: 'root',
})
export abstract class BasePrmComponentService {
  get isStopPointExisting() {
    return this._isStopPointExisting;
  }

  abstract getTag(): Tab;

  _isStopPointExisting!: boolean;

  protected router: Router;

  protected constructor(router: Router) {
    this.router = router;
  }

  protected closeSidePanel() {
    this.router.navigate([Pages.PRM.path]).then();
  }

  showCurrentTab(data: Data): void {
    const stopPointVersions: ReadStopPointVersion[] = data.stopPoints;
    const servicePoints: ReadStopPointVersion[] = data.servicePoints;
    if (stopPointVersions.length === 0) {
      this.redirectToStopPoint(servicePoints);
    } else {
      this._isStopPointExisting = true;
      const isReduced = PrmMeanOfTransportHelper.isReduced(stopPointVersions[0].meansOfTransport);
      if (isReduced && this.canShowTab()) {
        this.redirectToStopPoint(servicePoints);
      }
    }
  }

  canShowTab() {
    return PrmTab.completeTabs.includes(this.getTag());
  }

  redirectToStopPoint(servicePoints: ReadStopPointVersion[]) {
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
