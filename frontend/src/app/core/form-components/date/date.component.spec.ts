import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { DateComponent } from './date.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormControl, FormGroup } from '@angular/forms';
import { DateIconComponent } from '../date-icon/date-icon.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent, InfoIconComponent } from '@atlas/form';
import { TranslatePipe } from '@ngx-translate/core';
import { DateRangeValidator } from '../../validation/date-range/date-range-validator';
import { MatDatepicker } from '@angular/material/datepicker';

describe('DateComponent', () => {
  let component: DateComponent;
  let fixture: ComponentFixture<DateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        MatDatepicker,
        DateComponent,
        DateIconComponent,
        AtlasFieldErrorComponent,
        InfoIconComponent,
        AtlasLabelFieldComponent,
        TranslatePipe,
      ],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DateComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup(
      {
        validFrom: new FormControl(),
        validTo: new FormControl(),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
    fixture.detectChanges();
  });

  it('MIN_DATE and MAX_DATE should be defined', () => {
    expect(component.minDate()).toBeDefined();
    expect(component.maxDate()).toBeDefined();
  });
});
