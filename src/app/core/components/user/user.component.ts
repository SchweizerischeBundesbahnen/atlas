import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth.service';
import { User } from '../../../model/user';
import { Observable, of } from 'rxjs';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  user$: Observable<User | undefined> = of(undefined);

  protected authenticated: boolean | undefined;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authenticated = this.authService.loggedIn;
    this.user$ = of(this.authService.claims);
  }

  login(): void {
    this.authService.login();
  }

  logout(): void {
    this.authService.logout();
  }
}
