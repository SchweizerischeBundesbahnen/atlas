import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowFormComponent } from './workflow-form.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormControl, FormGroup } from '@angular/forms';
import { WorkflowFormGroup } from '../workflow-form-group';

describe('WorkflowFormComponent', () => {
  let component: WorkflowFormComponent;
  let fixture: ComponentFixture<WorkflowFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WorkflowFormComponent],
      imports: [AppTestingModule],
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
