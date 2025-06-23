import { Component, inject, Signal } from '@angular/core';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TranslatePipe } from '@ngx-translate/core';
import { TableColumn } from '../../../../core/components/table/table-column';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { Subject, switchMap } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { ContainerTerminationStopPointWorkflowModel } from '../../../../api/model/containerTerminationStopPointWorkflowModel';
import { TerminationStopPointWorkflowModel } from '../../../../api/model/terminationStopPointWorkflowModel';
import { WorkflowService } from '../../../../api/service/workflow/workflow.service';

@Component({
  selector: 'stop-point-termination-workflow-overview',
  templateUrl: './stop-point-termination-workflow-overview.component.html',
  imports: [TableComponent, TranslatePipe],
})
export class StopPointTerminationWorkflowOverviewComponent {
  private readonly workflowService = inject(WorkflowService);

  private readonly trigger$ = new Subject<{
    searchCriteria: string[];
  }>();

  protected readonly workflows: Signal<
    ContainerTerminationStopPointWorkflowModel | undefined
  > = toSignal(
    this.trigger$.pipe(
      switchMap(({ searchCriteria }) => {
        return this.workflowService.getTerminationStopPointWorkflows(
          searchCriteria
        );
      })
    )
  );

  protected readonly tableColumns: TableColumn<TerminationStopPointWorkflowModel>[] =
    [
      { headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.ID', value: 'id' },
      {
        headerTitle: 'COMMON.STATUS',
        value: 'status',
        translate: { withPrefix: '' }, // todo
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

  protected readonly tableFilterConfig: TableFilter<unknown>[][] = [];

  protected onRowClick(element: TerminationStopPointWorkflowModel) {}

  protected loadWorkflows(pagination: TablePagination) {
    this.trigger$.next({
      searchCriteria: ['test'],
    });
  }
}
