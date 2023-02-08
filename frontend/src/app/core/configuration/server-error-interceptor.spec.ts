import { TestBed } from '@angular/core/testing';

import { ServerErrorInterceptor } from './server-error-interceptor';
import { NotificationService } from '../notification/notification.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpErrorResponse, HttpEventType, HttpHeaders } from '@angular/common/http';

describe('Server Error Interceptor', () => {
  let interceptor: ServerErrorInterceptor;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        MatSnackBarModule,
        TranslateModule.forRoot(),
        RouterModule.forRoot([]),
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [NotificationService, ServerErrorInterceptor],
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
