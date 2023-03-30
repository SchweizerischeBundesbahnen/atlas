import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { User } from '../../../../../api';

@Component({
  selector: 'app-user-administration',
  templateUrl: './user-administration-user-detail.component.html',
})
export class UserAdministrationUserDetailComponent implements OnInit {
  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any) {}

  user: User = {};

  ngOnInit(): void {
    this.user = this.dialogData.user;
  }
}
