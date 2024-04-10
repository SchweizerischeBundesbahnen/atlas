import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableFieldNumberDetailComponent } from './timetable-field-number-detail.component';
import { AbstractControl, FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TimetableFieldNumbersService, TimetableFieldNumberVersion } from '../../../api';
import moment from 'moment';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { HomeComponent } from '../../home/home.component';
import { HttpErrorResponse } from '@angular/common/http';
import { AppTestingModule, authServiceMock } from '../../../app.testing.module';
import { AuthService } from '../../../core/auth/auth.service';
import { FormModule } from '../../../core/module/form.module';
import { Component, Input } from '@angular/core';
import { ErrorNotificationComponent } from '../../../core/notification/error/error-notification.component';
import { InfoIconComponent } from '../../../core/form-components/info-icon/info-icon.component';
import { MockAppDetailWrapperComponent, MockBoSelectComponent } from '../../../app.testing.mocks';
import { CommentComponent } from '../../../core/form-components/comment/comment.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasFieldErrorComponent } from '../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { TextFieldComponent } from '../../../core/form-components/text-field/text-field.component';
import { Page } from '../../../core/model/page';
import { Record } from '../../../core/components/base-detail/record';

const version: TimetableFieldNumberVersion = {
  id: 1,
  ttfnid: 'ttfnid',
  description: 'description',
  swissTimetableFieldNumber: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  number: '1.1',
  businessOrganisation: 'sbb',
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

const mockData = {
  timetableFieldNumberDetail: version,
};

@Component({
  selector: 'app-coverage',
  template: '<p>Mock Product Editor Component</p>',
})
class MockAppCoverageComponent {
  @Input() pageType!: Page;
  @Input() currentRecord!: Record;
}

let component: TimetableFieldNumberDetailComponent;
let fixture: ComponentFixture<TimetableFieldNumberDetailComponent>;

describe('TimetableFieldNumberDetailComponent detail page read version', () => {
  let router: Router;
  const mockTimetableFieldNumbersService = jasmine.createSpyObj('timetableFieldNumbersService', [
    'updateVersionWithVersioning',
    'deleteVersions',
  ]);
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        TimetableFieldNumberDetailComponent,
        MockAppCoverageComponent,
        MockAppDetailWrapperComponent,
        MockBoSelectComponent,
        ErrorNotificationComponent,
        InfoIconComponent,
        AtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
        TextFieldComponent,
        CommentComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: FormBuilder },
        { provide: TimetableFieldNumbersService, useValue: mockTimetableFieldNumbersService },
        { provide: AuthService, useValue: authServiceMock },
        { provide: ActivatedRoute, useValue: { snapshot: { data: mockData } } },
        { provide: TranslatePipe },
      ],
    })
      .compileComponents()
      .then();
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

  it('should update Version successfully', () => {
    mockTimetableFieldNumbersService.updateVersionWithVersioning.and.returnValue(of(version));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe('TTFN.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    mockTimetableFieldNumbersService.updateVersionWithVersioning.and.returnValue(
      throwError(() => error),
    );
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    expect(component.form.enabled).toBeTrue();
  });

  it('should delete Version successfully', () => {
    mockTimetableFieldNumbersService.deleteVersions.and.returnValue(of({}));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe('TTFN.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
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
      declarations: [
        TimetableFieldNumberDetailComponent,
        MockAppDetailWrapperComponent,
        MockAppCoverageComponent,
        ErrorNotificationComponent,
        InfoIconComponent,
      ],
      imports: [
        RouterTestingModule.withRoutes([{ path: '', component: HomeComponent }]),
        AppTestingModule,
        FormModule,
      ],
      providers: [
        { provide: FormBuilder },
        { provide: TimetableFieldNumbersService, useValue: mockTimetableFieldNumbersService },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { data: { timetableFieldNumberDetail: 'add' } } },
        },
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
        { provide: TranslatePipe },
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

  describe('Validation swissTimeTableFieldNumber', () => {
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

  describe('Validation businessOrganisation', () => {
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

  describe('Validation number', () => {
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

  describe('Validation description', () => {
    it('should not be greater then 255', () => {
      const description: AbstractControl = fixture.componentInstance.form.controls['description'];
      description.setValue(loremIpsum256Chars);
      description.markAsTouched();

      const validationErrors = description.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.maxlength).toBeDefined();
    });
  });

  describe('Validation validFrom', () => {
    it('should be required', () => {
      const validFrom: AbstractControl = fixture.componentInstance.form.controls['validFrom'];
      validFrom.markAsTouched();

      const validationErrors = validFrom.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.required).toBeDefined();
    });

    it('should not be less then 01.01.1700', () => {
      const validFrom: AbstractControl = fixture.componentInstance.form.controls['validFrom'];
      validFrom.setValue(moment('1699-12-01 00:00:00'));
      validFrom.markAsTouched();

      const validationErrors = validFrom.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.matDatepickerMin).toBeDefined();
    });
  });

  describe('Validation validTo', () => {
    it('should be required', () => {
      const validTo: AbstractControl = fixture.componentInstance.form.controls['validTo'];
      validTo.markAsTouched();

      const validationErrors = validTo.errors;

      expect(validationErrors).toBeDefined();
      expect(validationErrors?.required).toBeDefined();
    });

    it('should not be greater than 31.12.9999', () => {
      const validTo: AbstractControl = fixture.componentInstance.form.controls['validTo'];
      validTo.setValue(moment('1.12.10000', 'dd.MM.yyyy'));
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
        fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent.trim()).toBe('TTFN.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('should not create a new record', () => {
      mockTimetableFieldNumbersService.createVersion.and.returnValue(throwError(() => error));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      expect(component.form.enabled).toBeTrue();
    });
  });
});
