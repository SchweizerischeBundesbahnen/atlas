import { Component } from '@angular/core';
import { AuthService } from './core/auth.service';

import { environment } from '../environments/environment';

import packageJson from '../../package.json';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  version = packageJson.version;
  environmentLabel = environment.label;

  title = $localize`Timetable Field Number`;

  get userName() {
    return this.authService.claims?.name;
  }

  constructor(private authService: AuthService) {}

  login() {
    this.authService.login();
  }

  logout() {
    return this.authService.logout();
  }
}
