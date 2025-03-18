import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {AddExaminants, StopPointPerson, StopPointWorkflowService} from 'src/app/api';
import { DetailHelperService } from 'src/app/core/detail/detail-helper.service';
import {NotificationService} from "../../../../../core/notification/notification.service";
import {Router} from "@angular/router";
import { FormGroup, ReactiveFormsModule } from "@angular/forms";
import {
  StopPointWorkflowDetailFormGroupBuilder
} from "../detail-form/stop-point-workflow-detail-form-group";
import {ValidationService} from "../../../../../core/validation/validation.service";
import {Pages} from "../../../../pages";
import {AddExaminantsDialogData} from "./add-examinants-dialog-data";
import {AtlasCharsetsValidator} from "../../../../../core/validation/charsets/atlas-charsets-validator";
import {AtlasFieldLengthValidator} from "../../../../../core/validation/field-lengths/atlas-field-length-validator";
import {AddExaminantsFormGroup, AddExaminantsFormGroupBuilder} from "./add-examinants-form-group";
import {catchError, of} from "rxjs";
import { DialogCloseComponent } from '../../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../../core/components/dialog/content/dialog-content.component';
import { StopPointWorkflowExaminantsTableComponent } from '../examinant-table/stop-point-workflow-examinants-table.component';
import { StringListComponent } from '../../../../../core/form-components/string-list/string-list.component';
import { AtlasFieldErrorComponent } from '../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { DialogFooterComponent } from '../../../../../core/components/dialog/footer/dialog-footer.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-add-examinants',
    templateUrl: './add-examinants.component.html',
    styleUrls: ['./add-examinants.component.scss'],
    imports: [DialogCloseComponent, DialogContentComponent, StopPointWorkflowExaminantsTableComponent, StringListComponent, ReactiveFormsModule, AtlasFieldErrorComponent, DialogFooterComponent, TranslatePipe]
})
export class AddExaminantsComponent implements OnInit {

  readonly emailValidator = [AtlasCharsetsValidator.email, AtlasFieldLengthValidator.length_100];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: AddExaminantsDialogData,
    public dialogRef: MatDialogRef<AddExaminantsComponent>,
    private detailHelperService: DetailHelperService,
    private stopPointWorkflowService: StopPointWorkflowService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  form!: FormGroup<AddExaminantsFormGroup>;

  ngOnInit() {
    this.form = AddExaminantsFormGroupBuilder.buildFormGroup();
    this.form.controls.examinants.push(StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup());
  }

  addExaminants() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const addExaminants: AddExaminants = {
        ccEmails: this.form.controls.ccEmails.value!,
        examinants: this.form.controls.examinants.controls
          .filter((control) => !control.disabled)
          .map((examinant) => {
            if (!examinant.value.firstName) {
              examinant.controls.firstName.setValue(null);
            }
            if (!examinant.value.lastName) {
              examinant.controls.lastName.setValue(null);
            }
            return examinant.value as StopPointPerson;
          }),
      };
      this.saveAdditionalExaminants(addExaminants);
    }
  }

  private saveAdditionalExaminants(addExaminants: AddExaminants){
    this.stopPointWorkflowService
      .addExaminantsToStopPointWorkflow(this.data.workflowId, addExaminants)
      .pipe(
        catchError(() => {
          this.dialogRef.close(false);
          return of();
        }),
      )
      .subscribe(
        () => {
          this.notificationService.success('WORKFLOW.NOTIFICATION.ADD.SUCCESS');
          this.dialogRef.close(true);
          this.router
            .navigate([
              Pages.SEPODI.path,
              Pages.WORKFLOWS.path,
              this.data.workflowId,
            ])
            .then();
        }
      );
  }

  cancel() {
    this.detailHelperService
      .confirmLeaveDirtyForm(this.form)
      .subscribe((confirmed) => {
        if (confirmed) {
          this.dialogRef.close(false);
        }
      });
  }
}
