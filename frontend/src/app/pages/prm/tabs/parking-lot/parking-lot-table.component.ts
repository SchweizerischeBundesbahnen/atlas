import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {BasePrmTabComponentService} from '../base-prm-tab-component.service';
import {PrmTabs} from '../../prm-panel/prm-tabs';
import {Tab} from '../../../tab';
import {TableService} from '../../../../core/components/table/table.service';
import {Pages} from '../../../pages';
import {TableFilter} from '../../../../core/components/table-filter/config/table-filter';
import {TablePagination} from '../../../../core/components/table/table-pagination';
import {TableColumn} from '../../../../core/components/table/table-column';
import {ParkingLotOverview, PersonWithReducedMobilityService} from "../../../../api";
import {TableContentPaginationAndSorting} from "../../../../core/components/table/table-content-pagination-and-sorting";
import { NgIf } from '@angular/common';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TableComponent } from '../../../../core/components/table/table.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';

@Component({
    selector: 'app-parking-lot-table',
    templateUrl: './parking-lot-table.component.html',
    imports: [NgIf, AtlasButtonComponent, TableComponent, DetailFooterComponent]
})
export class ParkingLotTableComponent extends BasePrmTabComponentService implements OnInit {
  tableColumns: TableColumn<ParkingLotOverview>[] = [
    { headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION', value: 'designation' },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
    {
      headerTitle: 'PRM.PLATFORMS.RECORDING_STATUS',
      value: 'recordingStatus',
      translate: { withPrefix: 'PRM.PLATFORMS.RECORDINGSTATUS.' },
    },
  ];
  tableFilterConfig!: TableFilter<unknown>[][];

  totalCount = 0;
  parkingLots: ParkingLotOverview[] = [];

  constructor(
    protected readonly router: Router,
    private route: ActivatedRoute,
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private tableService: TableService,
  ) {
    super(router);
  }

  ngOnInit(): void {
    this.showCurrentTab(this.route.parent!.snapshot.data);
    this.tableFilterConfig = this.tableService.initializeFilterConfig({}, Pages.PARKING_LOT);
  }

  getTab(): Tab {
    return PrmTabs.PARKING_LOT;
  }

  getOverview(pagination: TablePagination) {
    const parentServicePointSloid = this.route.parent!.snapshot.params.stopPointSloid!;

    this.personWithReducedMobilityService
      .getParkingLotsOverview(parentServicePointSloid)
      .subscribe((overviewRows) => {
        this.parkingLots = TableContentPaginationAndSorting.pageAndSort(
          overviewRows,
          pagination,
          'designation,asc',
        );
        this.totalCount = overviewRows.length;
      });
  }

  rowClicked(clickedRow: ParkingLotOverview) {
    this.router.navigate([clickedRow.sloid], { relativeTo: this.route }).then();
  }

  new() {
    this.router.navigate(['add'], { relativeTo: this.route }).then();
  }
}
