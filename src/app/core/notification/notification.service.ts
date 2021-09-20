import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  SNACK_BAR_CONFIG: MatSnackBarConfig = {
    duration: 3000,
    horizontalPosition: 'right',
    verticalPosition: 'top',
  };

  constructor(private snackBar: MatSnackBar, private translateService: TranslateService) {}

  success(msg: string) {
    this.notify(msg, 'success');
  }

  error(msg: string) {
    this.notify(msg, 'error');
  }

  warning(msg: string) {
    this.notify(msg, 'warning');
  }

  notify(msg: string, type: string) {
    this.translateService.get(msg).subscribe((value) => {
      this.SNACK_BAR_CONFIG['panelClass'] = [type, 'notification'];
      this.snackBar.open(value, '', this.SNACK_BAR_CONFIG);
    });
  }
}
