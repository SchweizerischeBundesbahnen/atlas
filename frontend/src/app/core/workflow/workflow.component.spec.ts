import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowComponent } from './workflow.component';
import { AppTestingModule } from '../../app.testing.module';
import { MatExpansionModule } from '@angular/material/expansion';
import {
  LineVersionWorkflow,
  Status,
  User,
  UserAdministrationService,
  Workflow,
  WorkflowProcessingStatus,
  WorkflowService,
  WorkflowStart,
} from '../../api';
import { AtlasButtonComponent } from '../components/button/atlas-button.component';
import { of } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { DialogService } from '../components/dialog/dialog.service';
import WorkflowStatusEnum = WorkflowStart.WorkflowStatusEnum;
import WorkflowTypeEnum = Workflow.WorkflowTypeEnum;

const authServiceMock: Partial<AuthService> = {
  claims: {
    name: 'Test (ITC)',
    email: 'test@test.ch',
    sbbuid: 'e123456',
    roles: ['lidi-admin', 'lidi-writer'],
  },
};
const user: User = {
  sbbUserId: 'e123',
  lastName: 'Marek',
  firstName: 'Hamsik',
  mail: 'a@b.cd',
};
const workflow: Workflow = {
  workflowStatus: WorkflowStatusEnum.Started,
  client: {
    firstName: 'Marek',
    lastName: 'Hamsik',
    mail: 'a@b.cd',
    personFunction: 'centrocampista',
  },
  workflowComment: 'You are the best',
  swissId: 'ch:slnid:10000',
  description: 'Bern-Napoli',
  businessObjectId: 1000,
  workflowType: WorkflowTypeEnum.Line,
};

describe('WorkflowComponent', () => {
  const workflowServiceMock = jasmine.createSpyObj(WorkflowService, {
    getWorkflow: of({}),
    startWorkflow: of({}),
  });
  const userAdministrationServiceMock = jasmine.createSpyObj(UserAdministrationService, {
    getCurrentUser: of({}),
  });

  const dialogServiceSpy = jasmine.createSpyObj(DialogService, { confirmLeave: of({}) });

  let component: WorkflowComponent;
  let fixture: ComponentFixture<WorkflowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppTestingModule, MatExpansionModule],
      declarations: [WorkflowComponent, AtlasButtonComponent],
      providers: [
        { provide: UserAdministrationService, useValue: userAdministrationServiceMock },
        { provide: WorkflowService, useValue: workflowServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: DialogService, useValue: dialogServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkflowComponent);
    fixture.componentInstance.lineRecord = {
      id: 123,
      validFrom: new Date(),
      validTo: new Date(),
      slnid: 'ch:1:slnid:1000003',
      businessOrganisation: 'ch:1:sboid:110000',
      status: Status.Draft,
      versionNumber: 0,
    };

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show Workflow Form', () => {
    //given
    userAdministrationServiceMock.getCurrentUser.and.returnValue(of(user));

    //when
    component.lineRecord.lineVersionWorkflows?.clear();
    component.showWorflowForm();
    //then
    const form = component.workflowFormGroup.value;
    expect(form.comment).toEqual('');
    expect(form.function).toEqual('');
    expect(form.firstName).toEqual('Hamsik');
    expect(form.lastName).toEqual('Marek');
    expect(form.mail).toEqual('a@b.cd');
    expect(component.isAddWorkflowButtonDisabled).toBeTruthy();
    expect(component.isWorkflowFormEditable).toBeTruthy();
  });

  it('should init Workflow Form Read Mode', () => {
    //given
    fixture.componentInstance.lineRecord.lineVersionWorkflows = new Set<LineVersionWorkflow>();
    fixture.componentInstance.lineRecord.lineVersionWorkflows.add({
      workflowId: 1,
      workflowProcessingStatus: WorkflowProcessingStatus.InProgress,
    });
    workflowServiceMock.getWorkflow.and.returnValue(of(workflow));
    //when
    component.ngOnInit();
    //then
    expect(component.isReadMode).toBeTruthy();
    expect(component.isAddWorkflowButtonDisabled).toBeTruthy();
    expect(component.workflowFormGroup.disable).toBeTruthy();
    const form = component.workflowFormGroup.value;
    expect(form.comment).toEqual('You are the best');
    expect(form.function).toEqual('centrocampista');
    expect(form.firstName).toEqual('Marek');
    expect(form.lastName).toEqual('Hamsik');
    expect(form.mail).toEqual('a@b.cd');
  });

  it('should start Workflow', () => {
    //given
    const eventReloadParentSpy = spyOn(component.workflowEvent, 'emit');
    const form = component.workflowFormGroup;
    form.controls['comment'].setValue('The best in the world');
    form.controls['firstName'].setValue('Ciovanni');
    form.controls['lastName'].setValue('Stazione');
    form.controls['function'].setValue('Zug Fahrer');
    form.controls['mail'].setValue('ma@am.ma');
    workflowServiceMock.startWorkflow.and.returnValue(of(workflow));
    //when
    component.startWorflow();
    //then
    expect(eventReloadParentSpy).toHaveBeenCalledWith({
      reload: true,
    });
    expect(component.isReadMode).toBeTruthy();
    expect(component.isAddWorkflowButtonDisabled).toBeTruthy();
    expect(component.workflowFormGroup.disable).toBeTruthy();
    expect(component.isWorkflowFormEditable).toBeFalsy();
  });

  it('should toggle Workflow when form is dirty', () => {
    //given
    component.workflowFormGroup.markAsDirty();
    //when
    component.toggleWorkflow();
    //then
    expect(dialogServiceSpy.confirmLeave).toHaveBeenCalled();
    expect(component.isAddWorkflowButtonDisabled).toBeFalsy();
    expect(component.isReadMode).toBeFalsy();
    expect(component.isWorkflowFormEditable).toBeFalsy();
  });

  it('should toggle Workflow when form is not dirty', () => {
    //when
    component.toggleWorkflow();
    //then
    expect(component.isAddWorkflowButtonDisabled).toBeFalsy();
    expect(component.isReadMode).toBeFalsy();
    expect(component.isWorkflowFormEditable).toBeFalsy();
  });
});
