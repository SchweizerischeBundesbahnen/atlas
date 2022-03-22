import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { LinesService, LineType, LineVersion, PaymentType } from '../../../../api';
import { LineDetailComponent } from './line-detail.component';
import { CoreModule } from '../../../../core/module/core.module';
import { HttpErrorResponse } from '@angular/common/http';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MaterialModule } from '../../../../core/module/material.module';

const lineVersion: LineVersion = {
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: 'ACTIVE',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  paymentType: PaymentType.None,
  swissLineNumber: 'L1',
  type: LineType.Orderly,
  colorBackCmyk: '',
  colorBackRgb: '',
  colorFontCmyk: '',
  colorFontRgb: '',
};

const error = new HttpErrorResponse({
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

let component: LineDetailComponent;
let fixture: ComponentFixture<LineDetailComponent>;
let router: Router;

describe('LineDetailComponent for existing lineVersion', () => {
  const mockLinesService = jasmine.createSpyObj('linesService', [
    'updateLineVersion',
    'deleteLines',
  ]);
  const mockData = {
    lineDetail: lineVersion,
  };

  beforeEach(() => {
    setupTestBed(mockLinesService, mockData);

    fixture = TestBed.createComponent(LineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should update LineVersion successfully', () => {
    mockLinesService.updateLineVersion.and.returnValue(of(lineVersion));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.LINE.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    mockLinesService.updateLineVersion.and.returnValue(throwError(() => error));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toContain('TTFN.CONFLICT.NUMBER');
    expect(snackBarContainer.classList).toContain('error');
  });

  it('should delete LineVersion successfully', () => {
    mockLinesService.deleteLines.and.returnValue(of({}));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.LINE.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not delete Version', () => {
    const httpResponseError = new HttpErrorResponse({
      status: 404,
    });
    mockLinesService.deleteLines.and.returnValue(throwError(() => httpResponseError));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toContain('LIDI.LINE.NOTIFICATION.DELETE_ERROR');
    expect(snackBarContainer.classList).toContain('error');
  });
});

describe('LineDetailComponent for new lineVersion', () => {
  const mockLinesService = jasmine.createSpyObj('linesService', ['createLineVersion']);
  const mockData = {
    lineDetail: 'add',
  };

  beforeEach(() => {
    setupTestBed(mockLinesService, mockData);

    fixture = TestBed.createComponent(LineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('create new Version', () => {
    it('successfully', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      mockLinesService.createLineVersion.and.returnValue(of(lineVersion));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent).toBe('LIDI.LINE.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('displaying error', () => {
      mockLinesService.createLineVersion.and.returnValue(throwError(() => error));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent).toContain('TTFN.CONFLICT.NUMBER');
      expect(snackBarContainer.classList).toContain('error');
    });
  });
});

function setupTestBed(linesService: LinesService, data: { lineDetail: string | LineVersion }) {
  TestBed.configureTestingModule({
    declarations: [LineDetailComponent],
    imports: [
      CoreModule,
      RouterModule.forRoot([]),
      HttpClientTestingModule,
      BrowserAnimationsModule,
      MatDialogModule,
      TranslateModule.forRoot({
        loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
      }),
    ],
    providers: [
      { provide: FormBuilder },
      { provide: LinesService, useValue: linesService },
      {
        provide: MAT_DIALOG_DATA,
        useValue: data,
      },
    ],
  })
    .compileComponents()
    .then();
}
