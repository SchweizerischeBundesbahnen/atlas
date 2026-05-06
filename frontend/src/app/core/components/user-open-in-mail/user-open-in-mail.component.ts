import { Component, inject, input } from '@angular/core';
import { UserAdministrationService } from '../../../api/service/user-administration/user-administration.service';
import { ApplicationType, User } from '../../../api';
import { PermissionService } from '../../auth/permission/permission.service';

@Component({
  selector: 'atlas-user-open-in-mail',
  styleUrls: ['./user-open-in-mail.component.scss'],
  templateUrl: './user-open-in-mail.component.html',
})
export class UserOpenInMailComponent {
  readonly userId = input.required<string>();
  readonly applicationType = input.required<ApplicationType>();

  readonly userAdministrationService = inject(UserAdministrationService);
  readonly permissionService = inject(PermissionService);

  openInMail() {
    this.userAdministrationService.getUser(this.userId()).subscribe((user: User) => {
      window.open(`mailto:${user.mail}`, '_self');
    });
  }
}
