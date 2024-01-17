import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateRangeComponent } from './date-range.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormControl, FormGroup } from '@angular/forms';
import { DateIconComponent } from '../date-icon/date-icon.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import { InfoIconComponent } from '../info-icon/info-icon.component';

describe('DateRangeComponent', () => {
  let component: DateRangeComponent;
  let fixture: ComponentFixture<DateRangeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        DateRangeComponent,
        DateIconComponent,
        AtlasFieldErrorComponent,
        InfoIconComponent,
        AtlasLabelFieldComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DateRangeComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      validFrom: new FormControl(),
      validTo: new FormControl(),
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
