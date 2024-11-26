import { ErrorHandler, inject, Injectable } from '@angular/core';
import { NotificationService } from '../notification/notification.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  private readonly notificationService = inject(NotificationService);

  handleError(error: Error) {
    this.notificationService.error(error);
  }
}
