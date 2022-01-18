import { Injectable, OnDestroy } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig, MatSnackBarRef } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { NotificationParamMessage } from './notification-param-message';
import { catchError, Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { ErrorNotificationComponent } from './error-notification.component';
import { DisplayInfo } from '../../api';
import { NavigationStart, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class NotificationService implements OnDestroy {
  private ngUnsubscribe = new Subject<void>();

  SNACK_BAR_CONFIG: MatSnackBarConfig = {
    horizontalPosition: 'right',
    verticalPosition: 'top',
  };

  constructor(
    private snackBar: MatSnackBar,
    private translateService: TranslateService,
    private router: Router
  ) {}

  success(msg: string, param?: NotificationParamMessage) {
    this.notify(msg, 'success', param);
  }

  error(errorResponse: HttpErrorResponse) {
    this.SNACK_BAR_CONFIG['duration'] = undefined;
    this.SNACK_BAR_CONFIG['panelClass'] = ['error', 'notification'];
    if (errorResponse.status.toString().startsWith('5')) {
      this.SNACK_BAR_CONFIG['data'] = 'NOTIFICATION.COMMON_ERROR';
    } else {
      this.SNACK_BAR_CONFIG['data'] = errorResponse.error;
    }
    const errorSnackBar = this.snackBar.openFromComponent(
      ErrorNotificationComponent,
      this.SNACK_BAR_CONFIG
    );
    this.dismissOnNavigation(errorSnackBar);
  }

  private dismissOnNavigation(errorSnackBar: MatSnackBarRef<ErrorNotificationComponent>) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationStart))
      .pipe(
        takeUntil(this.ngUnsubscribe),
        catchError((err) => {
          throw err;
        })
      )
      .subscribe(() => {
        errorSnackBar.dismiss();
      });
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
        this.SNACK_BAR_CONFIG['duration'] = 5000;
        this.SNACK_BAR_CONFIG['data'] = undefined;
        this.SNACK_BAR_CONFIG['panelClass'] = [type, 'notification'];
        this.snackBar.open(value, '', this.SNACK_BAR_CONFIG);
      });
  }

  arrayParametersToObject(displayInfo: DisplayInfo) {
    return Object.fromEntries(displayInfo.parameters.map((e) => [e.key, e.value]));
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
