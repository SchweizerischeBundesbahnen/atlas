import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableHearingOverviewTabHeadingComponent } from './timetable-hearing-overview-tab-heading.component';
import { AppTestingModule } from '../../../../app.testing.module';

describe('TimetableHearingOverviewTabHeadingComponent', () => {
  let component: TimetableHearingOverviewTabHeadingComponent;
  let fixture: ComponentFixture<TimetableHearingOverviewTabHeadingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableHearingOverviewTabHeadingComponent],
      imports: [AppTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(TimetableHearingOverviewTabHeadingComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.cantonShort = 'BE';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
