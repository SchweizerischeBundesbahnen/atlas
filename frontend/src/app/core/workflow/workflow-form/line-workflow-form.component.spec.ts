import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LineWorkflowFormComponent } from './line-workflow-form.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormControl, FormGroup } from '@angular/forms';
import { WorkflowFormGroup } from '../workflow-form-group';
import { InfoIconComponent } from '@atlas/form/info-icon/info-icon.component';
import { CommentComponent } from '../../form-components/comment/comment.component';
import { TextFieldComponent } from '../../form-components/text-field/text-field.component';
import { AtlasFieldErrorComponent } from '../../form-components/atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '@atlas/form/atlas-label-field/atlas-label-field.component';
import { TranslatePipe } from '@ngx-translate/core';

describe('LineWorkflowFormComponent', () => {
  let component: LineWorkflowFormComponent;
  let fixture: ComponentFixture<LineWorkflowFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        LineWorkflowFormComponent,
        InfoIconComponent,
        CommentComponent,
        TextFieldComponent,
        AtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
      ],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();
    fixture = TestBed.createComponent(LineWorkflowFormComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup<WorkflowFormGroup>({
      comment: new FormControl(''),
      firstName: new FormControl(''),
      lastName: new FormControl(''),
      function: new FormControl(''),
      mail: new FormControl(''),
    });
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });
});
