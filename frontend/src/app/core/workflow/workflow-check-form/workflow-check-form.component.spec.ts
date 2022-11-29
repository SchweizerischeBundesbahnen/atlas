import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowCheckFormComponent } from './workflow-check-form.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormControl, FormGroup } from '@angular/forms';
import { InfoIconComponent } from '../../form-components/info-icon/info-icon.component';
import { CommentComponent } from '../../form-components/comment/comment.component';
import { WorkflowCheckFormGroup } from './workflow-check-form-group';

describe('WorkflowFormComponent', () => {
  let component: WorkflowCheckFormComponent;
  let fixture: ComponentFixture<WorkflowCheckFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WorkflowCheckFormComponent, InfoIconComponent, CommentComponent],
      imports: [AppTestingModule],
    }).compileComponents();
    fixture = TestBed.createComponent(WorkflowCheckFormComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup<WorkflowCheckFormGroup>({
      comment: new FormControl(''),
      firstName: new FormControl(''),
      lastName: new FormControl(''),
      function: new FormControl(''),
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
