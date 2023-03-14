import { ApplicationRef, Injectable } from '@angular/core';
import { filter, first } from 'rxjs/operators';
import { concat, interval } from 'rxjs';
import { DialogComponent } from './core/components/dialog/dialog.component';
import { SwUpdate } from '@angular/service-worker';
import { MatLegacyDialog as MatDialog } from '@angular/material/legacy-dialog';

@Injectable()
export class ServiceWorkerService {
  constructor(
    private readonly appRef: ApplicationRef,
    private readonly swUpdate: SwUpdate,
    private readonly dialog: MatDialog
  ) {
    if (swUpdate.isEnabled) {
      const appIsStable$ = appRef.isStable.pipe(first((isStable) => isStable));
      const checkForUpdateInterval$ = interval(300000); // all 5 minutes
      const checkForUpdate$ = concat(appIsStable$, checkForUpdateInterval$);

      checkForUpdate$.subscribe(() => swUpdate.checkForUpdate());

      swUpdate.versionUpdates
        .pipe(filter((versionEvent) => versionEvent.type === 'VERSION_READY'))
        .subscribe(() => this.openSWDialog('SW_DIALOG.UPDATE_TITLE', 'SW_DIALOG.UPDATE_MESSAGE'));

      swUpdate.unrecoverable.subscribe(() =>
        this.openSWDialog('SW_DIALOG.UNRECOVERABLE_TITLE', 'SW_DIALOG.UNRECOVERABLE_MESSAGE')
      );
    }
  }

  private openSWDialog(titleTranslateKey: string, messageTranslateKey: string): void {
    this.dialog
      .open(DialogComponent, {
        data: {
          confirmText: 'DIALOG.RELOAD',
          title: titleTranslateKey,
          message: messageTranslateKey,
        },
        panelClass: 'atlas-dialog-panel',
        backdropClass: 'atlas-dialog-backdrop',
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          ServiceWorkerService.reloadPage();
        }
      });
  }

  private static reloadPage(): void {
    location.reload();
  }
}
