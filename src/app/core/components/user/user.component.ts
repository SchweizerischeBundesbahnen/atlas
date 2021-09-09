import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { User } from './user';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  user: User | undefined;
  userName: string | undefined;
  isAuthenticated = false;

  protected authenticated: boolean | undefined;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authenticated = this.authService.loggedIn;
    this.user = this.authService.claims;
    this.extractUserName();
    this.authenticate();
  }

  extractUserName() {
    this.userName = this.user?.name.substr(0, this.user.name.indexOf('(')).trim();
  }

  authenticate() {
    this.isAuthenticated = this.user != null;
  }

  login(): void {
    this.authService.login();
  }

  logout(): void {
    this.authService.logout();
  }
}
