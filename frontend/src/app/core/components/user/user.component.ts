import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { User } from './user';
import { ApplicationRole, ApplicationType, Permission } from '../../../api';
import {UserService} from "../../auth/user.service";
import {filter} from "rxjs/operators";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  user: User | undefined;
  userName: string | undefined;
  isLoggedIn = false;
  isAdmin = false;
  permissions: Permission[] | undefined;

  constructor(private userService: UserService,
              private authService: AuthService) {}

  ngOnInit(): void {
    this.userService.userChanged.subscribe(() => this.init());
  }

  init() {
    this.isLoggedIn = this.userService.loggedIn;
    if (this.isLoggedIn) {
      this.user = this.userService.currentUser;
      this.extractUserName();
      this.loadPermissions();
    }
  }

  extractUserName() {
    this.userName = this.user?.name.substring(0, this.user.name.indexOf('(')).trim();
  }

  loadPermissions() {
    this.isAdmin = this.userService.isAdmin;
    this.permissions = this.userService.permissions
      .filter(
        (permission) =>
          !(
            permission.application === ApplicationType.TimetableHearing &&
            permission.role === ApplicationRole.Reader
          )
      );
  }

  login(): void {
    this.authService.login();
  }

  logout() {
    this.authService.logout();
  }
}
