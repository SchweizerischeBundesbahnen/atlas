import { Component } from '@angular/core';
import { Pages } from '../../pages';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  templateUrl: './bodi-overview.component.html',
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
  activeTab = this.TABS[0];

  constructor(private route: ActivatedRoute, private router: Router) {}

  newBusinessOrganisation() {
    this.router.navigate([Pages.BODI.path, Pages.BUSINESS_ORGANISATIONS.path, 'add']).then();
  }
}
