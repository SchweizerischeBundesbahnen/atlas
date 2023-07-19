import { inject, Injectable } from '@angular/core';
import { CanDeactivateFn } from '@angular/router';
import { of } from 'rxjs';
import { DialogService } from '../components/dialog/dialog.service';

export interface DetailFormComponent {
  isFormDirty: () => boolean;
}

@Injectable({
  providedIn: 'root',
})
export class LeaveDirtyFormGuard {
  constructor(private dialogService: DialogService) {}

  canDeactivate(component: DetailFormComponent) {
    if (component.isFormDirty()) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }
}

export const canLeaveDirtyForm: CanDeactivateFn<DetailFormComponent> = (component) => {
  return inject(LeaveDirtyFormGuard).canDeactivate(component);
};
