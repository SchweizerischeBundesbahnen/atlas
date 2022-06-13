import { ApplicationRef, Component } from '@angular/core';

import { LoadingSpinnerService } from './core/components/loading-spinner/loading-spinner.service';
import { animate, style, transition, trigger } from '@angular/animations';
import { SwUpdate } from '@angular/service-worker';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from './core/components/dialog/dialog.component';
import { filter, first } from 'rxjs/operators';
import { concat, interval } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  animations: [
    trigger('fadeInOut', [
      transition('true <=> false', [style({ opacity: 0 }), animate(650, style({ opacity: 1 }))]),
    ]),
  ],
})
export class AppComponent {
  constructor(
    public loadingSpinnerService: LoadingSpinnerService,
    private readonly swUpdate: SwUpdate,
    private readonly dialog: MatDialog,
    private readonly appRef: ApplicationRef
  ) {
    loadingSpinnerService.initLoadingSpinner();

    // TODO: service
    if (swUpdate.isEnabled) {
      const appIsStable$ = appRef.isStable.pipe(first((isStable) => isStable));
      const checkForUpdateInterval$ = interval(60000);
      const checkForUpdate$ = concat(appIsStable$, checkForUpdateInterval$);

      checkForUpdate$.subscribe(() => swUpdate.checkForUpdate());

      swUpdate.versionUpdates
        .pipe(filter((versionEvent) => versionEvent.type === 'VERSION_READY'))
        .subscribe(() => {
          const dialogRef = dialog.open(DialogComponent, {
            data: {
              confirmText: 'DIALOG.RELOAD',
              title: 'Newer version available',
              message: 'There is a new version of the app available. You should reload the page',
            },
            panelClass: 'atlas-dialog-panel',
            backdropClass: 'atlas-dialog-backdrop',
          });

          dialogRef.afterClosed().subscribe((result) => {
            if (result) {
              document.location.reload();
            }
          });
        });
      swUpdate.unrecoverable.subscribe((event) => {
        const dialogRef = dialog.open(DialogComponent, {
          data: {
            message:
              `Your version of the application is not recoverable because:\n${event.reason}\n\n` +
              'Please reload the page. If this error occurs again, try with an hard refresh (Ctrl + F5)',
            confirmText: 'DIALOG.RELOAD',
            title: 'Unrecoverable version',
          },
          panelClass: 'atlas-dialog-panel',
          backdropClass: 'atlas-dialog-backdrop',
        });
        dialogRef.afterClosed().subscribe((result) => {
          if (result) {
            document.location.reload();
          }
        });
      });
    }
  }
}

Date.prototype.toISOString = function () {
  return (
    this.getFullYear() +
    '-' +
    ('0' + (this.getMonth() + 1)).slice(-2) +
    '-' +
    ('0' + this.getDate()).slice(-2)
  );
};
