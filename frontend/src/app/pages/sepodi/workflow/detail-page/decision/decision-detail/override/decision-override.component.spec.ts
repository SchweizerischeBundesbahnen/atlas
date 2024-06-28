import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DecisionOverrideComponent} from './decision-override.component';
import {AppTestingModule} from "../../../../../../../app.testing.module";
import {JudgementType, StopPointWorkflowService} from "../../../../../../../api";
import {of} from "rxjs";
import {PermissionService} from "../../../../../../../core/auth/permission/permission.service";
import {adminPermissionServiceMock} from "../../../../../../../app.testing.mocks";
import {NotificationService} from "../../../../../../../core/notification/notification.service";

const stopPointWorkflowService = jasmine.createSpyObj('stopPointWorkflowService', {
  overrideVoteWorkflow: of({}),
});

const notificationService = jasmine.createSpyObj('notificationService', ['success']);

describe('DecisionOverrideComponent', () => {
  let component: DecisionOverrideComponent;
  let fixture: ComponentFixture<DecisionOverrideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionOverrideComponent, AppTestingModule],
      providers: [
        {provide: StopPointWorkflowService, useValue: stopPointWorkflowService},
        {provide: PermissionService, useValue: adminPermissionServiceMock},
        {provide: NotificationService, useValue: notificationService},
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DecisionOverrideComponent);
    component = fixture.componentInstance;

    component.workflowId = 12;
    component.examinantId = 12331;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.isSepodiSupervisor).toBeTrue();
  });

  it('should save override', () => {
    component.formGroup.controls.firstName.setValue("Markus");
    component.formGroup.controls.lastName.setValue("Giger");
    component.formGroup.controls.fotJudgement.setValue(JudgementType.Yes);

    component.saveOverride();

    expect(stopPointWorkflowService.overrideVoteWorkflow).toHaveBeenCalledWith(component.workflowId, component.examinantId, {
      firstName: 'Markus',
      lastName: 'Giger',
      fotJudgement: JudgementType.Yes,
      fotMotivation: null
    });

    expect(notificationService.success).toHaveBeenCalled()
  });
});
