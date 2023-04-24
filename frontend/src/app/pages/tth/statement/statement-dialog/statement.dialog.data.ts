import { FormGroup } from '@angular/forms';

export interface StatementDialogData {
  // ths: TimetableHearingStatement;
  form: FormGroup;
  cancelText?: string;
  confirmText?: string;
}
