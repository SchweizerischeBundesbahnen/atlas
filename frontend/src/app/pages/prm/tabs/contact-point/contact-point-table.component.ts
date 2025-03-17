import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {BasePrmTabComponentService} from '../base-prm-tab-component.service';
import {PrmTabs} from '../../prm-panel/prm-tabs';
import {Tab} from '../../../tab';
import {TableColumn} from "../../../../core/components/table/table-column";
import {TableFilter} from "../../../../core/components/table-filter/config/table-filter";
import {ContactPointOverview, PersonWithReducedMobilityService} from "../../../../api";
import {TableService} from "../../../../core/components/table/table.service";
import {Pages} from "../../../pages";
import {TablePagination} from "../../../../core/components/table/table-pagination";
import {TableContentPaginationAndSorting} from "../../../../core/components/table/table-content-pagination-and-sorting";

@Component({
    selector: 'app-contact-point-table',
    templateUrl: './contact-point-table.component.html',
    standalone: false
})
export class ContactPointTableComponent extends BasePrmTabComponentService implements OnInit {

  tableColumns: TableColumn<ContactPointOverview>[] = [
    { headerTitle: 'PRM.CONTACT_POINTS.DESIGNATION', value: 'designation' },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    { headerTitle: 'PRM.CONTACT_POINTS.TYPE', value: 'type',translate: { withPrefix: 'PRM.CONTACT_POINTS.TYPES.' } },
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
  contactPoints: ContactPointOverview[] = [];

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
    this.tableFilterConfig = this.tableService.initializeFilterConfig({}, Pages.CONTACT_POINT);
  }

  getTab(): Tab {
    return PrmTabs.CONTACT_POINT;
  }

  getOverview(pagination: TablePagination) {
    const parentServicePointSloid = this.route.parent!.snapshot.params.stopPointSloid!;

    this.personWithReducedMobilityService
      .getContactPointOverview(parentServicePointSloid)
      .subscribe((overviewRows) => {
        this.contactPoints = TableContentPaginationAndSorting.pageAndSort(
          overviewRows,
          pagination,
          'designation,asc',
        );
        this.totalCount = overviewRows.length;
      });
  }

  rowClicked(clickedRow: ContactPointOverview) {
    this.router.navigate([clickedRow.sloid], { relativeTo: this.route }).then();
  }

  new() {
    this.router.navigate(['add'], { relativeTo: this.route }).then();
  }

}
