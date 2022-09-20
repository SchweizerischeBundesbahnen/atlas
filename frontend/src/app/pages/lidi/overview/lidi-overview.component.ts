import { Component } from '@angular/core';
import { Pages } from '../../pages';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { Role } from '../../../core/auth/role';
import { ApplicationType } from '../../../api';

@Component({
  templateUrl: './lidi-overview.component.html',
  styleUrls: ['./lidi-overview.component.scss'],
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
  ];

  readonly userAllowedToCreate;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private readonly authService: AuthService
  ) {
    this.userAllowedToCreate = authService.hasPermissionsToCreate(ApplicationType.Lidi);
  }

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
