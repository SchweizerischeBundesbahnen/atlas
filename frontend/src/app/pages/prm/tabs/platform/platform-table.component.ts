import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmTabComponentService } from '../base-prm-tab-component.service';
import { PrmTab } from '../../prm-panel/prm-tab';
import { Tab } from '../../../tab';
import {
  PersonWithReducedMobilityService,
  ReadTrafficPointElementVersion,
  TrafficPointElementsService,
} from '../../../../api';
import { PlatformOverviewRow } from './platform-overview-row';
import { TableColumn } from '../../../../core/components/table/table-column';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { Pages } from '../../../pages';
import { TableService } from '../../../../core/components/table/table.service';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { mergeMap } from 'rxjs';
import { servicePointSloidToNumber } from '../../../../core/util/sloidHelper';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-platform',
  templateUrl: './platform-table.component.html',
})
export class PlatformTableComponent extends BasePrmTabComponentService implements OnInit {
  platforms: PlatformOverviewRow[] = [];
  totalCount = 0;
  trafficPointElements: ReadTrafficPointElementVersion[] = [];

  tableColumns: TableColumn<PlatformOverviewRow>[] = [
    { headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION', value: 'designation' },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    {
      headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION_OPERATIONAL',
      value: 'designationOperational',
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
    {
      headerTitle: 'PRM.PLATFORMS.RECORDING_STATUS',
      value: 'completion',
      translate: { withPrefix: 'PRM.PLATFORMS.RECORDINGSTATUS.' },
    },
  ];
  tableFilterConfig!: TableFilter<unknown>[][];

  constructor(
    readonly router: Router,
    private route: ActivatedRoute,
    private tableService: TableService,
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private trafficPointElementsService: TrafficPointElementsService,
  ) {
    super(router);
  }

  ngOnInit(): void {
    this.showCurrentTab(this.route.parent!.snapshot.data);

    this.tableFilterConfig = this.tableService.initializeFilterConfig({}, Pages.PLATFORMS);
  }

  getTag(): Tab {
    return PrmTab.PLATFORM;
  }

  rowClicked(clickedRow: PlatformOverviewRow) {
    this.router.navigate([clickedRow.sloid], { relativeTo: this.route }).then();
  }

  getOverview(pagination: TablePagination) {
    const sloid = this.route.parent!.snapshot.params.stopPointSloid!;

    this.trafficPointElementsService
      .getPlatformsOfServicePoint(
        servicePointSloidToNumber(sloid),
        pagination.page,
        pagination.size,
        [pagination.sort ?? 'designation,asc'],
      )
      .pipe(
        tap((sepodiPlatforms) => {
          this.trafficPointElements = sepodiPlatforms.objects!;
          this.totalCount = sepodiPlatforms.totalCount!;
        }),
        mergeMap(() => this.personWithReducedMobilityService.getPlatformOverview(sloid)),
      )
      .subscribe((platforms) => {
        const platformOverview: PlatformOverviewRow[] = [];
        this.trafficPointElements.forEach((trafficPointElement) => {
          const correspondingPrmPlatform = platforms.find(
            (i) => i.sloid === trafficPointElement.sloid!,
          );
          platformOverview.push({
            designation: trafficPointElement.designation,
            designationOperational: trafficPointElement.designationOperational,
            sloid: trafficPointElement.sloid!,
            validFrom: correspondingPrmPlatform?.validFrom,
            validTo: correspondingPrmPlatform?.validTo,
            completion: correspondingPrmPlatform?.recordingStatus || 'NOT_STARTED',
          });
        });

        this.platforms = platformOverview;
      });
  }
}
