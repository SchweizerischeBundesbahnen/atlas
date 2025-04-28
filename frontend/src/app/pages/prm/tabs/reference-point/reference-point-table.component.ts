import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {BasePrmTabComponentService} from '../base-prm-tab-component.service';
import {PrmTabs} from '../../prm-panel/prm-tabs';
import {Tab} from '../../../tab';
import {PersonWithReducedMobilityService, ReadReferencePointVersion} from '../../../../api';
import {TableService} from '../../../../core/components/table/table.service';
import {Pages} from '../../../pages';
import {TableFilter} from '../../../../core/components/table-filter/config/table-filter';
import {TablePagination} from '../../../../core/components/table/table-pagination';
import {TableColumn} from '../../../../core/components/table/table-column';
import {TableContentPaginationAndSorting} from "../../../../core/components/table/table-content-pagination-and-sorting";

@Component({
    selector: 'app-reference-point-table',
    templateUrl: './reference-point-table.component.html',
    standalone: false
})
export class ReferencePointTableComponent extends BasePrmTabComponentService implements OnInit {
  tableColumns: TableColumn<ReadReferencePointVersion>[] = [
    { headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION', value: 'designation' },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];
  tableFilterConfig!: TableFilter<unknown>[][];

  totalCount = 0;
  referencePoints: ReadReferencePointVersion[] = [];

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
    this.tableFilterConfig = this.tableService.initializeFilterConfig({}, Pages.REFERENCE_POINT);
  }

  getTab(): Tab {
    return PrmTabs.REFERENCE_POINT;
  }

  getOverview(pagination: TablePagination) {
    const parentServicePointSloid = this.route.parent!.snapshot.params.stopPointSloid!;

    this.personWithReducedMobilityService
      .getReferencePointsOverview(parentServicePointSloid)
      .subscribe((overviewRows) => {
        this.referencePoints = TableContentPaginationAndSorting.pageAndSort(
          overviewRows,
          pagination,
          'designation,asc',
        );
        this.totalCount = overviewRows.length;
      });
  }

  rowClicked(clickedRow: ReadReferencePointVersion) {
    this.router.navigate([clickedRow.sloid], { relativeTo: this.route }).then();
  }

  new() {
    this.router.navigate(['add'], { relativeTo: this.route }).then();
  }
}
