import {ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkflowComponent} from './workflow.component';
import {AppTestingModule} from '../../app.testing.module';
import {MatExpansionModule} from '@angular/material/expansion';
import {LineVersionWorkflow, Status,} from '../../api';
import {AtlasButtonComponent} from '../components/button/atlas-button.component';
import {AtlasFieldErrorComponent} from '../form-components/atlas-field-error/atlas-field-error.component';
import {AtlasLabelFieldComponent} from '../form-components/atlas-label-field/atlas-label-field.component';
import {TextFieldComponent} from '../form-components/text-field/text-field.component';
import {TranslatePipe} from '@ngx-translate/core';
import {adminPermissionServiceMock} from "../../app.testing.mocks";
import {PermissionService} from "../auth/permission/permission.service";

describe('WorkflowComponent', () => {
  let component: WorkflowComponent;
  let fixture: ComponentFixture<WorkflowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [AppTestingModule, MatExpansionModule, WorkflowComponent,
        AtlasButtonComponent,
        AtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
        TextFieldComponent],
    providers: [{ provide: PermissionService, useValue: adminPermissionServiceMock }, { provide: TranslatePipe }],
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
