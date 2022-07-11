import { Component } from '@angular/core';
import { Pages } from '../../pages';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { Role } from '../../../core/auth/role';

@Component({
  templateUrl: './bodi-overview.component.html',
  styleUrls: ['./bodi-overview.component.scss'],
})
export class BodiOverviewComponent {
  TABS = [
    {
      link: Pages.BUSINESS_ORGANISATIONS.path,
      title: 'BODI.BUSINESS_ORGANISATION.BUSINESS_ORGANISATIONS',
    },
    {
      link: Pages.TRANSPORT_COMPANIES.path,
      title: 'BODI.TRANSPORT_COMPANIES.TRANSPORT_COMPANIES',
    },
    {
      link: Pages.COMPANIES.path,
      title: 'BODI.COMPANIES.COMPANIES',
    },
  ];

  readonly userAllowedToCreate;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private readonly authService: AuthService
  ) {
    this.userAllowedToCreate = authService.hasAnyRole([Role.BoWriter, Role.BoAdmin]);
  }

  newBusinessOrganisation() {
    this.router
      .navigate([Pages.BUSINESS_ORGANISATIONS.path, 'add'], {
        relativeTo: this.route,
      })
      .then();
  }
}
