import { Component, Input, OnInit } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { TimetableFieldNumbersService, Version } from '../api';

import { environment } from '../../environments/environment';
import { TableColumn } from '../core/components/table/table-column';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  tableColumns: TableColumn<Version>[] = [
    { headerTitle: 'TABLE.TTFN.SWISS_TIMETABLE_FIELD_NUMBER', value: 'swissTimetableFieldNumber' },
    { headerTitle: 'TABLE.TTFN.NAME', value: 'name' },
    { headerTitle: 'TABLE.TTFN.STATUS', value: 'status' },
    { headerTitle: 'TABLE.TTFN.TTFNID', value: 'ttfnid' },
    { headerTitle: 'TABLE.TTFN.VALID_FROM', value: 'validFrom' },
    { headerTitle: 'TABLE.TTFN.VALID_TO', value: 'validTo' },
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
