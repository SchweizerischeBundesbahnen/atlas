import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LinesService, LineType, LineVersion, PaymentType } from '../../../../api';
import { LineDetailComponent } from './line-detail.component';
import { HttpErrorResponse } from '@angular/common/http';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { InfoIconComponent } from '../../../../core/form-components/info-icon/info-icon.component';
import { MockAppDetailWrapperComponent } from '../../../../app.testing.mocks';

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
  lineType: LineType.Orderly,
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
let dialogRef: MatDialogRef<LineDetailComponent>;

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
    dialogRef = TestBed.inject(MatDialogRef);
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

    expect(component.form.enabled).toBeTrue();
  });

  it('should delete LineVersion successfully', () => {
    mockLinesService.deleteLines.and.returnValue(of({}));
    spyOn(dialogRef, 'close');
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('LIDI.LINE.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(dialogRef.close).toHaveBeenCalled();
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

      expect(component.form.enabled).toBeTrue();
    });
  });
});

function setupTestBed(linesService: LinesService, data: { lineDetail: string | LineVersion }) {
  TestBed.configureTestingModule({
    declarations: [
      LineDetailComponent,
      MockAppDetailWrapperComponent,
      ErrorNotificationComponent,
      InfoIconComponent,
    ],
    imports: [AppTestingModule],
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
