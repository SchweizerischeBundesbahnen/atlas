import { Component, OnInit } from '@angular/core';
import { AuthService } from './core/auth.service';

import { environment } from '../environments/environment';

import packageJson from '../../package.json';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  version = packageJson.version;
  environmentLabel = environment.label;

  title = $localize`Timetable Field Number`;
  constructor(private translate: TranslateService) // private keepalive: KeepaliveService,
  // public loadingSpinnerService: LoadingSpinnerService
  {
    //TODO: get lang from browser
    this.translate.use('de');
    // this.loadingSpinnerService.initLoadingSpinner();
  }

  ngOnInit(): void {
    // this.keepalive.initialize();
    console.log('app-component');
  }
}
