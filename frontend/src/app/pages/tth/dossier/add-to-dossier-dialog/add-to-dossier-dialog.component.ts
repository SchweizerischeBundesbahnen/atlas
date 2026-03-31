import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogRef,
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { DossierSelectComponent } from './dossier-select/dossier-select.component';
import { TthDossier } from '../../../../api/model/tthDossier';
import { DossierInternalService } from '../../../../api/service/workflow/dossier-internal.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DOSSIER_EDITABLE_STATES } from '../detail/canton-dossier-detail/canton-dossier-detail.component';
import { DialogData } from '../../../../core/components/dialog/dialog.data';
import { TimetableHearingStatementV2 } from '../../../../api';

export interface AddToDossierData extends DialogData {
  statement: TimetableHearingStatementV2;
}

@Component({
  selector: 'atlas-statement-select-dialog',
  templateUrl: './add-to-dossier-dialog.component.html',
  imports: [
    MatDialogClose,
    ReactiveFormsModule,
    MatDialogActions,
    TranslatePipe,
    AtlasButtonComponent,
    DossierSelectComponent,
  ],
  providers: [TranslatePipe],
})
export class AddToDossierDialogComponent {
  private readonly dialogRef =
    inject<MatDialogRef<AddToDossierDialogComponent, boolean>>(MatDialogRef);
  private readonly dossierInternalService = inject(DossierInternalService);
  private readonly notificationService = inject(NotificationService);

  readonly data = inject<AddToDossierData>(MAT_DIALOG_DATA);
  readonly addableDossierStates = DOSSIER_EDITABLE_STATES;

  form = new FormGroup({
    dossier: new FormControl(),
  });

  confirm() {
    const updatedDossier: TthDossier = this.form.controls.dossier.value;
    updatedDossier.statementIds.push(this.data.statement.id!);

    this.dossierInternalService.updateDossier(updatedDossier).subscribe(() => {
      this.notificationService.success('TTH.DOSSIER.NOTIFICATION.EDIT_SUCCESS');
      this.dialogRef.close(true);
    });
  }

  cancel() {
    this.dialogRef.close(false);
  }
}
