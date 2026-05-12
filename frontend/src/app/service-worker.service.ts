import { ApplicationRef, inject, Injectable } from '@angular/core';
import { first } from 'rxjs/operators';
import { concat, interval } from 'rxjs';
import { DialogComponent } from './core/components/dialog/dialog.component';
import { SwUpdate } from '@angular/service-worker';
import { MatDialog } from '@angular/material/dialog';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ServiceWorkerService {
  private readonly appRef = inject(ApplicationRef);
  private readonly swUpdate = inject(SwUpdate);
  private readonly dialog = inject(MatDialog);

  private readonly environmentReleaseNotesUrl: string = environment.atlasReleaseNotes;

  constructor() {
    if (this.swUpdate.isEnabled) {
      const appIsStable$ = this.appRef.isStable.pipe(first((isStable) => isStable));
      const checkForUpdateInterval$ = interval(300000); // all 5 minutes
      const checkForUpdate$ = concat(appIsStable$, checkForUpdateInterval$);

      checkForUpdate$.subscribe(() => this.swUpdate.checkForUpdate());

      this.swUpdate.versionUpdates.pipe().subscribe((versionEvent) => {
        if (versionEvent.type === 'VERSION_READY') {
          this.openSWDialog('SW_DIALOG.UPDATE_TITLE', 'SW_DIALOG.UPDATE_MESSAGE');
        }
      });

      this.swUpdate.unrecoverable.subscribe(() => {
        this.openSWDialog('SW_DIALOG.UNRECOVERABLE_TITLE', 'SW_DIALOG.UNRECOVERABLE_MESSAGE');
      });
    }
  }

  openSWDialog(titleTranslateKey: string, messageTranslateKey: string): void {
    this.dialog
      .open(DialogComponent, {
        data: {
          confirmText: 'DIALOG.RELOAD',
          title: titleTranslateKey,
          message: messageTranslateKey,
          link: {
            url: this.environmentReleaseNotesUrl,
            textLink: 'Release Notes',
            text: 'SW_DIALOG.NEW_RELEASE_TEXT',
          },
        },
        panelClass: 'atlas-dialog-panel',
        backdropClass: 'atlas-dialog-backdrop',
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.reloadPage();
        }
      });
  }

  reloadPage(): void {
    location.reload();
  }
}
