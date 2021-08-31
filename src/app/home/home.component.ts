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
    { headerTitle: 'TABLE.TTFN.SWISS_TIMETABLE_FIELD_NUMBER', value: 'swissTimetableFieldNumber' },
    { headerTitle: 'TABLE.TTFN.NAME', value: 'name' },
    { headerTitle: 'TABLE.TTFN.STATUS', value: 'status' },
    { headerTitle: 'TABLE.TTFN.TTFNID', value: 'ttfnid' },
    { headerTitle: 'TABLE.TTFN.VALID_FROM', value: 'validFrom' },
    { headerTitle: 'TABLE.TTFN.VALID_TO', value: 'validTo' },
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
      .getVersions()
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
