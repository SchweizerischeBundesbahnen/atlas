import { Component, inject, OnInit } from '@angular/core';
import { User } from '../../../../api';
import { ActivatedRoute } from '@angular/router';
import { UserAdministrationUserCreateComponent } from './create/user-administration-user-create.component';
import { UserAdministrationUserEditComponent } from './edit/user-administration-user-edit.component';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { FormGroup } from '@angular/forms';
import { UserPermissionGivenUserService } from './edit/user-permission-given-user.service';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-user-administration',
  templateUrl: './user-administration-user-detail.component.html',
  imports: [
    UserAdministrationUserEditComponent,
    UserAdministrationUserCreateComponent,
  ],
  providers: [TranslatePipe],
})
export class UserAdministrationUserDetailComponent
  implements OnInit, DetailFormComponent
{
  activatedRoute = inject(ActivatedRoute);
  userPermissionGivenUserService = inject(UserPermissionGivenUserService);

  user?: User;

  ngOnInit(): void {
    this.activatedRoute.data.subscribe((data) => {
      this.user = data.user;
    });
  }

  get form(): FormGroup | undefined {
    return this.userPermissionGivenUserService.applicationPermissionFormGroup;
  }
}
