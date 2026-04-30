import { Component, inject } from '@angular/core';
import { LoadingSpinnerService } from './core/components/loading-spinner/loading-spinner.service';
import { MatSidenav, MatSidenavContainer, MatSidenavContent } from '@angular/material/sidenav';
import { LoadingSpinnerComponent } from './core/components/loading-spinner/loading-spinner.component';
import { HeaderComponent } from './core/components/header/header.component';
import { MatNavList } from '@angular/material/list';
import { SideNavComponent } from './core/components/side-nav/side-nav.component';
import { RouterOutlet } from '@angular/router';
import { AsyncPipe } from '@angular/common';
import { AuthService } from './core/auth/auth.service';

@Component({
  selector: 'atlas-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [
    LoadingSpinnerComponent,
    HeaderComponent,
    MatSidenavContainer,
    MatSidenav,
    MatNavList,
    SideNavComponent,
    MatSidenavContent,
    RouterOutlet,
    AsyncPipe,
  ],
})
export class AppComponent {
  protected readonly authenticated$ = inject(AuthService).initAuth();
  protected readonly loadingSpinnerService = inject(LoadingSpinnerService);
}

Date.prototype.toISOString = function () {
  return this.getFullYear() + '-' + ('0' + (this.getMonth() + 1)).slice(-2) + '-' + ('0' + this.getDate()).slice(-2);
};
