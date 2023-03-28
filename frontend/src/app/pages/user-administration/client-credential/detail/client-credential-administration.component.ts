import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ClientCredential } from '../../../../api';

@Component({
  selector: 'app-client-credential-administration',
  templateUrl: './client-credential-administration.component.html',
})
export class ClientCredentialAdministrationComponent implements OnInit {
  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any) {}

  clientCredential: ClientCredential = {};

  ngOnInit(): void {
    this.clientCredential = this.dialogData.clientCredential;
  }
}
