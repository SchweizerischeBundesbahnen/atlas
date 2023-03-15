import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableHearingOverviewComponent } from './timetable-hearing-overview.component';

describe('TimetableHearingOverviewComponent', () => {
  let component: TimetableHearingOverviewComponent;
  let fixture: ComponentFixture<TimetableHearingOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableHearingOverviewComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TimetableHearingOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
