import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableHearingOverviewDetailComponent } from './timetable-hearing-overview-detail.component';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';

describe('TimetableHearingOverviewDetailComponent', () => {
  let component: TimetableHearingOverviewDetailComponent;
  let fixture: ComponentFixture<TimetableHearingOverviewDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableHearingOverviewDetailComponent],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }, { provide: DisplayDatePipe }],
    }).compileComponents();

    fixture = TestBed.createComponent(TimetableHearingOverviewDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
