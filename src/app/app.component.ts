import { Component } from '@angular/core';

import { LoadingSpinnerService } from './core/components/loading-spinner/loading-spinner.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  constructor(public loadingSpinnerService: LoadingSpinnerService) {
    // private keepalive: KeepaliveService
    this.loadingSpinnerService.initLoadingSpinner();
  }
}
