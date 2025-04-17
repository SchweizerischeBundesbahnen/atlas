import { Injectable, OnInit } from '@angular/core';
import { Observable, of, take } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { DialogService } from '../components/dialog/dialog.service';

export interface DetailWithCancelEdit extends OnInit {
  isNew: boolean;
  back: () => void;
  form: FormGroup;
}

@Injectable({
  providedIn: 'root',
})
export class DetailHelperService {
  constructor(private dialogService: DialogService) {}

  public showCancelEditDialog(detail: DetailWithCancelEdit) {
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

  public confirmLeave(detail: DetailWithCancelEdit): Observable<boolean> {
    return this.confirmLeaveDirtyForm(detail.form);
  }

  public confirmLeaveDirtyForm(form: FormGroup): Observable<boolean> {
    if (form.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }
}
