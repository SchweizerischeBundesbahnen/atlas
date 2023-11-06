import { Component } from '@angular/core';
import { TableColumn } from '../../../../core/components/table/table-column';
import { ReadTrafficPointElementVersion, TrafficPointElementsService } from '../../../../api';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { ActivatedRoute, Router } from '@angular/router';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import { Pages } from '../../../pages';

@Component({
  selector: 'app-service-point-traffic-point-elements-table',
  templateUrl: './traffic-point-elements-table.component.html',
  styleUrls: ['./traffic-point-elements-table.component.scss'],
})
export class TrafficPointElementsTableComponent {
  tableColumns: TableColumn<ReadTrafficPointElementVersion>[] = [
    { headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION', value: 'designation' },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    {
      headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION_OPERATIONAL',
      value: 'designationOperational',
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  trafficPointElementRows: ReadTrafficPointElementVersion[] = [];
  totalCount$ = 0;

  constructor(
    private trafficPointElementService: TrafficPointElementsService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  getOverview(pagination: TablePagination) {
    this.trafficPointElementService
      .getPlatformsOfServicePoint(this.servicePointNumber, pagination.page, pagination.size, [
        pagination.sort ?? 'designation,asc',
      ])
      .subscribe((container) => {
        this.trafficPointElementRows = container.objects!;
        this.totalCount$ = container.totalCount!;
      });
  }

  private groupDisplayRows(
    versions: ReadTrafficPointElementVersion[],
  ): ReadTrafficPointElementVersion[] {
    const trafficPointRows: ReadTrafficPointElementVersion[] = [];
    const map = VersionsHandlingService.groupVersionsByKey(versions, 'sloid');

    Object.values(map).forEach((value) => {
      const maxValidity = VersionsHandlingService.getMaxValidity(value);
      const rowToDisplay = VersionsHandlingService.determineDefaultVersionByValidity(
        value,
      ) as ReadTrafficPointElementVersion;
      rowToDisplay.validFrom = maxValidity.validFrom;
      rowToDisplay.validTo = maxValidity.validTo;

      trafficPointRows.push(rowToDisplay);
    });

    return trafficPointRows;
  }

  newTrafficPointElement() {
    this.router
      .navigate([Pages.SEPODI.path, Pages.TRAFFIC_POINT_ELEMENTS.path, 'add'], {
        state: { servicePointNumber: this.servicePointNumber },
      })
      .then();
  }

  editVersion($event: ReadTrafficPointElementVersion) {
    this.router
      .navigate([Pages.SEPODI.path, Pages.TRAFFIC_POINT_ELEMENTS.path, $event.sloid], {
        state: { servicePointNumber: this.servicePointNumber },
      })
      .then();
  }

  get servicePointNumber() {
    return this.route.parent!.snapshot.params['id'];
  }

  closeSidePanel() {
    this.router.navigate([Pages.SEPODI.path]).then();
  }
}
