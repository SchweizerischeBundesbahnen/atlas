import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableHearingOverviewTabComponent } from './timetable-hearing-overview-tab.component';
import { AppTestingModule } from '../../../app.testing.module';

describe('TimetableHearingOverviewTabComponent', () => {
  let component: TimetableHearingOverviewTabComponent;
  let fixture: ComponentFixture<TimetableHearingOverviewTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableHearingOverviewTabComponent],
      imports: [AppTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(TimetableHearingOverviewTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
