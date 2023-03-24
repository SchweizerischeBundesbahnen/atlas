import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowComponent } from './workflow.component';
import { AppTestingModule } from '../../app.testing.module';
import { MatExpansionModule } from '@angular/material/expansion';
import {
  ApplicationRole,
  ApplicationType,
  LineVersionWorkflow,
  Status,
  User,
  UserAdministrationService,
  UserPermission,
  Workflow,
  WorkflowProcessingStatus,
  WorkflowService,
  WorkflowStatus,
} from '../../api';
import { AtlasButtonComponent } from '../components/button/atlas-button.component';
import { Observable, of } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { DialogService } from '../components/dialog/dialog.service';
import { Component, Input } from '@angular/core';
import { Role } from '../auth/role';
import { AtlasFieldErrorComponent } from '../form-components/atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../form-components/atlas-label-field/atlas-label-field.component';
import { TextFieldComponent } from '../form-components/text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import WorkflowTypeEnum = Workflow.WorkflowTypeEnum;

@Component({
  selector: 'app-workflow-check-form',
  template: '',
})
export class MockWorkflowCheckFormComponent {
  @Input() workflowId: number | undefined;
}

const authServiceMock: Partial<AuthService> = {
  claims: {
    name: 'Test (ITC)',
    email: 'test@test.ch',
    sbbuid: 'e123456',
    roles: ['lidi-admin', 'lidi-writer'],
  },
  hasRole(role: Role): boolean {
    return this.claims!.roles.includes(role);
  },
  get isAdmin(): boolean {
    return true;
  },
  hasPermissionsToWrite(): boolean {
    return true;
  },
  isAtLeastSupervisor(): boolean {
    return true;
  },
  getApplicationUserPermission(applicationType: ApplicationType): UserPermission {
    return {
      application: applicationType,
      role: ApplicationRole.Supervisor,
      permissionRestrictions: [],
    };
  },
};
const user: User = {
  sbbUserId: 'e123',
  lastName: 'Marek',
  firstName: 'Hamsik',
  mail: 'a@b.cd',
};
const workflow: Workflow = {
  id: 1,
  workflowStatus: WorkflowStatus.Started,
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
      declarations: [
        WorkflowComponent,
        AtlasButtonComponent,
        MockWorkflowCheckFormComponent,
        AtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
        TextFieldComponent,
      ],
      providers: [
        { provide: UserAdministrationService, useValue: userAdministrationServiceMock },
        { provide: WorkflowService, useValue: workflowServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: TranslatePipe },
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
    fixture.componentInstance.switchVersionEvent = new Observable();
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
    component.showWorkflowForm();
    //then
    const form = component.workflowFormGroup.value;
    expect(form.comment).toBeNull();
    expect(form.function).toBeNull();
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
    component.startWorkflow();
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
