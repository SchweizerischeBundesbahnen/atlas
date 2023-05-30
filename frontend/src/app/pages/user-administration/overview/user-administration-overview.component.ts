import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../pages';

@Component({
  selector: 'app-user-administration-overview',
  templateUrl: './user-administration-overview.component.html',
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
