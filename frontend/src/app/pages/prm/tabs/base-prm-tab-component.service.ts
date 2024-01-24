import { Injectable } from '@angular/core';
import { Data, Router } from '@angular/router';
import { ReadServicePointVersion, ReadStopPointVersion } from '../../../api';
import { PrmMeanOfTransportHelper } from '../util/prm-mean-of-transport-helper';
import { Pages } from '../../pages';
import { PRM_COMPLETE_TABS } from '../prm-panel/prm-tabs';
import { Tab } from '../../tab';

@Injectable({
  providedIn: 'root',
})
export abstract class BasePrmTabComponentService {
  get isStopPointExisting() {
    return this._isStopPointExisting;
  }

  abstract getTab(): Tab;

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
    const servicePoints: ReadServicePointVersion[] = data.servicePoints;
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
    return PRM_COMPLETE_TABS.includes(this.getTab());
  }

  redirectToStopPoint(servicePoints: ReadServicePointVersion[]) {
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
