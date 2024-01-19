import { Component, OnInit } from '@angular/core';
import { TableColumn } from '../../../../core/components/table/table-column';
import {
  ReadServicePointVersion,
  ReadTrafficPointElementVersion,
  TrafficPointElementsService,
} from '../../../../api';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../../pages';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { TableService } from '../../../../core/components/table/table.service';

@Component({
  selector: 'app-service-point-traffic-point-elements-table',
  templateUrl: './traffic-point-elements-table.component.html',
  styleUrls: ['./traffic-point-elements-table.component.scss'],
})
export class TrafficPointElementsTableComponent implements OnInit {
  tableColumnsPlatforms: TableColumn<ReadTrafficPointElementVersion>[] = [
    { headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION', value: 'designation' },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    {
      headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION_OPERATIONAL',
      value: 'designationOperational',
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  tableColumnsAreas: TableColumn<ReadTrafficPointElementVersion>[] = [
    { headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION', value: 'designation' },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  trafficPointElementRows: ReadTrafficPointElementVersion[] = [];
  totalCount$ = 0;
  isTrafficPointArea = false;
  createVisible = false;

  tableFilterConfig!: TableFilter<unknown>[][];

  constructor(
    private trafficPointElementService: TrafficPointElementsService,
    private tableService: TableService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe((next) => {
      this.isTrafficPointArea = next.isTrafficPointArea;
      this.createVisible = this.isParentStopPoint;
      this.tableFilterConfig = this.tableService.initializeFilterConfig(
        {},
        this.isTrafficPointArea
          ? Pages.TRAFFIC_POINT_ELEMENTS_AREA
          : Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM,
      );
    });
  }

  getOverview(pagination: TablePagination) {
    this.getTrafficPointElements(pagination, this.isTrafficPointArea);
  }

  addNewTrafficPointElement() {
    this.router
      .navigate([Pages.SEPODI.path, Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path, 'add'], {
        state: {
          servicePointNumber: this.servicePointNumber,
          isTrafficPointArea: this.isTrafficPointArea,
        },
      })
      .then();
  }

  editVersion($event: ReadTrafficPointElementVersion) {
    this.router
      .navigate([Pages.SEPODI.path, Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path, $event.sloid], {
        state: {
          servicePointNumber: this.servicePointNumber,
          isTrafficPointArea: this.isTrafficPointArea,
        },
      })
      .then();
  }

  get servicePointNumber() {
    return this.route.parent!.snapshot.params['id'];
  }

  get isParentStopPoint(): boolean {
    const servicePointVersions: ReadServicePointVersion[] =
      this.route.parent!.snapshot.data.servicePoint;
    return servicePointVersions.filter((sp) => sp.stopPoint).length > 0;
  }

  closeSidePanel() {
    this.router.navigate([Pages.SEPODI.path]).then();
  }

  getTrafficPointElements(pagination: TablePagination, isArea: boolean) {
    const getEndpoint = isArea ? 'getAreasOfServicePoint' : 'getPlatformsOfServicePoint';

    this.trafficPointElementService[getEndpoint](
      this.servicePointNumber,
      pagination.page,
      pagination.size,
      [pagination.sort ?? 'designation,asc'],
    ).subscribe((container) => {
      this.trafficPointElementRows = container.objects!;
      this.totalCount$ = container.totalCount!;
    });
  }
}
