import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { StopPointWorkflowOverviewComponent } from './stop-point-workflow-overview.component';
import { MockTableComponent, translateServiceProvider } from '../../../../app.testing.mocks';
import { ContainerReadStopPointWorkflow, ReadStopPointWorkflow } from '../../../../api';
import { of, Subject } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { TableComponent } from '../../../../core/components/table/table.component';
import { StopPointWorkflowService } from '../../../../api/service/workflow/stop-point-workflow.service';

describe('StopPointWorkflowOverviewComponent', () => {
  const workflow: ReadStopPointWorkflow = {
    versionId: 1000,
    sloid: 'ch:1:sloid:7000',
    workflowComment: 'no comment!',
  };
  const container: ContainerReadStopPointWorkflow = {
    objects: [workflow],
    totalCount: 1,
  };
  let isAtLeastSupervisor = true;
  const permissionServiceMock: Partial<PermissionService> = {
    isAtLeastSupervisor(): boolean {
      return isAtLeastSupervisor;
    },
  };

  let component: StopPointWorkflowOverviewComponent;
  let fixture: ComponentFixture<StopPointWorkflowOverviewComponent>;
  let router: Router;

  let stopPointWorkflowService: Mocked<Pick<StopPointWorkflowService, 'getStopPointWorkflows'>>;

  beforeEach(() => {
    stopPointWorkflowService = {
      getStopPointWorkflows: vi.fn().mockReturnValue(of(container)),
    };

    TestBed.configureTestingModule({
      imports: [StopPointWorkflowOverviewComponent],
      providers: [
        { provide: PermissionService, useValue: permissionServiceMock },
        { provide: ActivatedRoute, useValue: { queryParam: new Subject() } },
        {
          provide: StopPointWorkflowService,
          useValue: stopPointWorkflowService,
        },
        translateServiceProvider,
      ],
    }).overrideComponent(StopPointWorkflowOverviewComponent, {
      remove: { imports: [TableComponent] },
      add: { imports: [MockTableComponent] },
    });
    fixture = TestBed.createComponent(StopPointWorkflowOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load overview as supervisor', () => {
    isAtLeastSupervisor = true;
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(stopPointWorkflowService.getStopPointWorkflows).toHaveBeenCalled();
    expect(component.stopPointWorkflows.length).toBe(1);
    expect(component.stopPointWorkflows[0].versionId).toBe(1000);
    expect(component.totalCount$).toEqual(1);
  });

  it('should load overview for reader/writer', () => {
    isAtLeastSupervisor = false;
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(stopPointWorkflowService.getStopPointWorkflows).toHaveBeenCalled();
    expect(component.stopPointWorkflows.length).toBe(1);
    expect(component.stopPointWorkflows[0].versionId).toBe(1000);
    expect(component.totalCount$).toEqual(1);
  });

  it('should go to detail on click', () => {
    vi.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));

    component.edit(workflow);

    expect(router.navigate).toHaveBeenCalled();
  });
});
