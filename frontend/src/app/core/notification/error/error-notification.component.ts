import { Component, inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { NotificationService } from '../notification.service';
import { ErrorResponse } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-error-notification',
  templateUrl: './error-notification.component.html',
  styleUrls: ['./error-notification.component.scss'],
  imports: [TranslatePipe],
  providers: [TranslatePipe],
})
export class ErrorNotificationComponent {
  protected readonly snackBarRef = inject(MatSnackBarRef<ErrorNotificationComponent>);
  protected readonly notificationService = inject(NotificationService);
  protected readonly data: ErrorResponse = inject(MAT_SNACK_BAR_DATA);
}
