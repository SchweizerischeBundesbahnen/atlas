import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserModel } from '../../../api/model/userModel';

@Component({
  selector: 'app-user-administration-basic',
  templateUrl: './user-administration-basic.component.html',
})
export class UserAdministrationBasicComponent implements OnInit {
  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any) {}

  user: UserModel = {};

  ngOnInit(): void {
    this.user = this.dialogData.user;
  }
}
