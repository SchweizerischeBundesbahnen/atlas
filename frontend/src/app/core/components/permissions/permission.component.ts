import { Component, output } from '@angular/core';
import { ApplicationType } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { ApplicationPermissionComponent } from './application-permission/application-permission.component';
import {
  MatTab,
  MatTabContent,
  MatTabGroup,
  MatTabLabel,
} from '@angular/material/tabs';

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
export class PermissionComponent {
  applicationChanged = output<ApplicationType>();

  protected readonly applications: ApplicationType[] =
    Object.values(ApplicationType);

  protected readonly ApplicationType = ApplicationType;

  onSelectedTabChange(index: number) {
    this.applicationChanged.emit(this.applications[index]);
  }
}
