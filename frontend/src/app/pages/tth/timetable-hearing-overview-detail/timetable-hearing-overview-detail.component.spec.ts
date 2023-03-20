import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableHearingOverviewDetailComponent } from './timetable-hearing-overview-detail.component';

describe('TimetableHearingOverviewDetailComponent', () => {
  let component: TimetableHearingOverviewDetailComponent;
  let fixture: ComponentFixture<TimetableHearingOverviewDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableHearingOverviewDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TimetableHearingOverviewDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
