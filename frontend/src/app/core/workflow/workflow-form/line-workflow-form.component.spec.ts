import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { LineWorkflowFormComponent } from './line-workflow-form.component';
import { FormControl, FormGroup } from '@angular/forms';
import { WorkflowFormGroup } from '../workflow-form-group';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { inputBinding, signal } from '@angular/core';

describe('LineWorkflowFormComponent', () => {
  let component: LineWorkflowFormComponent;
  let fixture: ComponentFixture<LineWorkflowFormComponent>;
  let formGroupInput: ReturnType<typeof signal<FormGroup<WorkflowFormGroup>>>;

  beforeEach(() => {
    // Config
    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    // Arrangement
    const commentLabelInputName: keyof LineWorkflowFormComponent = 'commentLabel';
    const personLabelInputName: keyof LineWorkflowFormComponent = 'personLabel';
    const formGroupInputName: keyof LineWorkflowFormComponent = 'formGroup';
    formGroupInput = signal(
      new FormGroup<WorkflowFormGroup>({
        comment: new FormControl(''),
        firstName: new FormControl(''),
        lastName: new FormControl(''),
        function: new FormControl(''),
        mail: new FormControl(''),
      })
    );
    fixture = TestBed.createComponent(LineWorkflowFormComponent, {
      bindings: [
        inputBinding(formGroupInputName, formGroupInput),
        inputBinding(commentLabelInputName, () => 'test comment label'),
        inputBinding(personLabelInputName, () => 'test person label'),
      ],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });
});
