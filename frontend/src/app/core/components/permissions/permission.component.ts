import { Component, input, OnInit, output } from '@angular/core';
import { ApplicationType } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { ApplicationPermissionComponent } from './application-permission/application-permission.component';
import {
  MatTab,
  MatTabChangeEvent,
  MatTabContent,
  MatTabGroup,
  MatTabLabel,
} from '@angular/material/tabs';
import { FormGroup } from '@angular/forms';
import { ApplicationPermission } from './form/application-permission-form-group';

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
  form = input.required<FormGroup<ApplicationPermission>>();
  applicationChanged = output<ApplicationType>();

  protected readonly applications: ApplicationType[] =
    Object.values(ApplicationType);

  ngOnInit(): void {}

  protected readonly ApplicationType = ApplicationType;

  onSelectedTabChange($event: MatTabChangeEvent) {
    this.applicationChanged.emit(this.applications[$event.index]);
  }
}
