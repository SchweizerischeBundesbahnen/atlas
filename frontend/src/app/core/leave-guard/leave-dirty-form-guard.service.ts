import { inject, Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanDeactivateFn,
  RouterStateSnapshot,
} from '@angular/router';
import { DialogService } from '../components/dialog/dialog.service';
import { FormGroup } from '@angular/forms';

export interface DetailFormComponent {
  form?: FormGroup;
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
    nextState: RouterStateSnapshot
  ) {
    if (this.staysOnSameDetailPage(currentState, nextState)) {
      return true;
    }

    if (component.form && component.form.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }

    return true;
  }

  staysOnSameDetailPage(
    currentState: RouterStateSnapshot,
    nextState: RouterStateSnapshot
  ) {
    //if from new to created url
    const indexOfAdd = currentState.url.indexOf('/add');
    const addPresentInUrl = indexOfAdd !== -1;
    if (addPresentInUrl) {
      const urlBeforeAdd = currentState.url.substring(0, indexOfAdd);
      if (nextState.url.startsWith(urlBeforeAdd)) {
        return true;
      }
    }
    return nextState.url.startsWith(currentState.url);
  }
}

export const canLeaveDirtyForm: CanDeactivateFn<DetailFormComponent> = (
  component: DetailFormComponent,
  currentRoute: ActivatedRouteSnapshot,
  currentState: RouterStateSnapshot,
  nextState: RouterStateSnapshot
) => {
  return inject(LeaveDirtyFormGuard).canDeactivate(
    component,
    currentRoute,
    currentState,
    nextState
  );
};
