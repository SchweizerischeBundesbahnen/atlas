import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ClientCredential } from '../../../../api';

@Component({
  selector: 'app-client-credential-administration',
  templateUrl: './user-administration-client-detail.component.html',
})
export class UserAdministrationClientDetailComponent implements OnInit {
  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any) {}

  clientCredential: ClientCredential = {};

  ngOnInit(): void {
    this.clientCredential = this.dialogData.clientCredential;
  }
}
