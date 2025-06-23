import { Component, inject, OnInit } from '@angular/core';
import { JsonPipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { UserService } from '../../core/auth/user/user.service';
import { User } from '../../core/auth/user/user';
import { PermissionComponent } from '../../core/components/permissions/permission.component';
import {
  ApplicationPermission,
  ApplicationPermissionFormGroupBuilder,
} from '../../core/components/permissions/form/application-permission-form-group';
import { ApplicationType, Permission } from '../../api';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
  imports: [TranslatePipe, PermissionComponent, JsonPipe],
})
export class UserProfileComponent implements OnInit {
  userService = inject(UserService);

  currentUser!: User;
  form!: FormGroup<ApplicationPermission>;

  ngOnInit() {
    this.currentUser = this.userService.currentUser!;
    this.form = ApplicationPermissionFormGroupBuilder.buildFormGroup(
      ApplicationType.Ttfn
    );
  }

  onApplicationChanged(application: ApplicationType) {
    this.form =
      ApplicationPermissionFormGroupBuilder.buildFormGroup(application);

    const permissions: Permission = this.currentUser.permissions.find(
      (i) => i.application === application
    )!;
    this.form.controls.role.setValue(permissions.role);
  }
}
