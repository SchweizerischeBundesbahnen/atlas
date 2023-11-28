import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanDeactivateFn, RouterStateSnapshot } from '@angular/router';
import { DialogService } from '../components/dialog/dialog.service';

export interface DetailFormComponent {
  isFormDirty: () => boolean;
}

@Injectable({
  providedIn: 'root',
})
export class LeaveDirtyFormGuard {
  constructor(private dialogService: DialogService) {}

  canDeactivate(
    component: DetailFormComponent,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState: RouterStateSnapshot,
  ) {
    if (this.staysOnSameDetailPage(currentState, nextState)) {
      return true;
    }

    if (component.isFormDirty()) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }

    return true;
  }

  staysOnSameDetailPage(currentState: RouterStateSnapshot, nextState: RouterStateSnapshot) {
    //if from new to created url
    const substringAfterLastSlash = this.getSubstringAfterLastSlash(currentState.url);
    if (substringAfterLastSlash === 'add') {
      if (
        this.getSubstringBeforeLastSlash(currentState.url) ===
        this.getSubstringBeforeLastSlash(nextState.url)
      ) {
        return true;
      }
    }
    return currentState.url === nextState.url;
  }

  private getSubstringAfterLastSlash(value: string) {
    return value.substring(value.lastIndexOf('/') + 1, value.length);
  }

  private getSubstringBeforeLastSlash(value: string) {
    return value.substring(0, value.lastIndexOf('/'));
  }
}

export const canLeaveDirtyForm: CanDeactivateFn<DetailFormComponent> = (
  component: DetailFormComponent,
  currentRoute: ActivatedRouteSnapshot,
  currentState: RouterStateSnapshot,
  nextState: RouterStateSnapshot,
) => {
  return inject(LeaveDirtyFormGuard).canDeactivate(
    component,
    currentRoute,
    currentState,
    nextState,
  );
};
