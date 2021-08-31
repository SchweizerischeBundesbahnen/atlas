import { Component } from '@angular/core';

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
  constructor() {
    // private keepalive: KeepaliveService, // public loadingSpinnerService: LoadingSpinnerService
    // this.loadingSpinnerService.initLoadingSpinner();
  }
}
