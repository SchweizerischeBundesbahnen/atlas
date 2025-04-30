import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StopPointWorkflowOverviewComponent } from './stop-point-workflow-overview.component';
import { MockTableComponent } from '../../../../app.testing.mocks';
import {
  ContainerReadStopPointWorkflow,
  ReadStopPointWorkflow,
  StopPointWorkflowService,
} from '../../../../api';
import { of, Subject } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TranslateModule } from '@ngx-translate/core';

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

describe('StopPointWorkflowOverviewComponent', () => {
  let component: StopPointWorkflowOverviewComponent;
  let fixture: ComponentFixture<StopPointWorkflowOverviewComponent>;
  let router: Router;

  const stopPointWorkflowService = jasmine.createSpyObj(
    'stopPointWorkflowService',
    {
      getStopPointWorkflows: of(container),
    }
  );

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [StopPointWorkflowOverviewComponent, TranslateModule.forRoot()],
      providers: [
        { provide: PermissionService, useValue: permissionServiceMock },
        { provide: ActivatedRoute, useValue: { queryParam: new Subject() } },
        {
          provide: StopPointWorkflowService,
          useValue: stopPointWorkflowService,
        },
      ],
    })
      .overrideComponent(StopPointWorkflowOverviewComponent, {
        remove: { imports: [TableComponent] },
        add: { imports: [MockTableComponent] },
      })
      .compileComponents()
      .then();
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
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.edit(workflow);

    expect(router.navigate).toHaveBeenCalled();
  });
});
