import { Component } from '@angular/core';
import { TableColumn } from '../../../../core/components/table/table-column';
import { ReadTrafficPointElementVersionModel, TrafficPointElementsService } from '../../../../api';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { ActivatedRoute, Router } from '@angular/router';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import { Pages } from '../../../pages';
import { naturalCompare } from '../../../../core/util/sorting';

@Component({
  selector: 'app-service-point-traffic-point-elements-table',
  templateUrl: './traffic-point-elements-table.component.html',
  styleUrls: ['./traffic-point-elements-table.component.scss'],
})
export class TrafficPointElementsTableComponent {
  tableColumns: TableColumn<ReadTrafficPointElementVersionModel>[] = [
    { headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION', value: 'designation' },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    {
      headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION_OPERATIONAL',
      value: 'designationOperational',
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  trafficPointElementRows: ReadTrafficPointElementVersionModel[] = [];
  totalCount$ = 0;

  constructor(
    private trafficPointElementService: TrafficPointElementsService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  getOverview(pagination: TablePagination) {
    const servicePointNumber = this.route.parent!.snapshot.params['id'];
    this.trafficPointElementService
      .getTrafficPointElements(
        undefined,
        [servicePointNumber],
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        500,
        ['sloid,asc'],
      )
      .subscribe((container) => {
        const versions = container.objects!;
        const trafficPointRows = this.groupDisplayRows(versions);

        trafficPointRows.sort((a: any, b: any) => {
          const sort =
            pagination.sort?.substring(0, pagination.sort?.indexOf(',')) || 'designation';
          const direction = (pagination.sort?.substring(pagination.sort?.indexOf(',') + 1) ||
            'asc') as 'asc' | 'desc';

          return naturalCompare(a[sort], b[sort], direction);
        });

        const start = pagination.page * pagination.size;
        const end = start + pagination.size;
        this.trafficPointElementRows = trafficPointRows.slice(start, end);
        this.totalCount$ = trafficPointRows.length;
      });
  }

  private groupDisplayRows(
    versions: ReadTrafficPointElementVersionModel[],
  ): ReadTrafficPointElementVersionModel[] {
    const trafficPointRows: ReadTrafficPointElementVersionModel[] = [];
    const map = VersionsHandlingService.groupVersionsByKey(versions, 'sloid');

    Object.values(map).forEach((value) => {
      const maxValidity = VersionsHandlingService.getMaxValidity(value);
      const rowToDisplay = VersionsHandlingService.determineDefaultVersionByValidity(
        value,
      ) as ReadTrafficPointElementVersionModel;
      rowToDisplay.validFrom = maxValidity.validFrom;
      rowToDisplay.validTo = maxValidity.validTo;

      trafficPointRows.push(rowToDisplay);
    });

    return trafficPointRows;
  }

  newTrafficPointElement() {
    this.router.navigate([Pages.SEPODI.path, Pages.TRAFFIC_POINT_ELEMENTS.path, 'add']).then();
  }

  editVersion($event: ReadTrafficPointElementVersionModel) {
    this.router
      .navigate([Pages.SEPODI.path, Pages.TRAFFIC_POINT_ELEMENTS.path, $event.sloid])
      .then();
  }
}
