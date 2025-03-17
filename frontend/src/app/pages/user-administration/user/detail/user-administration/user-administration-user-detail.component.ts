import { Component, OnInit } from '@angular/core';
import { User } from '../../../../../api';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-user-administration',
    templateUrl: './user-administration-user-detail.component.html',
    standalone: false
})
export class UserAdministrationUserDetailComponent implements OnInit {
  constructor(private activatedRoute: ActivatedRoute) {}

  user: User = {};

  ngOnInit(): void {
    this.user = this.activatedRoute.snapshot.data.user;
  }
}
