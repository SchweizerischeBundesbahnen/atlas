import { Component, inject, OnInit } from '@angular/core';
import { User } from '../../../../api';
import { ActivatedRoute } from '@angular/router';
import { UserAdministrationUserCreateComponent } from './create/user-administration-user-create.component';
import { UserAdministrationUserEditComponent } from './edit/user-administration-user-edit.component';

@Component({
  selector: 'app-user-administration',
  templateUrl: './user-administration-user-detail.component.html',
  imports: [
    UserAdministrationUserEditComponent,
    UserAdministrationUserCreateComponent,
  ],
})
export class UserAdministrationUserDetailComponent implements OnInit {
  activatedRoute = inject(ActivatedRoute);

  user?: User;

  ngOnInit(): void {
    this.activatedRoute.data.subscribe((data) => {
      this.user = data.user;
    });
  }
}
