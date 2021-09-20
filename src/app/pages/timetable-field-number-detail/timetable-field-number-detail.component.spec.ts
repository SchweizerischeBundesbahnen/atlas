import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableFieldNumberDetailComponent } from './timetable-field-number-detail.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AbstractControl, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TimetableFieldNumbersService, Version } from '../../api';
import { MaterialModule } from '../../core/module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DetailWrapperComponent } from '../../core/components/detail-wrapper/detail-wrapper.component';
import moment from 'moment/moment';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { HomeComponent } from '../home/home.component';

const version: Version = {
  id: 1,
  ttfnid: 'ttfnid',
  name: 'name',
  swissTimetableFieldNumber: 'asdf',
  status: 'ACTIVE',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
};

const routeSnapshotVersionReadMock = {
  snapshot: {
    paramMap: {},
    data: {
      timetableFieldNumberDetail: version,
    },
  },
};

const routeSnapshotVersionAddMock = {
  snapshot: {
    paramMap: {},
    data: {
      timetableFieldNumberDetail: 'add',
    },
  },
};
let component: TimetableFieldNumberDetailComponent;
let fixture: ComponentFixture<TimetableFieldNumberDetailComponent>;

describe('TimetableFieldNumberDetailComponent detail page read version', () => {
  let router: Router;
  const mockTimetableFieldNumbersService = jasmine.createSpyObj('timetableFieldNumbersService', [
    'updateVersion',
    'deleteVersion',
  ]);
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableFieldNumberDetailComponent, DetailWrapperComponent],
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
        { provide: FormBuilder },
        { provide: TimetableFieldNumbersService, useValue: mockTimetableFieldNumbersService },
        {
          provide: ActivatedRoute,
          useValue: routeSnapshotVersionReadMock,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimetableFieldNumberDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get valid from placeholder', () => {
    const fixedDate = new Date(2020, 11, 31);
    jasmine.clock().install();
    jasmine.clock().mockDate(fixedDate);
    const result = fixture.componentInstance.getValidFromPlaceHolder();

    expect(result).toBe('31.12.2020');
  });

  it('should update Version successfully', () => {
    mockTimetableFieldNumbersService.updateVersion.and.returnValue(of(version));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('TTFN.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    const error = new Error('401');
    mockTimetableFieldNumbersService.updateVersion.and.returnValue(throwError(() => error));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('TTFN.NOTIFICATION.EDIT_ERROR');
    expect(snackBarContainer.classList).toContain('error');
  });

  it('should delete Version successfully', () => {
    mockTimetableFieldNumbersService.deleteVersion.and.returnValue(of({}));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('TTFN.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not delete Version', () => {
    const error = new Error('401');
    mockTimetableFieldNumbersService.deleteVersion.and.returnValue(throwError(() => error));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent).toBe('TTFN.NOTIFICATION.DELETE_ERROR');
    expect(snackBarContainer.classList).toContain('error');
  });
});

