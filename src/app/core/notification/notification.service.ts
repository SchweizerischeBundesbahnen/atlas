import { Injectable, OnDestroy } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { NotificationParamMessage } from './notification-param-message';
import { catchError, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class NotificationService implements OnDestroy {
  private ngUnsubscribe = new Subject<void>();

  SNACK_BAR_CONFIG: MatSnackBarConfig = {
    duration: 5000,
    horizontalPosition: 'right',
    verticalPosition: 'top',
  };

  constructor(private snackBar: MatSnackBar, private translateService: TranslateService) {}

  success(msg: string, param?: NotificationParamMessage) {
    this.notify(msg, 'success', param);
  }

  error(msg: string, param?: NotificationParamMessage) {
    this.notify(msg, 'error', param);
  }

  info(msg: string, param?: NotificationParamMessage) {
    this.notify(msg, 'info', param);
  }

  notify(msg: string, type: string, param?: NotificationParamMessage) {
    this.translateService
      .get(msg, param)
      .pipe(
        takeUntil(this.ngUnsubscribe),
        catchError((err) => {
          console.log(err);
          throw err;
        })
      )
      .subscribe((value) => {
        this.SNACK_BAR_CONFIG['panelClass'] = [type, 'notification'];
        this.snackBar.open(value, '', this.SNACK_BAR_CONFIG);
      });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
