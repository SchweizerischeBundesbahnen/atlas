import { Component, Input, OnInit } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { TimetableFieldNumbersService, Version } from '../api';

import { environment } from '../../environments/environment';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  tableColumns = [
    'TABLE.TTFN.swissTimetableFieldNumber',
    'TABLE.TTFN.name',
    'TABLE.TTFN.status',
    'TABLE.TTFN.ttfnid',
    'TABLE.TTFN.validFrom',
    'TABLE.TTFN.validTo',
  ];

  @Input() isLoading = false;

  versions: Version[] = [];
  envUrl = environment.backendUrl;

  constructor(
    private authService: AuthService,
    private timetableFieldNumbersService: TimetableFieldNumbersService
  ) {}

  ngOnInit(): void {
    this.getVersions();
  }

  get loggedIn() {
    return this.authService.loggedIn;
  }

  getVersions(): void {
    this.timetableFieldNumbersService
      .getVersions()
      .subscribe((versions) => (this.versions = versions));
  }
}
