import { Component } from '@angular/core';
import { TableColumn } from '../../../../core/components/table/table-column';
import {
  TimetableFieldNumber,
  TimetableFieldNumbersService,
  TrafficPointElementsService,
} from '../../../../api';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { ActivatedRoute, Router } from '@angular/router';
import { TableService } from '../../../../core/components/table/table.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-service-point-traffic-point-elements-table',
  templateUrl: './traffic-point-elements-table.component.html',
  styleUrls: ['./traffic-point-elements-table.component.scss'],
})
export class TrafficPointElementsTableComponent {
  tableColumns: TableColumn<TimetableFieldNumber>[] = [
    { headerTitle: 'TTFN.NUMBER', value: 'number' },
    { headerTitle: 'TTFN.DESCRIPTION', value: 'description' },
    { headerTitle: 'TTFN.SWISS_TIMETABLE_FIELD_NUMBER', value: 'swissTimetableFieldNumber' },
    { headerTitle: 'TTFN.TTFNID', value: 'ttfnid' },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  timetableFieldNumbers: TimetableFieldNumber[] = [];
  totalCount$ = 0;

  constructor(
    private trafficPointElementService: TrafficPointElementsService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  newTrafficPointElement() {
    this.router
      .navigate(['add'], {
        relativeTo: this.route,
      })
      .then();
  }

  getOverview(pagination: TablePagination) {
    const servicePointNumber = '8507000';
    this.trafficPointElementService
      .getTrafficPointElements(undefined, [servicePointNumber])
      .subscribe((container) => {
        const versions = container.objects!;
        const groupedVersions = new Map(versions.map((i) => [i.servicePointNumber.number, i]));
      });
  }

  editVersion($event: TimetableFieldNumber) {
    this.router
      .navigate([$event.ttfnid], {
        relativeTo: this.route,
      })
      .then();
  }
}
