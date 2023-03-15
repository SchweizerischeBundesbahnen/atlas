import { Component, OnInit } from '@angular/core';
import { Pages } from '../../pages';
import { ActivatedRoute, Router } from '@angular/router';
import { TabService } from '../../tab.service';

@Component({
  templateUrl: './lidi-overview.component.html',
})
export class LidiOverviewComponent implements OnInit {
  TABS = [
    {
      link: Pages.LINES.path,
      title: 'LIDI.LINE.LINES',
    },
    {
      link: Pages.SUBLINES.path,
      title: 'LIDI.SUBLINE.SUBLINES',
    },
    {
      link: Pages.WORKFLOWS.path,
      title: 'LIDI.LINE_VERSION_SNAPSHOT.TAB_HEADER',
    },
  ];
  activeTab = this.TABS[0];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tabService: TabService
  ) {}

  newLine() {
    this.router
      .navigate([Pages.LINES.path, 'add'], {
        relativeTo: this.route,
      })
      .then();
  }

  newSubline() {
    this.router
      .navigate([Pages.SUBLINES.path, 'add'], {
        relativeTo: this.route,
      })
      .then();
  }

  ngOnInit(): void {
    if (this.router.url) {
      const currentTabIndex = this.tabService.getCurrentTabIndex(this.router.url, this.TABS);
      this.activeTab = this.TABS[currentTabIndex];
    }
  }
}
