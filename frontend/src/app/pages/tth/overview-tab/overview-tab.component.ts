import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Pages } from '../../pages';
import { ActivatedRoute, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { OverviewToTabShareDataService } from './service/overview-to-tab-share-data.service';
import { HearingOverviewTab } from './model/hearing-overview-tab';
import { MatTabLink, MatTabNav, MatTabNavPanel } from '@angular/material/tabs';
import { TranslatePipe } from '@ngx-translate/core';
import { NgClass } from '@angular/common';
import { PermissionService } from '../../../core/auth/permission/permission.service';

@Component({
  templateUrl: './overview-tab.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [MatTabNav, RouterLinkActive, MatTabLink, RouterLink, MatTabNavPanel, RouterOutlet, TranslatePipe, NgClass],
})
export class OverviewTabComponent {
  protected readonly route = inject(ActivatedRoute);
  private readonly overviewToTabService = inject(OverviewToTabShareDataService);
  private readonly permissionService = inject(PermissionService);

  protected readonly isHearingYearPlanned = this.overviewToTabService.isHearingYearPlanned;
  protected readonly hearingStatus = this.overviewToTabService.hearingStatus;

  TABS: HearingOverviewTab[] = [
    {
      link: Pages.TTH_STATEMENTS.path,
      title: 'TTH.TAB.STATEMENTS',
    },
    {
      link: Pages.TTH_DOSSIERS.path,
      title: 'TTH.TAB.DOSSIERS',
    },
  ];

  BO_TTH_TABS: HearingOverviewTab[] = [
    {
      link: Pages.TTH_DOSSIERS.path,
      title: 'TTH.TAB.DOSSIERS',
    },
  ];

  constructor() {
    this.overviewToTabService.setCantonShort(this.route.snapshot.params['canton']);

    if (this.permissionService.getTthApplicationUserType() === 'BO_TTH') {
      this.TABS = this.BO_TTH_TABS;
    }
  }
}
