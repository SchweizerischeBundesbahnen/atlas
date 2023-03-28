import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../pages';
import { TabService } from '../../tab.service';

@Component({
  selector: 'app-user-administration-overview',
  templateUrl: './user-administration-overview.component.html',
  styleUrls: ['./user-administration-overview.component.scss'],
})
export class UserAdministrationOverviewComponent implements OnInit {
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
  activeTab = this.TABS[0];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tabService: TabService
  ) {}

  ngOnInit(): void {
    if (this.router.url) {
      const currentTabIndex = this.tabService.getCurrentTabIndex(this.router.url, this.TABS);
      this.activeTab = this.TABS[currentTabIndex];
    }
  }

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
