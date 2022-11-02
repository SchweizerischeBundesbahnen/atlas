import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowComponent } from './workflow.component';
import { AppTestingModule } from '../../app.testing.module';
import { MatExpansionModule } from '@angular/material/expansion';
import { Status, User, UserAdministrationService, WorkflowProcessingStatus } from '../../api';
import { AtlasButtonComponent } from '../components/button/atlas-button.component';
import { of } from 'rxjs';
import { AuthService } from '../auth/auth.service';

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
const userAdministrationService = jasmine.createSpyObj('userAdministrationService', [
  'getCurrentUser',
]);

userAdministrationService.getCurrentUser.and.returnValue(of(user));

describe('WorkflowComponent', () => {
  let component: WorkflowComponent;
  let fixture: ComponentFixture<WorkflowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppTestingModule, MatExpansionModule],
      declarations: [WorkflowComponent, AtlasButtonComponent],
      providers: [
        { provide: UserAdministrationService, useValue: userAdministrationService },
        { provide: AuthService, useValue: authServiceMock },
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
    fixture.componentInstance.lineRecord.lineVersionWorkflows?.add({
      workflowId: 1,
      workflowProcessingStatus: WorkflowProcessingStatus.InProgress,
    });

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show Workflow Form', () => {
    //when
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
});
