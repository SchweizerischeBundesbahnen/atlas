import { Component, inject } from '@angular/core';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TranslatePipe } from '@ngx-translate/core';
import { TableColumn } from '../../../../core/components/table/table-column';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { Observable, of } from 'rxjs';
import { ContainerTerminationStopPointWorkflowModel } from '../../../../api/model/containerTerminationStopPointWorkflowModel';
import { TerminationStopPointWorkflowModel } from '../../../../api/model/terminationStopPointWorkflowModel';
import { WorkflowService } from '../../../../api/service/workflow/workflow.service';
import { TableService } from '../../../../core/components/table/table.service';
import { Pages } from '../../../pages';
import { TableFilterChip } from '../../../../core/components/table-filter/config/table-filter-chip';
import { TableFilterSingleSearch } from '../../../../core/components/table-filter/config/table-filter-single-search';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { TableFilterMultiSelect } from '../../../../core/components/table-filter/config/table-filter-multiselect';
import { BusinessOrganisation } from '../../../../api';
import { TableFilterSearchSelect } from '../../../../core/components/table-filter/config/table-filter-search-select';
import { TableFilterSearchType } from '../../../../core/components/table-filter/config/table-filter-search-type';
import { FormControl, FormGroup } from '@angular/forms';
import { TerminationWorkflowStatus } from '../../../../api/model/terminationWorkflowStatus';
import { addElementsToArrayWhenNotUndefined } from '../../../../core/util/arrays';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'stop-point-termination-workflow-overview',
  templateUrl: './stop-point-termination-workflow-overview.component.html',
  imports: [TableComponent, TranslatePipe, AsyncPipe],
})
export class StopPointTerminationWorkflowOverviewComponent {
  private readonly workflowService = inject(WorkflowService);
  private readonly tableService = inject(TableService);

  protected workflows$: Observable<ContainerTerminationStopPointWorkflowModel> =
    of();

  protected readonly tableColumns: TableColumn<TerminationStopPointWorkflowModel>[] =
    [
      { headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.ID', value: 'id' },
      {
        headerTitle: 'COMMON.STATUS',
        value: 'status',
        translate: { withPrefix: 'TERMINATION_WORKFLOW.STATUS.' },
      },
      {
        headerTitle: 'SEPODI.SERVICE_POINTS.DESIGNATION_OFFICIAL',
        value: 'designationOfficial',
      },
      { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
      {
        headerTitle:
          'TERMINATION_WORKFLOW.OVERVIEW_TABLE_COLUMNS.TRANSPORT_COMPANY_DATE',
        value: 'boTerminationDate',
        formatAsDate: true,
      },
      {
        headerTitle: 'TERMINATION_WORKFLOW.OVERVIEW_TABLE_COLUMNS.INFO+_DATE',
        value: 'infoPlusTerminationDate',
        formatAsDate: true,
      },
      {
        headerTitle: 'TERMINATION_WORKFLOW.OVERVIEW_TABLE_COLUMNS.NOVA_DATE',
        value: 'novaTerminationDate',
        formatAsDate: true,
      },
    ];

  private readonly tableFilterConfigIntern = {
    search: new TableFilterChip(
      0,
      'col-6',
      'SEPODI.SERVICE_POINTS.WORKFLOW.SEARCH'
    ),
    workflowIds: new TableFilterSingleSearch(
      1,
      'SEPODI.SERVICE_POINTS.WORKFLOW.ID',
      'col-4',
      AtlasCharsetsValidator.numeric
    ),
    workflowStatus: new TableFilterMultiSelect(
      'TERMINATION_WORKFLOW.STATUS.',
      'WORKFLOW.STATUS_DETAIL',
      [
        TerminationWorkflowStatus.Started,
        TerminationWorkflowStatus.TerminationApproved,
        TerminationWorkflowStatus.TerminationNotApproved,
        TerminationWorkflowStatus.Canceled,
        TerminationWorkflowStatus.TerminationNotApprovedClosed,
        TerminationWorkflowStatus.TariffStopApproved,
        TerminationWorkflowStatus.TariffStopNotApproved,
      ],
      1,
      'col-4'
    ),
    sboid: new TableFilterSearchSelect<BusinessOrganisation>(
      TableFilterSearchType.BUSINESS_ORGANISATION,
      1,
      'col-4',
      new FormGroup({
        businessOrganisation: new FormControl(),
      })
    ),
  };

  protected readonly tableFilterConfig: TableFilter<unknown>[][] =
    this.tableService.initializeFilterConfig(
      this.tableFilterConfigIntern,
      Pages.TERMINATION_STOP_POINT_WORKFLOWS
    );

  protected onRowClick(element: TerminationStopPointWorkflowModel) {
    // todo: route to
    console.log(element);
  }

  protected loadWorkflows(pagination: TablePagination) {
    this.workflows$ = this.workflowService.getTerminationStopPointWorkflows(
      this.tableService.filter.search.getActiveSearch(),
      addElementsToArrayWhenNotUndefined(
        this.tableService.filter.sboid.getActiveSearch()?.sboid
      ),
      addElementsToArrayWhenNotUndefined(
        this.tableService.filter.workflowIds.getActiveSearch()
      ),
      this.tableService.filter.workflowStatus.getActiveSearch(),
      pagination.page,
      pagination.size,
      addElementsToArrayWhenNotUndefined(pagination.sort, 'id,desc')
    );
  }
}
