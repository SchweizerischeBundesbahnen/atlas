import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableFieldNumberDetailComponent } from './timetable-field-number-detail.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  ValidationErrors,
} from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TimetableFieldNumbersService, Version } from '../../api';
import { MaterialModule } from '../../core/module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DetailWrapperComponent } from '../../core/components/detail-wrapper/detail-wrapper.component';
import moment from 'moment/moment';

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
        { provide: TimetableFieldNumbersService },
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
});

describe('TimetableFieldNumberDetailComponent Detail page add new version', () => {
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
        { provide: TimetableFieldNumbersService },
        {
          provide: ActivatedRoute,
          useValue: routeSnapshotVersionAddMock,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimetableFieldNumberDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate validFrom and validTrue', () => {
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
});
