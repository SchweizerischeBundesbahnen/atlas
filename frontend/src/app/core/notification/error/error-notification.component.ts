import { Component, Inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { NotificationService } from '../notification.service';
import { ErrorResponse } from '../../../api';

@Component({
  selector: 'error-notification',
  templateUrl: './error-notification.component.html',
  styleUrls: ['./error-notification.component.scss'],
})
export class ErrorNotificationComponent {
  constructor(
    public snackBarRef: MatSnackBarRef<ErrorNotificationComponent>,
    public notificationService: NotificationService,
    @Inject(MAT_SNACK_BAR_DATA) public data: ErrorResponse,
  ) {}
}
