import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateRangeTextComponent } from './date-range-text.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AppTestingModule } from '../../../app.testing.module';
import { DisplayDatePipe } from '../../pipe/display-date.pipe';

describe('DateRangeTextComponent', () => {
  let component: DateRangeTextComponent;
  let fixture: ComponentFixture<DateRangeTextComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DateRangeTextComponent, DisplayDatePipe],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();

    fixture = TestBed.createComponent(DateRangeTextComponent);
    component = fixture.componentInstance;
    component.dateRange = { validFrom: new Date('2023-01-01'), validTo: new Date('2023-01-31') };

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
