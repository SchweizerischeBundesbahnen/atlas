import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableHearingOverviewTabHeadingComponent } from './timetable-hearing-overview-tab-heading.component';

describe('TimetableHearingOverviewTabHeadingComponent', () => {
  let component: TimetableHearingOverviewTabHeadingComponent;
  let fixture: ComponentFixture<TimetableHearingOverviewTabHeadingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableHearingOverviewTabHeadingComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TimetableHearingOverviewTabHeadingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
