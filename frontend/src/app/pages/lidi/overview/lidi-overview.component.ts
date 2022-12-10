import { Component } from '@angular/core';
import { Pages } from '../../pages';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  templateUrl: './lidi-overview.component.html',
})
export class LidiOverviewComponent {
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
      title: 'LIDI.LINE_WORKFLOW.TAB_HEADER',
    },
  ];

  constructor(private route: ActivatedRoute, private router: Router) {}

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
}
