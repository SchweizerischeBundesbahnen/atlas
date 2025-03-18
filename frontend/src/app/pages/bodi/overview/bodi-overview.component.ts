import { Component } from '@angular/core';
import { Pages } from '../../pages';
import { ActivatedRoute, Router, RouterLinkActive, RouterLink, RouterOutlet } from '@angular/router';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { MatTabNav, MatTabLink, MatTabNavPanel } from '@angular/material/tabs';
import { NgFor } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    templateUrl: './bodi-overview.component.html',
    imports: [AtlasButtonComponent, MatTabNav, NgFor, MatTabLink, RouterLinkActive, RouterLink, MatTabNavPanel, RouterOutlet, TranslatePipe]
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

  constructor(private route: ActivatedRoute, private router: Router) {}

  newBusinessOrganisation() {
    this.router.navigate([Pages.BODI.path, Pages.BUSINESS_ORGANISATIONS.path, 'add']).then();
  }
}
