import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LineWorkflowCheckFormComponent } from './line-workflow-check-form.component';
import { AppTestingModule } from '../../../app.testing.module';
import { InfoIconComponent } from '@atlas/form/info-icon/info-icon.component';
import { CommentComponent } from '../../form-components/comment/comment.component';
import { By } from '@angular/platform-browser';
import { LineWorkflowFormComponent } from '../workflow-form/line-workflow-form.component';
import { AtlasButtonComponent } from '../../components/button/atlas-button.component';
import { of } from 'rxjs';
import { AtlasFieldErrorComponent } from '../../form-components/atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '@atlas/form/atlas-label-field/atlas-label-field.component';
import { TextFieldComponent } from '../../form-components/text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import { PermissionService } from '../../auth/permission/permission.service';
import { LineWorkflowService } from '../../../api/service/workflow/line-workflow.service';

let isAtLeastSupervisor = true;
const permissionServiceMock: Partial<PermissionService> = {
  isAtLeastSupervisor(): boolean {
    return isAtLeastSupervisor;
  },
};

describe('LineWorkflowCheckFormComponent', () => {
  let component: LineWorkflowCheckFormComponent;
  let fixture: ComponentFixture<LineWorkflowCheckFormComponent>;

  const workflowServiceSpy = jasmine.createSpyObj(LineWorkflowService, {
    examinantCheck: of({}),
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: PermissionService, useValue: permissionServiceMock },
        { provide: LineWorkflowService, useValue: workflowServiceSpy },
        { provide: TranslatePipe },
      ],
      imports: [
        AppTestingModule,
        LineWorkflowCheckFormComponent,
        LineWorkflowFormComponent,
        AtlasButtonComponent,
        InfoIconComponent,
        CommentComponent,
        AtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
        TextFieldComponent,
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(LineWorkflowCheckFormComponent);
    component = fixture.componentInstance;

    isAtLeastSupervisor = true;
    fixture.detectChanges();
  });

  it('should show component for supervisor', () => {
    isAtLeastSupervisor = true;

    expect(component).toBeTruthy();
    expect(
      fixture.debugElement.query(By.css('app-workflow-form'))
    ).toBeTruthy();
  });

  it('should not show component for reader/writer', () => {
    isAtLeastSupervisor = false;
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('app-workflow-form'))).toBeFalsy();
  });

  it('should check workflow on button click', () => {
    component.formGroup.controls.comment.setValue('Super Linie');
    component.formGroup.controls.firstName.setValue('Laurin');
    component.formGroup.controls.lastName.setValue('Schäfer');
    component.formGroup.controls.function.setValue('ZukunftsPO');
    fixture.detectChanges();

    component.acceptWorkflow();

    expect(workflowServiceSpy.examinantCheck).toHaveBeenCalled();
  });
});
