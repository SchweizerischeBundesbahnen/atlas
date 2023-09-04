import { Component, OnInit } from '@angular/core';
import { Pages } from '../../pages';
import { ActivatedRoute, Router } from '@angular/router';
import { OverviewToTabShareDataService } from './service/overview-to-tab-share-data.service';
import { Cantons } from '../../../core/cantons/Cantons';
import { HearingOverviewTab } from './model/hearing-overview-tab';

@Component({
  templateUrl: './overview-tab.component.html',
})
export class OverviewTabComponent implements OnInit {
  TABS: HearingOverviewTab[] = [
    {
      link: Pages.TTH_ACTIVE.path,
      title: 'TTH.TAB.ACTUAL',
    },
    {
      link: Pages.TTH_PLANNED.path,
      title: 'TTH.TAB.PLANNED',
    },
    {
      link: Pages.TTH_ARCHIVED.path,
      title: 'TTH.TAB.ARCHIVED',
    },
  ];
  cantonShort = Cantons.swiss.path;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private overviewToTabService: OverviewToTabShareDataService
  ) {}

  clickOnTab() {
    this.cantonShort = this.route.snapshot.params['canton'];
    this.overviewToTabService.changeData(this.cantonShort);
  }

  ngOnInit(): void {
    this.overviewToTabService.cantonShort$.subscribe((res) => (this.cantonShort = res));
    this.cantonShort = this.route.snapshot.params['canton'];
    this.overviewToTabService.changeData(this.cantonShort);
  }
}
