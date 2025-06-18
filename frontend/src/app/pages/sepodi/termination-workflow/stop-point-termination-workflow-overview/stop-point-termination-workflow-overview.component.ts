import { Component, Signal } from '@angular/core';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TranslatePipe } from '@ngx-translate/core';
import { TableColumn } from '../../../../core/components/table/table-column';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { of, Subject, switchMap } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';

type test = {
  elements: any[];
  totalCount: number;
};

@Component({
  selector: 'stop-point-termination-workflow-overview',
  templateUrl: './stop-point-termination-workflow-overview.component.html',
  imports: [TableComponent, TranslatePipe],
})
export class StopPointTerminationWorkflowOverviewComponent {
  private trigger$ = new Subject<{
    searchCriteria: string[];
  }>();

  readonly workflows: Signal<test> = toSignal(
    this.trigger$.pipe(
      switchMap((triggerEvent) => {
        console.log(triggerEvent); // todo: use for filter request
        return of({
          elements: [],
          totalCount: 0,
        } as test);
      })
    ),
    {
      initialValue: {
        elements: [],
        totalCount: 0,
      },
    }
  );

  readonly tableColumns: TableColumn<any>[] = // todo: generate model
    [
      { headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.ID', value: 'id' },
      {
        headerTitle: 'COMMON.STATUS',
        value: 'status',
        translate: { withPrefix: 'WORKFLOW.STATUS.' },
      },
      { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
      {
        headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.NEW_DESIGNATION_OFFICIAL',
        value: 'designationOfficial',
      },
      {
        headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.VERSION_VALID_FROM',
        value: 'versionValidFrom',
        formatAsDate: true,
      },
      {
        headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.CREATION',
        value: 'creationDate',
        formatAsDate: true,
      },
      {
        headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.START',
        value: 'startDate',
        formatAsDate: true,
      },
      {
        headerTitle: 'SEPODI.SERVICE_POINTS.WORKFLOW.END',
        value: 'endDate',
        formatAsDate: true,
      },
    ];

  readonly tableFilterConfig: TableFilter<unknown>[][] = [];

  onRowClick(element: any) {} // todo: use model type

  loadWorkflows(pagination: TablePagination) {
    this.trigger$.next({
      searchCriteria: ['test'],
    });
  }
}
