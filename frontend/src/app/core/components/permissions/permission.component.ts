import { Component, inject, OnInit, output, ViewChild } from '@angular/core';
import { ApplicationType } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { ApplicationPermissionComponent } from './application-permission/application-permission.component';
import {
  MatTab,
  MatTabContent,
  MatTabGroup,
  MatTabHeader,
  MatTabLabel,
} from '@angular/material/tabs';
import { UserPermissionProviderService } from './application-permission/user-permission-provider-service';
import { DialogService } from '../dialog/dialog.service';

@Component({
  selector: 'atlas-permission',
  templateUrl: './permission.component.html',
  styleUrls: ['./permission.component.scss'],
  imports: [
    ApplicationPermissionComponent,
    TranslatePipe,
    MatTabGroup,
    MatTab,
    MatTabLabel,
    MatTabContent,
  ],
})
export class PermissionComponent implements OnInit {
  protected readonly applications: ApplicationType[] =
    Object.values(ApplicationType);

  userPermissionProviderService = inject(UserPermissionProviderService);
  dialogService = inject(DialogService);
  applicationChanged = output<ApplicationType>();

  protected readonly ApplicationType = ApplicationType;

  @ViewChild(MatTabGroup, { static: true }) applicationTabs!: MatTabGroup;

  ngOnInit(): void {
    this.applicationTabs._handleClick = (
      _tab: MatTab,
      tabHeader: MatTabHeader,
      index: number
    ) => {
      if (this.applicationTabs.selectedIndex != index) {
        if (this.userPermissionProviderService.getCurrentForm()?.dirty) {
          this.dialogService.confirmLeave().subscribe((result) => {
            if (result) {
              this.changeTab(tabHeader, index);
            }
          });
        } else {
          this.changeTab(tabHeader, index);
        }
      }
    };
  }

  private changeTab(tabHeader: MatTabHeader, index: number) {
    tabHeader.focusIndex = index;
    this.applicationTabs.selectedIndex = index;
    this.applicationChanged.emit(this.applications[index]);
  }
}
