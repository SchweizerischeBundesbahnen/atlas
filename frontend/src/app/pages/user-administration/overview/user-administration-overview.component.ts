import { Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkActive, RouterLink, RouterOutlet } from '@angular/router';
import { Pages } from '../../pages';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { MatTabNav, MatTabLink, MatTabNavPanel } from '@angular/material/tabs';
import { NgFor } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-user-administration-overview',
    templateUrl: './user-administration-overview.component.html',
    imports: [AtlasButtonComponent, MatTabNav, NgFor, RouterLinkActive, MatTabLink, RouterLink, MatTabNavPanel, RouterOutlet, TranslatePipe]
})
export class UserAdministrationOverviewComponent {
  TABS = [
    {
      link: Pages.USERS.path,
      title: 'USER_ADMIN.TABS.USER',
    },
    {
      link: Pages.CLIENTS.path,
      title: 'USER_ADMIN.TABS.CLIENTS',
    },
  ];

  constructor(private route: ActivatedRoute, private router: Router) {}

  newUser(): Promise<boolean> {
    return this.router.navigate([Pages.USERS.path, 'add'], {
      relativeTo: this.route,
    });
  }

  newClient() {
    return this.router.navigate([Pages.CLIENTS.path, 'add'], {
      relativeTo: this.route,
    });
  }
}
