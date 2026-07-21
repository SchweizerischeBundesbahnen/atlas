import { inject, Injectable, OnInit, Signal, WritableSignal } from '@angular/core';
import { Observable, of, take } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { DialogService } from '../components/dialog/dialog.service';
import { filter } from 'rxjs/operators';
import { DialogData } from '../components/dialog/dialog.data';

export interface DetailWithCancelEdit extends OnInit {
  isNew: boolean;
  back: () => void;
  form: FormGroup;
}

export interface SignalDetailWithCancelEdit<FormType extends object> extends OnInit {
  isNew: boolean;
  back: () => void;
  formModel: WritableSignal<FormType>;
  emptyFormValue: FormType;
  dirty: Signal<boolean>;
  editMode: WritableSignal<boolean>;
}

@Injectable({
  providedIn: 'root',
})
export class DetailDialogHelperService {
  private readonly dialogService = inject(DialogService);

  showCancelEditDialog(detail: DetailWithCancelEdit) {
    this.confirmLeave(detail)
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          if (detail.isNew) {
            detail.form.reset();
            detail.back();
          } else {
            detail.ngOnInit();
            detail.form.disable();
          }
        }
      });
  }

  confirmLeave(detail: DetailWithCancelEdit): Observable<boolean> {
    if (detail.form.dirty) {
      return this.openLeaveDialog();
    }
    return of(true);
  }

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  openCancelEditDialog(detail: SignalDetailWithCancelEdit<any>) {
    this.confirmLeaving(detail)
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          if (detail.isNew) {
            detail.formModel.set({ ...detail.emptyFormValue });
            detail.back();
          } else {
            detail.ngOnInit();
            detail.editMode.set(false);
          }
        }
      });
  }

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  confirmLeaving(detail: SignalDetailWithCancelEdit<any>): Observable<boolean> {
    if (detail.dirty()) {
      return this.openLeaveDialog();
    }
    return of(true);
  }

  confirmLeaveDirtyForm(form: FormGroup): Observable<boolean> {
    if (form.dirty) {
      return this.openLeaveDialog();
    }
    return of(true);
  }

  openLeaveDialog(): Observable<boolean> {
    return this.dialogService.openDialogDataWithConfirmationResult({
      title: 'DIALOG.DISCARD_CHANGES_TITLE',
      message: 'DIALOG.LEAVE_SITE',
    } satisfies DialogData);
  }

  confirmWarning(labels: Pick<DialogData, 'message' | 'confirmText'>, onConfirm: () => void) {
    this.dialogService
      .openDialogDataWithConfirmationResult({
        title: 'DIALOG.WARNING',
        cancelText: 'DIALOG.BACK',
        ...labels,
      })
      .pipe(
        take(1),
        filter((confirmed) => confirmed)
      )
      .subscribe(onConfirm);
  }
}
