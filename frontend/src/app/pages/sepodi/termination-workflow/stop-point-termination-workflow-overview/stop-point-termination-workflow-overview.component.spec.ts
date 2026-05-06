import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { StopPointTerminationWorkflowOverviewComponent } from './stop-point-termination-workflow-overview.component';
import { Component, input, output } from '@angular/core';
import { By } from '@angular/platform-browser';
import { TerminationStopPointWorkflowModel } from '../../../../api/model/terminationStopPointWorkflowModel';
import { StopPointTerminationWorkflowService } from '../../../../api/service/workflow/stop-point-termination-workflow.service';
import { of, Subject } from 'rxjs';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TranslateModule } from '@ngx-translate/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'atlas-table',
  template: 'MockTableComponent',
})
class MockTableComponent {
  readonly tableInitialized = output<TablePagination>();
  readonly tableChanged = output<TablePagination>();
  readonly tableData = input<TerminationStopPointWorkflowModel[]>();
  readonly totalCount = input();
  readonly tableColumns = input();
  readonly tableFilterConfig = input();
}

describe('StopPointTerminationWorkflowOverviewComponent', () => {
  let fixture: ComponentFixture<StopPointTerminationWorkflowOverviewComponent>;

  let wfServiceMock: Mocked<Pick<StopPointTerminationWorkflowService, 'getTerminationStopPointWorkflows'>>;

  beforeEach(async () => {
    wfServiceMock = {
      getTerminationStopPointWorkflows: vi.fn().mockReturnValue(
        of({
          objects: [
            {
              sloid: 'ch:1:sloid:1',
            } as TerminationStopPointWorkflowModel,
          ],
          totalCount: 1,
        })
      ),
    };

    await TestBed.configureTestingModule({
      imports: [StopPointTerminationWorkflowOverviewComponent, TranslateModule.forRoot()],
      providers: [
        {
          provide: StopPointTerminationWorkflowService,
          useValue: wfServiceMock,
        },
        { provide: ActivatedRoute, useValue: { queryParam: new Subject() } },
      ],
    })
      .overrideComponent(StopPointTerminationWorkflowOverviewComponent, {
        remove: { imports: [TableComponent] },
        add: { imports: [MockTableComponent] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(StopPointTerminationWorkflowOverviewComponent);
    fixture.detectChanges();
  });

  it('should display initial table data', () => {
    // given
    const mockTableComp: MockTableComponent = fixture.debugElement.query(By.css('atlas-table')).componentInstance;
    // when
    mockTableComp.tableInitialized.emit({
      page: 0,
      size: 10,
    });
    fixture.detectChanges();
    // then
    expect(mockTableComp.tableData()?.[0]?.sloid).toEqual('ch:1:sloid:1');
    expect(mockTableComp.totalCount()).toEqual(1);
    expect(mockTableComp.tableColumns()).toBeDefined();
    expect(mockTableComp.tableFilterConfig()).toBeDefined();
  });

  it('should update table data and count on user input', () => {
    // given
    wfServiceMock.getTerminationStopPointWorkflows.mockReturnValue(
      of({
        objects: [
          {
            sloid: 'ch:1:sloid:5',
          } as TerminationStopPointWorkflowModel,
          {
            sloid: 'ch:1:sloid:50',
          } as TerminationStopPointWorkflowModel,
        ],
        totalCount: 2,
      })
    );
    const mockTableComp: MockTableComponent = fixture.debugElement.query(By.css('atlas-table')).componentInstance;
    // when
    mockTableComp.tableChanged.emit({
      page: 0,
      size: 10,
    });
    fixture.detectChanges();
    // then
    expect(mockTableComp.tableData()?.[0]?.sloid).toEqual('ch:1:sloid:5');
    expect(mockTableComp.totalCount()).toEqual(2);
  });
});
