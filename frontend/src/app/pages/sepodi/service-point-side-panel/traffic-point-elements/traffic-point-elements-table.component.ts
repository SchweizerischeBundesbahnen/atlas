import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { TableColumn } from '../../../../core/components/table/table-column';
import { ReadServicePointVersion, ReadTrafficPointElementVersion } from '../../../../api';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../../pages';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { TableService } from '../../../../core/components/table/table.service';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { NavigationSepodiPrmComponent } from '../../../../core/navigation-sepodi-prm/navigation-sepodi-prm.component';
import { TableComponent } from '../../../../core/components/table/table.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { TranslatePipe } from '@ngx-translate/core';
import { TrafficPointElementInternalService } from '../../../../api/service/sepodi/traffic-point-element-internal.service';
import { TrafficPointMapService } from '../../map/traffic-point-map.service';

@Component({
  selector: 'atlas-service-point-traffic-point-elements-table',
  templateUrl: './traffic-point-elements-table.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./traffic-point-elements-table.component.scss'],
  imports: [AtlasButtonComponent, NavigationSepodiPrmComponent, TableComponent, DetailFooterComponent, TranslatePipe],
})
export class TrafficPointElementsTableComponent implements OnInit, OnDestroy {
  private readonly trafficPointElementInternalService = inject(TrafficPointElementInternalService);
  private readonly trafficPointMapService = inject(TrafficPointMapService);
  private readonly tableService = inject(TableService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  tableColumnsPlatforms: TableColumn<ReadTrafficPointElementVersion>[] = [
    {
      headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION',
      value: 'designation',
    },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    {
      headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION_OPERATIONAL',
      value: 'designationOperational',
    },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  tableColumnsAreas: TableColumn<ReadTrafficPointElementVersion>[] = [
    {
      headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION',
      value: 'designation',
    },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  trafficPointElementRows: ReadTrafficPointElementVersion[] = [];
  totalCount$ = 0;
  isTrafficPointArea = false;
  createVisible = false;

  tableFilterConfig!: TableFilter<unknown>[][];
  servicePointVersion!: ReadServicePointVersion;
  servicePointBusinessOrganisations: string[] = [];

  ngOnInit(): void {
    this.route.data.subscribe((next) => {
      this.isTrafficPointArea = next.isTrafficPointArea;
      this.createVisible = this.isParentStopPoint;
      this.tableFilterConfig = this.tableService.initializeFilterConfig(
        {},
        this.isTrafficPointArea ? Pages.TRAFFIC_POINT_ELEMENTS_AREA : Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM
      );
      const servicePoints: ReadServicePointVersion[] = this.route.parent!.snapshot.data.servicePoint;
      this.servicePointVersion = servicePoints[servicePoints.length - 1];
      this.servicePointBusinessOrganisations = servicePoints.map((i) => i.businessOrganisation);
    });
  }

  getOverview(pagination: TablePagination) {
    this.getTrafficPointElements(pagination, this.isTrafficPointArea);
  }

  addNewTrafficPointElement() {
    this.router.navigate(['add'], { relativeTo: this.route }).then();
  }

  editVersion($event: ReadTrafficPointElementVersion) {
    this.router.navigate([$event.sloid], { relativeTo: this.route }).then();
  }

  onRowHovered(row: ReadTrafficPointElementVersion) {
    const coordinates = row.trafficPointElementGeolocation?.wgs84;
    if (coordinates) {
      this.trafficPointMapService.highlightTrafficPoint(coordinates);
    } else {
      this.trafficPointMapService.clearHighlightedTrafficPoint();
    }
  }

  onRowHoverEnded() {
    this.trafficPointMapService.clearHighlightedTrafficPoint();
  }

  ngOnDestroy(): void {
    this.trafficPointMapService.clearHighlightedTrafficPoint();
  }

  get servicePointNumber() {
    return this.route.parent!.snapshot.params['servicePointNumber'];
  }

  get isParentStopPoint(): boolean {
    const servicePointVersions: ReadServicePointVersion[] = this.route.parent!.snapshot.data.servicePoint;
    return servicePointVersions.filter((sp) => sp.stopPoint).length > 0;
  }

  closeSidePanel() {
    this.router.navigate([Pages.SEPODI.path]).then();
  }

  getTrafficPointElements(pagination: TablePagination, isArea: boolean) {
    const getEndpoint = isArea ? 'getAreasOfServicePoint' : 'getPlatformsOfServicePoint';

    this.trafficPointElementInternalService[getEndpoint](this.servicePointNumber, pagination.page, pagination.size, [
      pagination.sort ?? 'designation,asc',
    ]).subscribe((container) => {
      this.trafficPointElementRows = container.objects!;
      this.totalCount$ = container.totalCount!;
    });
  }
}
