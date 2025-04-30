import { Component, OnInit } from '@angular/core';
import { User } from '../../../../../api';
import { ActivatedRoute } from '@angular/router';
import { NgIf } from '@angular/common';
import { UserAdministrationUserEditComponent } from '../edit/user-administration-user-edit.component';
import { UserAdministrationUserCreateComponent } from '../create/user-administration-user-create.component';

@Component({
  selector: 'app-user-administration',
  templateUrl: './user-administration-user-detail.component.html',
  imports: [
    NgIf,
    UserAdministrationUserEditComponent,
    UserAdministrationUserCreateComponent,
  ],
})
export class UserAdministrationUserDetailComponent implements OnInit {
  constructor(private activatedRoute: ActivatedRoute) {}

  user: User = {};

  ngOnInit(): void {
    this.user = this.activatedRoute.snapshot.data.user;
  }
}
