import { Component, OnInit } from '@angular/core';
import { Pages } from '../../pages';
import { ActivatedRoute, Router } from '@angular/router';
import { TabService } from '../../tab.service';
import { OverviewToTabService } from './overview-to-tab.service';

@Component({
  templateUrl: './timetable-hearing-overview-tab.component.html',
})
export class TimetableHearingOverviewTabComponent implements OnInit {
  TABS = [
    {
      link: Pages.TTH_ACTUAL.path,
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
  activeTab = this.TABS[0];
  cantonShort = 'ch';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tabService: TabService,
    private overviewToTabService: OverviewToTabService
  ) {}

  navigateTo(tab: any) {
    this.cantonShort = this.route.snapshot.params['canton'];
    this.activeTab = tab;
    this.overviewToTabService.changeData(this.cantonShort);
  }

  ngOnInit(): void {
    this.overviewToTabService.cantonShort$.subscribe((res) => (this.cantonShort = res));
    this.cantonShort = this.route.snapshot.params['canton'];
    this.overviewToTabService.changeData(this.cantonShort);
    if (this.router.url) {
      const currentTabIndex = this.tabService.getCurrentTabIndex(this.router.url, this.TABS);
      this.activeTab = this.TABS[currentTabIndex];
    }
  }
}
