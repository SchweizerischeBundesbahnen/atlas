import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { LineWorkflowCheckFormComponent } from './line-workflow-check-form.component';
import { By } from '@angular/platform-browser';
import { EMPTY, of } from 'rxjs';
import { PermissionService } from '../../auth/permission/permission.service';
import { LineWorkflowService } from '../../../api/service/workflow/line-workflow.service';
import { UserAdministrationService } from '../../../api/service/user-administration/user-administration.service';
import { Workflow } from '../../../api';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { inputBinding } from '@angular/core';

describe('LineWorkflowCheckFormComponent', () => {
  let component: LineWorkflowCheckFormComponent;
  let fixture: ComponentFixture<LineWorkflowCheckFormComponent>;

  let isAtLeastSupervisor = true;
  let permissionServiceStub: Pick<PermissionService, 'isAtLeastSupervisor'>;
  let workflowServiceStub: Mocked<Pick<LineWorkflowService, 'examinantCheck'>>;
  let userAdminServiceStub: Mocked<Pick<UserAdministrationService, 'getCurrentUser'>>;

  beforeEach(() => {
    // Mocking
    permissionServiceStub = {
      isAtLeastSupervisor(): boolean {
        return isAtLeastSupervisor;
      },
    };
    workflowServiceStub = {
      examinantCheck: vi.fn().mockReturnValue(of({} as Workflow)),
    };
    userAdminServiceStub = {
      getCurrentUser: vi.fn().mockReturnValue(EMPTY),
    };

    // Config
    TestBed.configureTestingModule({
      providers: [
        { provide: PermissionService, useValue: permissionServiceStub },
        { provide: LineWorkflowService, useValue: workflowServiceStub },
        { provide: UserAdministrationService, useValue: userAdminServiceStub },
        translateServiceProvider,
      ],
    });

    // Arrangement
    const workflowIdInputName: keyof LineWorkflowCheckFormComponent = 'workflowId';
    fixture = TestBed.createComponent(LineWorkflowCheckFormComponent, {
      bindings: [inputBinding(workflowIdInputName, () => 100)],
    });
    component = fixture.componentInstance;
  });

  it('should show component for supervisor', () => {
    isAtLeastSupervisor = true;
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(fixture.debugElement.query(By.css('atlas-workflow-form'))).toBeTruthy();
  });

  it('should not show component for reader/writer', () => {
    isAtLeastSupervisor = false;
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('atlas-workflow-form'))).toBeFalsy();
  });

  it('should check workflow on button click', () => {
    component.formGroup.controls.comment.setValue('Super Linie');
    component.formGroup.controls.firstName.setValue('Laurin');
    component.formGroup.controls.lastName.setValue('Schäfer');
    component.formGroup.controls.function.setValue('ZukunftsPO');
    fixture.detectChanges();

    component.acceptWorkflow();

    expect(workflowServiceStub.examinantCheck).toHaveBeenCalled();
  });
});
