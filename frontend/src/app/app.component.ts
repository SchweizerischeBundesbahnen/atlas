import { Component, ViewChild } from '@angular/core';
import { LoadingSpinnerService } from './core/components/loading-spinner/loading-spinner.service';
import { ServiceWorkerService } from './service-worker.service';
import { MatSidenav } from '@angular/material/sidenav';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [ServiceWorkerService],
})
export class AppComponent {
  @ViewChild('sidenav') sideNav?: MatSidenav;
  sideNavOpened = true;

  constructor(
    public loadingSpinnerService: LoadingSpinnerService,
    private readonly _swService: ServiceWorkerService,
  ) {
    loadingSpinnerService.initLoadingSpinner();
  }

  toggleSideNav(): void {
    this.sideNav?.toggle();
    this.sideNavOpened = !!this.sideNav?.opened;
  }
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
