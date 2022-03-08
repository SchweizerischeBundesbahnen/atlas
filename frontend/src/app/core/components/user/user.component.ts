import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { User } from './user';
import jwtDecode from 'jwt-decode';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  user: User | undefined;
  userName: string | undefined;
  isAuthenticated = false;
  roles: string[] | undefined;

  protected authenticated: boolean | undefined;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.eventUserComponentNotification?.subscribe((user) => {
      if (user) {
        this.init();
      }
    });
    this.init();
  }

  init() {
    this.authenticated = this.authService.loggedIn;
    this.user = this.authService.claims;
    this.extractUserName();
    this.authenticate();
    this.extractRoles();
  }

  extractUserName() {
    this.userName = this.user?.name.substr(0, this.user.name.indexOf('(')).trim();
  }

  extractRoles() {
    if (this.authService.accessToken) {
      this.roles = this.decodeAccessToken().roles.filter((role) => role !== 'apim-default-role');
    }
  }

  private decodeAccessToken(): User {
    return jwtDecode(this.authService.accessToken);
  }

  authenticate() {
    this.isAuthenticated = this.user != null;
  }

  login(): void {
    this.authService.login();
  }

  logout() {
    return this.authService.logout()?.then(() => {
      this.user = undefined;
      this.authenticate();
    });
  }
}
