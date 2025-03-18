import {Component} from '@angular/core';
import {Pages} from '../../pages';
import { ActivatedRoute, Router, RouterLinkActive, RouterLink, RouterOutlet } from '@angular/router';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { MatTabNav, MatTabLink, MatTabNavPanel } from '@angular/material/tabs';
import { NgFor } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    templateUrl: './lidi-overview.component.html',
    imports: [AtlasButtonComponent, MatTabNav, NgFor, RouterLinkActive, MatTabLink, RouterLink, MatTabNavPanel, RouterOutlet, TranslatePipe]
})
export class LidiOverviewComponent {
  TABS = [
    {
      link: Pages.LINES.path,
      title: 'LIDI.LINE.LINES',
    },
    {
      link: Pages.WORKFLOWS.path,
      title: 'LIDI.LINE_VERSION_SNAPSHOT.TAB_HEADER',
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
