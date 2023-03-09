import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowCheckFormComponent } from './workflow-check-form.component';
import { AppTestingModule } from '../../../app.testing.module';
import { InfoIconComponent } from '../../form-components/info-icon/info-icon.component';
import { CommentComponent } from '../../form-components/comment/comment.component';
import { AuthService } from '../../auth/auth.service';
import { By } from '@angular/platform-browser';
import { WorkflowFormComponent } from '../workflow-form/workflow-form.component';
import { AtlasButtonComponent } from '../../components/button/atlas-button.component';
import { WorkflowService } from '../../../api';
import { of } from 'rxjs';
import { AtlasFieldErrorComponent } from '../../form-components/atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../../form-components/atlas-label-field/atlas-label-field.component';
import { TextFieldComponent } from '../../form-components/text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';

let isAtLeastSupervisor = true;
const authServiceMock: Partial<AuthService> = {
  isAtLeastSupervisor(): boolean {
    return isAtLeastSupervisor;
  },
};

describe('WorkflowCheckFormComponent', () => {
  let component: WorkflowCheckFormComponent;
  let fixture: ComponentFixture<WorkflowCheckFormComponent>;

  const workflowServiceSpy = jasmine.createSpyObj(WorkflowService, {
    examinantCheck: of({}),
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        WorkflowCheckFormComponent,
        WorkflowFormComponent,
        AtlasButtonComponent,
        InfoIconComponent,
        CommentComponent,
        AtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
        TextFieldComponent,
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: WorkflowService, useValue: workflowServiceSpy },
        { provide: TranslatePipe },
      ],
      imports: [AppTestingModule],
    }).compileComponents();
    fixture = TestBed.createComponent(WorkflowCheckFormComponent);
    component = fixture.componentInstance;

    isAtLeastSupervisor = true;
    fixture.detectChanges();
  });

  it('should show component for supervisor', () => {
    isAtLeastSupervisor = true;

    expect(component).toBeTruthy();
    expect(fixture.debugElement.query(By.css('app-workflow-form'))).toBeTruthy();
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
