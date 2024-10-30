import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { catchError, EMPTY, Observable, retry } from 'rxjs';
import { NotificationService } from '../notification/notification.service';

@Injectable()
export class ServerErrorInterceptor implements HttpInterceptor {
  constructor(private notificationService: NotificationService) {}

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      retry(0),
      catchError((error: HttpErrorResponse) => {
        if (!this.isCypressRefreshTokenError(error)) {
          this.notificationService.error(error);
          throw error;
        }
        console.error(error);
        return EMPTY;
      }),
    );
  }

  isCypressRefreshTokenError(error: HttpErrorResponse) {
    return (
      error.error?.error === 'invalid_grant' &&
      error.status === 400 &&
      error.name == 'HttpErrorResponse' &&
      error.statusText === 'Bad Request' &&
      error.url?.includes('https://login.microsoftonline.com/') &&
      error.message.includes('Http failure response for https://login.microsoftonline.com/')
    );
  }
}
