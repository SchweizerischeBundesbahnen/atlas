import { Component } from '@angular/core';

import packageJson from '../../package.json';
import { LoadingSpinnerService } from './core/loading-spinner/loading-spinner.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  version = packageJson.version;

  title = $localize`Timetable Field Number`;

  constructor(public loadingSpinnerService: LoadingSpinnerService) {
    // private keepalive: KeepaliveService
    this.loadingSpinnerService.initLoadingSpinner();
  }
}
