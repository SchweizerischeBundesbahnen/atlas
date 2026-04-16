import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { LineWorkflowComponent } from './line-workflow.component';
import { LineVersionWorkflow, Status } from '../../api';
import { adminPermissionServiceMock, translateServiceProvider } from '../../app.testing.mocks';
import { PermissionService } from '../auth/permission/permission.service';

describe('LineWorkflowComponent', () => {
  let component: LineWorkflowComponent;
  let fixture: ComponentFixture<LineWorkflowComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: PermissionService, useValue: adminPermissionServiceMock }, translateServiceProvider],
    });

    fixture = TestBed.createComponent(LineWorkflowComponent);
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
    expect(component.workflowInProgress).toBe(false);
  });

  it('should show read Workflow Buttons when workflow in progress', () => {
    //given

    //when
    component.initWorkflowButtons();

    //then
    expect(component.workflowInProgress).toBe(true);
  });
});
