import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {AtlasFieldLengthValidator} from '../../validation/field-lengths/atlas-field-length-validator';
import {AtlasCharsetsValidator} from '../../validation/charsets/atlas-charsets-validator';
import {Subject} from 'rxjs';
import {UserAdministrationService, WorkflowService} from '../../../api';
import {takeUntil} from 'rxjs/operators';
import {WorkflowCheckFormGroup} from './workflow-check-form-group';
import {NotificationService} from '../../notification/notification.service';
import {ValidationService} from '../../validation/validation.service';
import {WhitespaceValidator} from '../../validation/whitespace/whitespace-validator';
import {PermissionService} from "../../auth/permission/permission.service";

@Component({
  selector: 'app-workflow-check-form',
  templateUrl: './workflow-check-form.component.html',
})
export class WorkflowCheckFormComponent implements OnInit {
  @Input() workflowId: number | undefined;
  @Output() workflowChecked = new EventEmitter<void>();

  formGroup: FormGroup<WorkflowCheckFormGroup> = new FormGroup<WorkflowCheckFormGroup>({
    comment: new FormControl('', [
      AtlasFieldLengthValidator.comments,
      AtlasCharsetsValidator.iso88591,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
    firstName: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
    lastName: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
    function: new FormControl('', [
      Validators.required,
      AtlasFieldLengthValidator.length_50,
      AtlasCharsetsValidator.iso88591,
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
    ]),
  });
  private ngUnsubscribe = new Subject<void>();

  constructor(
    private readonly workflowService: WorkflowService,
    private readonly notificationService: NotificationService,
    private readonly userAdministrationService: UserAdministrationService,
    public permissionService: PermissionService
  ) {}

  ngOnInit(): void {
    this.fillDefaultExaminant();
  }

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
    ValidationService.validateForm(this.formGroup);

    if (this.formGroup.valid) {
      this.workflowService
        .examinantCheck(this.workflowId!, {
          accepted: accepted,
          checkComment: this.formGroup.value.comment!,
          examinant: {
            firstName: this.formGroup.value.firstName!,
            lastName: this.formGroup.value.lastName!,
            personFunction: this.formGroup.value.function!,
          },
        })
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe(() => {
          this.workflowChecked.emit();
          this.notificationService.success(
            accepted
              ? 'WORKFLOW.NOTIFICATION.CHECK.ACCEPTED'
              : 'WORKFLOW.NOTIFICATION.CHECK.REJECTED'
          );
        });
    }
  }

  private fillDefaultExaminant() {
    this.userAdministrationService
      .getCurrentUser()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((user) => {
        this.formGroup.controls.firstName.setValue(user.firstName);
        this.formGroup.controls.lastName.setValue(user.lastName);
      });
  }
}
