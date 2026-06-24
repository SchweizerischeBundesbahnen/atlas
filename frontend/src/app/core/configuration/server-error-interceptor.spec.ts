import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { ServerErrorInterceptor } from './server-error-interceptor';
import { NotificationService } from '../notification/notification.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterModule } from '@angular/router';
import { HttpErrorResponse, HttpEventType, HttpHeaders } from '@angular/common/http';
import { translateServiceProvider } from '../../app.testing.mocks';

describe('Server Error Interceptor', () => {
  let interceptor: ServerErrorInterceptor;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MatSnackBarModule, RouterModule.forRoot([])],
      providers: [NotificationService, ServerErrorInterceptor, translateServiceProvider],
    });

    interceptor = TestBed.inject(ServerErrorInterceptor);
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  it('should return true when errorResponse is cypress refresh Token', () => {
    const errorResponse: HttpErrorResponse = {
      status: 400,
      url: 'https://login.microsoftonline.com/',
      message: 'Http failure response for https://login.microsoftonline.com/',
      error: {
        error: 'invalid_grant',
      },
      name: 'HttpErrorResponse',
      statusText: 'Bad Request',
      ok: false,
      headers: new HttpHeaders(),
      type: HttpEventType.ResponseHeader,
    };
    expect(interceptor.isCypressRefreshTokenError(errorResponse)).toBeTruthy();
  });

  it('should return false when errorResponse is not cypress refresh Token', () => {
    const errorResponse: HttpErrorResponse = {
      status: 520,
      url: 'https://login.microsoftonline.com/',
      message: 'Http failure response for https://login.microsoftonline.com/',
      error: {
        error: 'invalid_grant',
      },
      name: 'HttpErrorResponse',
      statusText: 'Bad Request',
      ok: false,
      headers: new HttpHeaders(),
      type: HttpEventType.ResponseHeader,
    };
    expect(interceptor.isCypressRefreshTokenError(errorResponse)).toBeFalsy();
  });
});
