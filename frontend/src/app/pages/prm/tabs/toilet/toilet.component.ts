import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BasePrmTabComponentService } from '../base-prm-tab-component.service';
import { PrmTabs } from '../../prm-panel/prm-tabs';
import { Tab } from '../../../tab';
import { TableColumn } from '../../../../core/components/table/table-column';
import {
  PersonWithReducedMobilityService,
  ToiletOverview,
} from '../../../../api';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { TableService } from '../../../../core/components/table/table.service';
import { Pages } from '../../../pages';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { TableContentPaginationAndSorting } from '../../../../core/components/table/table-content-pagination-and-sorting';
import { NgIf } from '@angular/common';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TableComponent } from '../../../../core/components/table/table.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';

@Component({
  selector: 'app-toilet',
  templateUrl: './toilet.component.html',
  imports: [NgIf, AtlasButtonComponent, TableComponent, DetailFooterComponent],
})
export class ToiletComponent
  extends BasePrmTabComponentService
  implements OnInit
{
  tableColumns: TableColumn<ToiletOverview>[] = [
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
    {
      headerTitle: 'PRM.PLATFORMS.RECORDING_STATUS',
      value: 'recordingStatus',
      translate: { withPrefix: 'PRM.PLATFORMS.RECORDINGSTATUS.' },
    },
  ];
  tableFilterConfig!: TableFilter<unknown>[][];

  totalCount = 0;
  toilets: ToiletOverview[] = [];
  constructor(
    readonly router: Router,
    private route: ActivatedRoute,
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private tableService: TableService
  ) {
    super(router);
  }
  ngOnInit(): void {
    this.showCurrentTab(this.route.parent!.snapshot.data);
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      {},
      Pages.TOILET
    );
  }

  getTab(): Tab {
    return PrmTabs.TOILET;
  }

  getOverview(pagination: TablePagination) {
    const parentServicePointSloid =
      this.route.parent!.snapshot.params.stopPointSloid!;

    this.personWithReducedMobilityService
      .getToiletOverview(parentServicePointSloid)
      .subscribe((overviewRows) => {
        this.toilets = TableContentPaginationAndSorting.pageAndSort(
          overviewRows,
          pagination,
          'designation,asc'
        );
        this.totalCount = overviewRows.length;
      });
  }

  rowClicked(clickedRow: ToiletOverview) {
    this.router.navigate([clickedRow.sloid], { relativeTo: this.route }).then();
  }

  new() {
    this.router.navigate(['add'], { relativeTo: this.route }).then();
  }
}
