import { Component, Inject } from '@angular/core';
import {
  MAT_LEGACY_SNACK_BAR_DATA as MAT_SNACK_BAR_DATA,
  MatLegacySnackBarRef as MatSnackBarRef,
} from '@angular/material/legacy-snack-bar';
import { ErrorResponse } from '../../../api';
import { NotificationService } from '../notification.service';

@Component({
  selector: 'error-notification',
  templateUrl: './error-notification.component.html',
  styleUrls: ['./error-notification.component.scss'],
})
export class ErrorNotificationComponent {
  constructor(
    public snackBarRef: MatSnackBarRef<ErrorNotificationComponent>,
    public notificationService: NotificationService,
    @Inject(MAT_SNACK_BAR_DATA) public data: ErrorResponse
  ) {}
}
