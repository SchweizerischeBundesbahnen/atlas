import { Component, Input, OnInit } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { TimetableFieldNumbersService, Version } from '../api';

import { TableColumn } from '../core/components/table/table-column';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  tableColumns: TableColumn<Version>[] = [
    { headerTitle: 'TTFN.SWISS_TIMETABLE_FIELD_NUMBER', value: 'swissTimetableFieldNumber' },
    { headerTitle: 'TTFN.NAME', value: 'name' },
    { headerTitle: 'TTFN.STATUS', value: 'status' },
    { headerTitle: 'TTFN.TTFNID', value: 'ttfnid' },
    { headerTitle: 'TTFN.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'TTFN.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  @Input() isLoading = false;

  versions: Version[] = [];

  constructor(
    private authService: AuthService,
    private timetableFieldNumbersService: TimetableFieldNumbersService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.getVersions();
  }

  get loggedIn() {
    return this.authService.loggedIn;
  }

  getVersions(): void {
    this.timetableFieldNumbersService
      .getVersions(0, 0, ['id,ASC'])
      .subscribe((versions) => (this.versions = versions));
  }

  newVersion() {
    this.router
      .navigate(['add'], {
        relativeTo: this.route,
      })
      .then();
  }

  editVersion($event: Version) {
    this.router
      .navigate([$event.id], {
        relativeTo: this.route,
      })
      .then();
  }
}
