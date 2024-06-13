import {ComponentFixture, TestBed} from '@angular/core/testing';
import {StopPointWorkflowOverviewComponent} from './stop-point-workflow-overview.component';
import {AppTestingModule} from "../../../../app.testing.module";
import {FormModule} from "../../../../core/module/form.module";
import {MockTableComponent} from "../../../../app.testing.mocks";
import {ContainerReadStopPointWorkflow, ReadStopPointWorkflow, StopPointWorkflowService} from "../../../../api";
import {of} from "rxjs";
import {Router} from "@angular/router";

const workflow: ReadStopPointWorkflow = {
  versionId: 1000,
};
const container: ContainerReadStopPointWorkflow = {
  objects: [workflow],
  totalCount: 1,
}

describe('StopPointWorkflowOverviewComponent', () => {
  let component: StopPointWorkflowOverviewComponent;
  let fixture: ComponentFixture<StopPointWorkflowOverviewComponent>;
  let router:Router;

  const stopPointWorkflowService = jasmine.createSpyObj('stopPointWorkflowService', {
    getStopPointWorkflows: of(container)
  })

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [
        StopPointWorkflowOverviewComponent,
        MockTableComponent
      ],
      imports: [AppTestingModule, FormModule],
      providers: [
        {provide: StopPointWorkflowService, useValue: stopPointWorkflowService},
      ]
    }).compileComponents().then();
    fixture = TestBed.createComponent(StopPointWorkflowOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load overview', () => {
    component.getOverview({
      page: 0,
      size: 10
    });

    expect(stopPointWorkflowService.getStopPointWorkflows).toHaveBeenCalled();
    expect(component.stopPointWorkflows.length).toBe(1);
    expect(component.stopPointWorkflows[0].versionId).toBe(1000);
    expect(component.totalCount$).toEqual(1)
  });

  it('should go to detail on click', () => {
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.edit(workflow);

    expect(router.navigate).toHaveBeenCalled();
  });
});
