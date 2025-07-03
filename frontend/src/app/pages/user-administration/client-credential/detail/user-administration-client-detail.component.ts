import { Component, inject, OnInit } from '@angular/core';
import { ClientCredential } from '../../../../api';
import { ActivatedRoute } from '@angular/router';
import { UserAdministrationClientEditComponent } from './edit/user-administration-client-edit.component';
import { UserAdministrationClientCreateComponent } from './create/user-administration-client-create.component';
import { FormGroup } from '@angular/forms';
import { UserPermissionGivenClientService } from './edit/user-permission-given-client.service';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';

@Component({
  selector: 'app-client-credential-administration',
  templateUrl: './user-administration-client-detail.component.html',
  imports: [
    UserAdministrationClientEditComponent,
    UserAdministrationClientCreateComponent,
  ],
})
export class UserAdministrationClientDetailComponent
  implements OnInit, DetailFormComponent
{
  activatedRoute = inject(ActivatedRoute);
  userPermissionGivenClientService = inject(UserPermissionGivenClientService);

  clientCredential: ClientCredential = {};

  ngOnInit(): void {
    this.activatedRoute.data.subscribe((data) => {
      this.clientCredential = data.clientCredential;
    });
  }

  get form(): FormGroup | undefined {
    return this.userPermissionGivenClientService.applicationPermissionFormGroup;
  }
}
