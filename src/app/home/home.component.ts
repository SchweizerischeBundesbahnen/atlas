import { Component } from '@angular/core';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  get loggedIn() {
    return this.authService.loggedIn;
  }

  constructor(private authService: AuthService) {}
}