describe('TimetableFieldNumberDetailComponent Detail page add new version', () => {
  const loremIpsum256Chars =
    'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis,s';

  const mockTimetableFieldNumbersService = jasmine.createSpyObj('timetableFieldNumbersService', [
    'createVersion',
  ]);
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableFieldNumberDetailComponent, DetailWrapperComponent],
      imports: [
        RouterModule.forRoot([]),
        RouterTestingModule.withRoutes([{ path: '', component: HomeComponent }]),
        HttpClientTestingModule,
        ReactiveFormsModule,
        MaterialModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        { provide: FormBuilder },
        { provide: TimetableFieldNumbersService, useValue: mockTimetableFieldNumbersService },
        {
          provide: ActivatedRoute,
          useValue: routeSnapshotVersionAddMock,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimetableFieldNumberDetailComponent);
    router = TestBed.inject(Router);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate validFrom and validTrue range', () => {
    const validFrom: AbstractControl = fixture.componentInstance.form.controls['validFrom'];
    const validTo: AbstractControl = fixture.componentInstance.form.controls['validTo'];
    validFrom.setValue(moment('31.10.2000', 'dd.MM.yyyy'));
    validFrom.markAsTouched();
    validTo.setValue(moment('31.10.1999', 'dd.MM.yyyy'));
    validTo.markAsTouched();
    fixture.detectChanges();

    const validFromErrors = validFrom.errors;
    expect(validFromErrors).toBeDefined();
    expect(validFromErrors?.date_range_error).toBeDefined();
    const validToErrors = validTo.errors;
    expect(validToErrors).toBeDefined();
    expect(validToErrors?.date_range_error).toBeDefined();
  });

  describe('Validation swissTimeTableFieldNumber ', () => {
    it('should be required', () => {
      const swissTimeTableFieldNumber: AbstractControl =
        fixture.componentInstance.form.controls['swissTimetableFieldNumber'];
      swissTimeTableFieldNumber.markAsTouched();

      const validationErrors = swissTimeTableFieldNumber.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.required).toBeDefined();
    });

    it('should not be greater then 255', () => {
      const swissTimeTableFieldNumber: AbstractControl =
        fixture.componentInstance.form.controls['swissTimetableFieldNumber'];
      swissTimeTableFieldNumber.setValue(loremIpsum256Chars);
      swissTimeTableFieldNumber.markAsTouched();

      const validationErrors = swissTimeTableFieldNumber.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.maxlength).toBeDefined();
    });
  });

  describe('Validation ttfnid ', () => {
    it('should be required', () => {
      const ttfndi: AbstractControl = fixture.componentInstance.form.controls['ttfnid'];
      ttfndi.markAsTouched();

      const validationErrors = ttfndi.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.required).toBeDefined();
    });

    it('should not be greater then 255', () => {
      const ttfnid: AbstractControl = fixture.componentInstance.form.controls['ttfnid'];
      ttfnid.setValue(loremIpsum256Chars);
      ttfnid.markAsTouched();

      const validationErrors = ttfnid.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.maxlength).toBeDefined();
    });
  });

  describe('Validation businessOrganisation ', () => {
    it('should be required', () => {
      const businessOrganisation: AbstractControl =
        fixture.componentInstance.form.controls['businessOrganisation'];
      businessOrganisation.markAsTouched();

      const validationErrors = businessOrganisation.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.required).toBeDefined();
    });

    it('should not be greater then 255', () => {
      const businessOrganisation: AbstractControl =
        fixture.componentInstance.form.controls['businessOrganisation'];
      businessOrganisation.setValue(loremIpsum256Chars);
      businessOrganisation.markAsTouched();

      const validationErrors = businessOrganisation.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.maxlength).toBeDefined();
    });
  });

  describe('Validation number ', () => {
    it('should be required', () => {
      const number: AbstractControl = fixture.componentInstance.form.controls['number'];
      number.markAsTouched();

      const validationErrors = number.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.required).toBeDefined();
    });

    it('should not be greater then 255', () => {
      const number: AbstractControl = fixture.componentInstance.form.controls['number'];
      number.setValue(loremIpsum256Chars);
      number.markAsTouched();

      const validationErrors = number.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.maxlength).toBeDefined();
    });
  });

  describe('Validation name ', () => {
    it('should be required', () => {
      const name: AbstractControl = fixture.componentInstance.form.controls['name'];
      name.markAsTouched();

      const validationErrors = name.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.required).toBeDefined();
    });

    it('should not be greater then 255', () => {
      const name: AbstractControl = fixture.componentInstance.form.controls['name'];
      name.setValue(loremIpsum256Chars);
      name.markAsTouched();

      const validationErrors = name.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.maxlength).toBeDefined();
    });
  });

  describe('Validation validFrom ', () => {
    it('should be required', () => {
      const validFrom: AbstractControl = fixture.componentInstance.form.controls['validFrom'];
      validFrom.markAsTouched();

      const validationErrors = validFrom.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.required).toBeDefined();
    });

    it('should not be less then today', () => {
      const validFrom: AbstractControl = fixture.componentInstance.form.controls['validFrom'];
      validFrom.setValue(moment().subtract(1, 'days'));
      validFrom.markAsTouched();

      const validationErrors = validFrom.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.matDatepickerMin).toBeDefined();
    });
  });

  describe('Validation validTo ', () => {
    it('should be required', () => {
      const validTo: AbstractControl = fixture.componentInstance.form.controls['validTo'];
      validTo.markAsTouched();

      const validationErrors = validTo.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.required).toBeDefined();
    });

    it('should not be greater then 31.12.2099', () => {
      const validTo: AbstractControl = fixture.componentInstance.form.controls['validTo'];
      validTo.setValue(moment('1.12.2100', 'dd.MM.yyyy'));
      validTo.markAsTouched();

      const validationErrors = validTo.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.matDatepickerMax).toBeDefined();
    });
  });

  describe('Create new Version', () => {
    it('should create successfully a new record', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      mockTimetableFieldNumbersService.createVersion.and.returnValue(of(version));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent).toBe('TTFN.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('should not create a new record', () => {
      const err = new Error('404');
      mockTimetableFieldNumbersService.createVersion.and.returnValue(throwError(() => err));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent).toBe('TTFN.NOTIFICATION.ADD_ERROR');
      expect(snackBarContainer.classList).toContain('error');
    });
  });
});
