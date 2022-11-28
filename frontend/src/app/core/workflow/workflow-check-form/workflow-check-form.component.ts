import { Component, Input } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { WorkflowFormGroup } from '../workflow-form-group';
import { AtlasFieldLengthValidator } from '../../validation/field-lengths/atlas-field-length-validator';
import { AtlasCharsetsValidator } from '../../validation/charsets/atlas-charsets-validator';
import { Subject } from 'rxjs';
import { WorkflowService } from '../../../api';
import { takeUntil } from 'rxjs/operators';
import { Record } from '../../components/base-detail/record';

@Component({
  selector: 'app-workflow-check-form',
  templateUrl: './workflow-check-form.component.html',
})
export class WorkflowCheckFormComponent {
  @Input() workflowId: number | undefined;

  formGroup: FormGroup<WorkflowFormGroup> = new FormGroup<WorkflowFormGroup>({
    comment: new FormControl('', [
      AtlasFieldLengthValidator.comments,
      AtlasCharsetsValidator.iso88591,
    ]),
    firstName: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
    ]),
    lastName: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
    ]),
    function: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
    ]),

    // TODO: mail required by db, but not by requirement
    mail: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_255,
      AtlasCharsetsValidator.email,
    ]),
  });
  private ngUnsubscribe = new Subject<void>();

  constructor(private workflowService: WorkflowService) {}

  acceptWorkflow() {
    this.checkWorkflow(true);
  }

  rejectWorkflow() {
    this.checkWorkflow(false);
  }

  private checkWorkflow(accepted: boolean) {
    if (!accepted) {
      this.formGroup.controls.comment.addValidators(Validators.required);
      this.formGroup.controls.comment.updateValueAndValidity();
    }
    this.validateForm();
    if (this.formGroup.valid) {
      this.workflowService
        .examinantCheck(this.workflowId!, {
          accepted: accepted,
          checkComment: this.formGroup.value.comment!,
          examinant: {
            firstName: this.formGroup.value.firstName!,
            lastName: this.formGroup.value.lastName!,
            mail: this.formGroup.value.mail!,
            personFunction: this.formGroup.value.function!,
          },
        })
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe((workflow) => {
          console.log(workflow);
        });
    }
  }

  private validateForm() {
    Object.keys(this.formGroup.controls).forEach((field) => {
      const control = this.formGroup.get(field);
      if (control) {
        control.markAsTouched({ onlySelf: true });
      }
    });
  }
}
