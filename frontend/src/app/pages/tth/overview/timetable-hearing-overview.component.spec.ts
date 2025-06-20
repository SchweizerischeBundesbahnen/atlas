import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableHearingOverviewComponent } from './timetable-hearing-overview.component';
import { By } from '@angular/platform-browser';
import { CantonCardComponent } from './canton-card/canton-card.component';
import { TranslateModule } from '@ngx-translate/core';
import { AppTestingModule } from '../../../app.testing.module';

describe('TimetableHearingOverviewComponent', () => {
  let component: TimetableHearingOverviewComponent;
  let fixture: ComponentFixture<TimetableHearingOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        TranslateModule.forRoot(),
        TimetableHearingOverviewComponent,
        CantonCardComponent,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TimetableHearingOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create 27 canton cards', () => {
    const cards = fixture.debugElement.queryAll(By.css('.card'));
    expect(cards.length).toBe(27);
  });
});
