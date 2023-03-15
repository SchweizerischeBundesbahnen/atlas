import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { ErrorNotificationComponent } from './error-notification.component';
import { RouterModule } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';

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

let component: ErrorNotificationComponent;
let fixture: ComponentFixture<ErrorNotificationComponent>;

describe('Error Notification component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ErrorNotificationComponent],
      imports: [
        RouterModule.forRoot([]),
        HttpClientTestingModule,
        ReactiveFormsModule,
        MaterialModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        {
          provide: MatSnackBarRef,
          useValue: {},
        },
        {
          provide: MAT_SNACK_BAR_DATA,
          useValue: {}, // Add any data you wish to test if it is passed/used correctly
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show error response', () => {
    //when
    component.notificationService.error(errorResponse);

    //then
    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toContain('TTFN.CONFLICT.NUMBER');
    expect(snackBarContainer.classList).toContain('error');
  });

  it('should show code error', () => {
    //when
    component.notificationService.error(errorResponse, 'CODE_ERROR');

    //then
    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toContain('CODE_ERROR');
    expect(snackBarContainer.classList).toContain('error');
  });

  it('should show generic error', () => {
    // given
    const errorResponse = new HttpErrorResponse({
      status: 404,
    });
    //when
    component.notificationService.error(errorResponse, 'GENERIC_ERROR');

    //then
    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toContain('GENERIC_ERROR');
    expect(snackBarContainer.classList).toContain('error');
  });

  it('should show warning ', () => {
    // given
    const errorResponse = new HttpErrorResponse({
      status: 520,
      error: {
        details: [
          {
            message: 'No entities were modified after versioning execution.',
            field: null,
            displayInfo: {
              code: 'ERROR.WARNING.VERSIONING_NO_CHANGES',
              parameters: [],
            },
          },
        ],
      },
    });
    //when
    component.notificationService.error(errorResponse);

    //then
    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toContain('ERROR.WARNING.VERSIONING_NO_CHANGES');
    expect(snackBarContainer.classList).toContain('warning');
  });
});
