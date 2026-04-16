import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { NotificationService } from './notification.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { ErrorNotificationComponent } from './error/error-notification.component';

const errorResponse = new HttpErrorResponse({
  status: 404,
  error: {
    message: 'Not found',
    details: [
      {
        message: 'Number 111 already taken from 2020-12-12 to 2026-12-12 by ch:1:ttfnid:1001720',
        field: 'number',
        displayInfo: {
          code: 'TTFN.CONFLICT.NUMBER',
          parameters: [
            {
              key: 'number',
              value: '111',
            },
            {
              key: 'validFrom',
              value: '2020-12-12',
            },
            {
              key: 'validTo',
              value: '2026-12-12',
            },
            {
              key: 'ttfnid',
              value: 'ch:1:ttfnid:1001720',
            },
          ],
        },
      },
    ],
  },
});

describe('NotificationService', () => {
  let service: NotificationService;
  let matSnackBarStub: Mocked<Pick<MatSnackBar, 'open' | 'openFromComponent'>>;
  let translateServiceStub: Mocked<Pick<TranslateService, 'get'>>;

  beforeEach(() => {
    // Mocking
    matSnackBarStub = {
      open: vi.fn().mockName('MatSnackBar.open'),
      openFromComponent: vi.fn().mockName('MatSnackBar.openFromComponent'),
    };
    translateServiceStub = {
      get: vi.fn().mockReturnValue(of('Notification with value')),
    };

    // Config
    TestBed.configureTestingModule({
      imports: [MatSnackBarModule, TranslateModule.forRoot(), RouterModule.forRoot([])],
      providers: [
        { provide: TranslateService, useValue: translateServiceStub },
        { provide: MatSnackBar, useValue: matSnackBarStub },
      ],
    });

    // Arrangement
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should be success', () => {
    service.success('msg');
    expect(service).toBeTruthy();
    expect(matSnackBarStub.open).toHaveBeenCalledExactlyOnceWith(
      'Notification with value',
      '',
      Object({
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['success', 'notification'],
      })
    );
  });

  it('should be error', () => {
    service.error(errorResponse);
    expect(service).toBeTruthy();
    expect(matSnackBarStub.openFromComponent).toHaveBeenCalledExactlyOnceWith(
      ErrorNotificationComponent,
      Object({
        horizontalPosition: 'right',
        verticalPosition: 'top',
        duration: undefined,
        panelClass: ['error', 'notification'],
        data: Object({
          message: 'Not found',
          details: [
            Object({
              message: 'Number 111 already taken from 2020-12-12 to 2026-12-12 by ch:1:ttfnid:1001720',
              field: 'number',
              displayInfo: Object({
                code: 'TTFN.CONFLICT.NUMBER',
                parameters: [
                  Object({ key: 'number', value: '111' }),
                  Object({
                    key: 'validFrom',
                    value: '2020-12-12',
                  }),
                  Object({ key: 'validTo', value: '2026-12-12' }),
                  Object({
                    key: 'ttfnid',
                    value: 'ch:1:ttfnid:1001720',
                  }),
                ],
              }),
            }),
          ],
        }),
      })
    );
  });

  it('should be info', () => {
    service.info('msg');
    expect(service).toBeTruthy();
    expect(matSnackBarStub.open).toHaveBeenCalledExactlyOnceWith(
      'Notification with value',
      '',
      Object({
        duration: 5000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['info', 'notification'],
      })
    );
  });
});
