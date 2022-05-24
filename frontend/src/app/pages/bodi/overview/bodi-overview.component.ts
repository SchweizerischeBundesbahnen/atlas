import { Component } from '@angular/core';
import { Pages } from '../../pages';
import { ActivatedRoute, Router } from '@angular/router';

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
  ];

  constructor(private route: ActivatedRoute, private router: Router) {}

  newBusinessOrganisation() {
    this.router
      .navigate([Pages.BUSINESS_ORGANISATIONS.path, 'add'], {
        relativeTo: this.route,
      })
      .then();
  }
}
