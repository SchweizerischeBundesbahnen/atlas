import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { ApplicationRole, ApplicationType, Permission } from '../../../api';
import { UserService } from '../../auth/user/user.service';
import { User } from '../../auth/user/user';
import { MatButton } from '@angular/material/button';
import { MatMenu, MatMenuTrigger } from '@angular/material/menu';
import { TranslatePipe } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { Pages } from '../../../pages/pages';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
  imports: [MatButton, MatMenuTrigger, MatMenu, TranslatePipe],
  providers: [TranslatePipe],
})
export class UserComponent implements OnInit {
  user: User | undefined;
  userName: string | undefined;
  isLoggedIn = false;
  isAdmin = false;
  permissions: Permission[] | undefined;

  router = inject(Router);
  userService = inject(UserService);
  authService = inject(AuthService);

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
    this.userName = this.removeDepartment(this.user?.name);
  }

  removeDepartment(username?: string) {
    const departmentStart = '(';
    if (!username?.includes(departmentStart)) {
      return username;
    }
    return username?.substring(0, username.indexOf(departmentStart)).trim();
  }

  loadPermissions() {
    this.isAdmin = this.userService.isAdmin;
    this.permissions = this.userService.permissions.filter(
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

  goToProfile() {
    this.router.navigate([Pages.USER_PROFILE.path]).then();
  }
}
