import { inject, Injectable, OnInit } from '@angular/core';
import { Observable, of, take } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { DialogService } from '../components/dialog/dialog.service';
import { filter } from 'rxjs/operators';

export interface DetailWithCancelEdit extends OnInit {
  isNew: boolean;
  back: () => void;
  form: FormGroup;
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
    return this.confirmLeaveDirtyForm(detail.form);
  }

  confirmLeaveDirtyForm(form: FormGroup): Observable<boolean> {
    if (form.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }

  confirmWarning(
    labels: { message: string; confirmText: string },
    onConfirm: () => void
  ) {
    this.dialogService
      .confirm({
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
