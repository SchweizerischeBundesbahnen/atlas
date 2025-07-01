import { Component, OnInit } from '@angular/core';
import { ClientCredential } from '../../../../api';
import { ActivatedRoute } from '@angular/router';
import { NgIf } from '@angular/common';
import { UserAdministrationClientEditComponent } from './edit/user-administration-client-edit.component';
import { UserAdministrationClientCreateComponent } from './create/user-administration-client-create.component';

@Component({
  selector: 'app-client-credential-administration',
  templateUrl: './user-administration-client-detail.component.html',
  imports: [
    UserAdministrationClientEditComponent,
    UserAdministrationClientCreateComponent,
  ],
})
export class UserAdministrationClientDetailComponent implements OnInit {
  constructor(private activatedRoute: ActivatedRoute) {}

  clientCredential: ClientCredential = {};

  ngOnInit(): void {
    this.activatedRoute.data.subscribe((data) => {
      this.clientCredential = data.clientCredential;
    });
  }
}
