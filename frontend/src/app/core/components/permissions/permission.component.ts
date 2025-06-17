import { Component, input, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { ApplicationRole, ApplicationType, Permission } from '../../../api';
import { UserService } from '../../auth/user/user.service';
import { User } from '../../auth/user/user';
import { JsonPipe, NgIf } from '@angular/common';
import { MatButton } from '@angular/material/button';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { TranslatePipe } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { Pages } from '../../../pages/pages';
import {
  ApplicationPermissionFormGroupBuilder,
  PermissionsForm,
} from './form/permission-form-group';
import { ApplicationPermissionComponent } from './application-permission/application-permission.component';

@Component({
  selector: 'atlas-permission',
  templateUrl: './permission.component.html',
  styleUrls: ['./permission.component.scss'],
  imports: [ApplicationPermissionComponent, JsonPipe],
})
export class PermissionComponent implements OnInit {
  editable = input(false);

  protected readonly applications: ApplicationType[] =
    Object.values(ApplicationType);

  form!: PermissionsForm;

  ngOnInit(): void {
    this.form = ApplicationPermissionFormGroupBuilder.buildFormGroup();
  }

  protected readonly ApplicationType = ApplicationType;
}
