import { beforeEach, describe, expect, it } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DateRangeTextComponent } from './date-range-text.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { inputBinding } from '@angular/core';

describe('DateRangeTextComponent', () => {
  let component: DateRangeTextComponent;
  let fixture: ComponentFixture<DateRangeTextComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    const dateRangeInputName: keyof DateRangeTextComponent = 'dateRange';
    fixture = TestBed.createComponent(DateRangeTextComponent, {
      bindings: [
        inputBinding(dateRangeInputName, () => ({
          validFrom: new Date('2023-01-01'),
          validTo: new Date('2023-01-31'),
        })),
      ],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
