import { Injectable } from '@angular/core';
import { ReadStopPointVersion } from '../../../api';
import { PRM_REDUCED_TABS, PRM_TABS, PrmTabs } from './prm-tabs';
import { PrmMeanOfTransportHelper } from '../util/prm-mean-of-transport-helper';
import { Subject } from 'rxjs';
import { Tab } from '../../tab';

@Injectable({ providedIn: 'root' })
export class PrmTabsService {
  tabs = new Subject<Tab[]>();
  disableTabNavigation = new Subject<boolean>();

  initTabs(stopPointVersions: ReadStopPointVersion[]) {
    if (stopPointVersions.length === 0) {
      this.disableTabNavigation.next(true);
      this.tabs.next([PrmTabs.STOP_POINT]);
    } else {
      this.disableTabNavigation.next(false);
      const isReduced = PrmMeanOfTransportHelper.isReduced(stopPointVersions[0].meansOfTransport);
      if (isReduced) {
        this.tabs.next(PRM_REDUCED_TABS);
      } else {
        this.tabs.next(PRM_TABS);
      }
    }
  }
}
