import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowComponent } from './workflow.component';
import { AppTestingModule } from '../../app.testing.module';
import { MatExpansionModule } from '@angular/material/expansion';
import {
  ApplicationRole,
  ApplicationType,
  LineVersionWorkflow,
  Permission,
  Status,
} from '../../api';
import { AtlasButtonComponent } from '../components/button/atlas-button.component';
import { AtlasFieldErrorComponent } from '../form-components/atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../form-components/atlas-label-field/atlas-label-field.component';
import { TextFieldComponent } from '../form-components/text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthService } from '../auth/auth.service';
import { Role } from '../auth/role';

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
  getApplicationUserPermission(applicationType: ApplicationType): Permission {
    return {
      application: applicationType,
      role: ApplicationRole.Supervisor,
      permissionRestrictions: [],
    };
  },
};

describe('WorkflowComponent', () => {
  let component: WorkflowComponent;
  let fixture: ComponentFixture<WorkflowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppTestingModule, MatExpansionModule],
      declarations: [
        WorkflowComponent,
        AtlasButtonComponent,
        AtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
        TextFieldComponent,
      ],
      providers: [{ provide: AuthService, useValue: authServiceMock }, { provide: TranslatePipe }],
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
      lineVersionWorkflows: new Set<LineVersionWorkflow>([
        {
          workflowId: 1,
          workflowProcessingStatus: 'IN_PROGRESS',
        },
      ]),
    };

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show Workflow Buttons when no workflow in progress', () => {
    //given

    //when
    component.lineRecord.lineVersionWorkflows?.clear();
    component.initWorkflowButtons();

    //then
    expect(component.workflowInProgress).toBeFalse();
  });

  it('should show read Workflow Buttons when workflow in progress', () => {
    //given

    //when
    component.initWorkflowButtons();

    //then
    expect(component.workflowInProgress).toBeTrue();
  });
});
