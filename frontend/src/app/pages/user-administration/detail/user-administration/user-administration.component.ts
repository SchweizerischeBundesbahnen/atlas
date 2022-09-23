import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserModel } from '../../../../api/model/userModel';

@Component({
  selector: 'app-user-administration',
  templateUrl: './user-administration.component.html',
})
export class UserAdministrationComponent implements OnInit {
  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any) {}

  user: UserModel = {};

  ngOnInit(): void {
    this.user = this.dialogData.user;
  }
}
