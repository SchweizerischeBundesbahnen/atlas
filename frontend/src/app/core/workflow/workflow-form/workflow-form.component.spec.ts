import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowFormComponent } from './workflow-form.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormControl, FormGroup } from '@angular/forms';
import { WorkflowFormGroup } from '../workflow-form-group';
import { InfoIconComponent } from '../../form-components/info-icon/info-icon.component';
import { CommentComponent } from '../../form-components/comment/comment.component';
import { TextFieldComponent } from '../../form-components/text-field/text-field.component';
import { AtlasFieldErrorComponent } from '../../form-components/atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../../form-components/atlas-label-field/atlas-label-field.component';
import { TranslatePipe } from '@ngx-translate/core';

describe('WorkflowFormComponent', () => {
  let component: WorkflowFormComponent;
  let fixture: ComponentFixture<WorkflowFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        WorkflowFormComponent,
        InfoIconComponent,
        CommentComponent,
        TextFieldComponent,
        AtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();
    fixture = TestBed.createComponent(WorkflowFormComponent);
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

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
