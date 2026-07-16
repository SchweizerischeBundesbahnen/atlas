import { Component, inject } from '@angular/core';
import { Pages } from '../../pages';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { MatTabNavPanel } from '@angular/material/tabs';

@Component({
  templateUrl: './lidi-overview.component.html',
  imports: [AtlasButtonComponent, MatTabNavPanel, RouterOutlet],
})
export class LidiOverviewComponent {
  TABS = [
    {
      link: Pages.LINES.path,
      title: 'LIDI.LINE.LINES',
    },
  ];

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

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
