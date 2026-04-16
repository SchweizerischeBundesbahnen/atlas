import { beforeEach, describe, expect, it } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ErrorNotificationComponent } from './error-notification.component';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { By } from '@angular/platform-browser';

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

describe('Error Notification component', () => {
  let component: ErrorNotificationComponent;
  let fixture: ComponentFixture<ErrorNotificationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        translateServiceProvider,
        {
          provide: MatSnackBarRef,
          useValue: {},
        },
        {
          provide: MAT_SNACK_BAR_DATA,
          useValue: errorResponse.error,
        },
      ],
    });

    fixture = TestBed.createComponent(ErrorNotificationComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display details', () => {
    component.notificationService.error(errorResponse);
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('li')).nativeElement.textContent).include('TTFN.CONFLICT.NUMBER');
  });
});
