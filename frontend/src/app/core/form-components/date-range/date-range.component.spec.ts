import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateRangeComponent } from './date-range.component';
import { AppTestingModule } from '../../../app.testing.module';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { DateIconComponent } from '../date-icon/date-icon.component';

describe('DateRangeComponent', () => {
  let component: DateRangeComponent;
  let fixture: ComponentFixture<DateRangeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DateRangeComponent, DateIconComponent],
      imports: [AppTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DateRangeComponent);
    component = fixture.componentInstance;
    component.formGroup = new UntypedFormGroup({
      validFrom: new UntypedFormControl(),
      validTo: new UntypedFormControl(),
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('MIN_DATE and MAX_DATE should be defined', () => {
    expect(component.MIN_DATE).toBeDefined();
    expect(component.MAX_DATE).toBeDefined();
  });
});
