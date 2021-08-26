import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth.service';
import { User } from '../../../model/user';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  user: User | undefined;

  protected authenticated: boolean | undefined;
  userName: string | undefined;
  isAuthenticated = false;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authenticated = this.authService.loggedIn;
    this.user = this.authService.claims;
    this.userName = this.user?.name.substr(0, this.user.name.indexOf('(')).trim();
    this.isAuthenticated = this.user != null;
  }

  login(): void {
    this.authService.login();
  }

  logout(): void {
    this.authService.logout();
  }
}
