import {Component, OnInit} from '@angular/core';
import {TableFilterChip} from "../../../../core/components/table-filter/config/table-filter-chip";
import {TableFilterSearchSelect} from "../../../../core/components/table-filter/config/table-filter-search-select";
import {
  ApplicationType,
  BusinessOrganisation,
  ReadStopPointWorkflow,
  StopPointWorkflowService,
  WorkflowStatus
} from "../../../../api";
import {TableFilterSearchType} from "../../../../core/components/table-filter/config/table-filter-search-type";
import {FormControl, FormGroup} from "@angular/forms";
import {TableFilterMultiSelect} from "../../../../core/components/table-filter/config/table-filter-multiselect";
import {TableFilter} from "../../../../core/components/table-filter/config/table-filter";
import {TableColumn} from "../../../../core/components/table/table-column";
import {Pages} from "../../../pages";
import {ActivatedRoute, Router} from "@angular/router";
import {TableService} from "../../../../core/components/table/table.service";
import {TablePagination} from "../../../../core/components/table/table-pagination";
import {addElementsToArrayWhenNotUndefined} from "../../../../core/util/arrays";
import {TableFilterSingleSearch} from "../../../../core/components/table-filter/config/table-filter-single-search";
import {AtlasCharsetsValidator} from "../../../../core/validation/charsets/atlas-charsets-validator";
import {TableFilterBoolean} from "../../../../core/components/table-filter/config/table-filter-boolean";
import {PermissionService} from "../../../../core/auth/permission/permission.service";

@Component({
  selector: 'stop-point-workflow-overview',
  templateUrl: './stop-point-workflow-overview.component.html',
})
export class StopPointWorkflowOverviewComponent implements OnInit {

  private readonly tableFilterConfigIntern = {
    search: new TableFilterChip(0, 'col-6', 'SEPODI.SERVICE_POINTS.WORKFLOW.SEARCH'),
    // filterByNoDecision: new TableFilterBoolean(0, 'col-3 ps-3 sanja', 'SEPODI.SERVICE_POINTS.WORKFLOW.SLIDE'),
    // filterByNoDecision: new TableFilterBoolean(0, 'container d-flex ms-auto col-3', 'SEPODI.SERVICE_POINTS.WORKFLOW.SLIDE'),
    // filterByNoDecision: new TableFilterBoolean(0, 'container d-flex justify-content-end col-3', 'SEPODI.SERVICE_POINTS.WORKFLOW.SLIDE'),
    filterByNoDecision: new TableFilterBoolean(0, 'container d-flex ml-auto col-3', 'SEPODI.SERVICE_POINTS.WORKFLOW.SLIDE'),
    workflowIds: new TableFilterSingleSearch(1, 'SEPODI.SERVICE_POINTS.WORKFLOW.ID','col-3', AtlasCharsetsValidator.numeric),
    workflowStatus: new TableFilterMultiSelect(
      'WORKFLOW.STATUS.',
      'WORKFLOW.STATUS_DETAIL',
      [WorkflowStatus.Added, WorkflowStatus.Hearing, WorkflowStatus.Approved, WorkflowStatus.Rejected, WorkflowStatus.Canceled],
      1,
      'col-3',
      [WorkflowStatus.Added, WorkflowStatus.Hearing, WorkflowStatus.Approved, WorkflowStatus.Rejected, WorkflowStatus.Canceled]
    ),
    sboid: new TableFilterSearchSelect<BusinessOrganisation>(
      TableFilterSearchType.BUSINESS_ORGANISATION,
      1,
      'col-3',
      new FormGroup({
        businessOrganisation: new FormControl(),
      })
    ),
    locality: new TableFilterSingleSearch(1, 'SEPODI.GEOLOCATION.DISTRICT','col-3 pb-5'),
  };

  tableFilterConfig!: TableFilter<unknown>[][];
  isAtLeastSupervisor!: boolean;

  tableColumns: TableColumn<ReadStopPointWorkflow>[] = [
    {headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.ID', value: 'id'},
    {headerTitle: 'COMMON.STATUS', value: 'status', translate: { withPrefix: 'WORKFLOW.STATUS.' },},
    {headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid'},
    {headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.NEW_DESIGNATION_OFFICIAL', value: 'designationOfficial'},
    {headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.VERSION_VALID_FROM', value: 'versionValidFrom', formatAsDate: true},
    {headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.CREATION', value: 'creationDate', formatAsDate: true},
    {headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.START', value: 'startDate', formatAsDate: true},
    {headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.END', value: 'endDate', formatAsDate: true},
  ];

  stopPointWorkflows: ReadStopPointWorkflow[] = [];
  totalCount$ = 0;

  constructor(
    private stopPointWorkflowService: StopPointWorkflowService,
    private route: ActivatedRoute,
    private router: Router,
    private tableService: TableService,
    private permissionService: PermissionService,
  ) {
  }

  ngOnInit() {
    this.isAtLeastSupervisor = this.permissionService.isAtLeastSupervisor(ApplicationType.Sepodi);
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      this.tableFilterConfigIntern,
      Pages.SERVICE_POINT_WORKFLOWS
    );
  }

  getOverview(pagination: TablePagination) {
    this.stopPointWorkflowService.getStopPointWorkflows(
      this.tableService.filter.search.getActiveSearch(),
      [this.tableService.filter.workflowIds.getActiveSearch()],
      this.tableService.filter.workflowStatus.getActiveSearch(),
      undefined,
      undefined,
      this.tableService.filter.locality.getActiveSearch(),
      [this.tableService.filter.sboid.getActiveSearch()?.sboid],
      undefined,
      undefined,
      this.tableService.filter.filterByNoDecision.getActiveSearch(),
      pagination.page,
      pagination.size,
      addElementsToArrayWhenNotUndefined(pagination.sort, 'id,asc')
    )
      .subscribe((container) => {
        this.stopPointWorkflows = container.objects!;
        this.totalCount$ = container.totalCount!;
      });
  }

  edit(workflow: ReadStopPointWorkflow) {
    this.router.navigate([workflow.id], {relativeTo: this.route}).then();
  }

}
