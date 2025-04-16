import { Component, ViewChild } from '@angular/core';
import { LoadingSpinnerService } from './core/components/loading-spinner/loading-spinner.service';
import { ServiceWorkerService } from './service-worker.service';
import { MatSidenav, MatSidenavContainer, MatSidenavContent } from '@angular/material/sidenav';
import { LoadingSpinnerComponent } from './core/components/loading-spinner/loading-spinner.component';
import { HeaderComponent } from './core/components/header/header.component';
import { MatNavList } from '@angular/material/list';
import { SideNavComponent } from './core/components/side-nav/side-nav.component';
import { RouterOutlet } from '@angular/router';
import { AsyncPipe } from '@angular/common';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
    providers: [ServiceWorkerService],
    imports: [LoadingSpinnerComponent, HeaderComponent, MatSidenavContainer, MatSidenav, MatNavList, SideNavComponent, MatSidenavContent, RouterOutlet, AsyncPipe]
})
export class AppComponent {
  @ViewChild('sidenav') sideNav?: MatSidenav;
  sideNavOpened = true;

  constructor(
    public readonly loadingSpinnerService: LoadingSpinnerService,
    private readonly _swService: ServiceWorkerService
  ) {}
}

Date.prototype.toISOString = function () {
  return (
    this.getFullYear() +
    '-' +
    ('0' + (this.getMonth() + 1)).slice(-2) +
    '-' +
    ('0' + this.getDate()).slice(-2)
  );
};
