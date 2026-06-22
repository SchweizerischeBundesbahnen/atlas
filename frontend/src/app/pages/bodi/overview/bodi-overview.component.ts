import { Component, inject } from '@angular/core';
import { Pages } from '../../pages';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { MatTabLink, MatTabNav, MatTabNavPanel } from '@angular/material/tabs';

import { TranslatePipe } from '@ngx-translate/core';

@Component({
  templateUrl: './bodi-overview.component.html',
  imports: [
    AtlasButtonComponent,
    MatTabNav,
    MatTabLink,
    RouterLinkActive,
    RouterLink,
    MatTabNavPanel,
    RouterOutlet,
    TranslatePipe,
  ],
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

  private readonly router = inject(Router);

  newBusinessOrganisation() {
    this.router.navigate([Pages.BODI.path, Pages.BUSINESS_ORGANISATIONS.path, 'add']).then();
  }
}
