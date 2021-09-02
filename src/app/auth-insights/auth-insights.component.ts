import { Component } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { Pages } from '../model/pages';

@Component({
  selector: 'app-auth-insights',
  templateUrl: './auth-insights.component.html',
  styleUrls: ['./auth-insights.component.scss'],
})
export class AuthInsightsComponent {
  pageTile = Pages.AUTH_INSIGHT.title;

  get claims() {
    return this.authService.claims;
  }

  get scopes() {
    return this.authService.scopes;
  }

  constructor(private authService: AuthService) {}
}
