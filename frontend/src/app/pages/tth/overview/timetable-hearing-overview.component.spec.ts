import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableHearingOverviewComponent } from './timetable-hearing-overview.component';
import { By } from '@angular/platform-browser';
import { CantonCardComponent } from './canton-card/canton-card.component';
import { TthModule } from '../tth.module';
import { TranslateModule } from '@ngx-translate/core';

describe('TimetableHearingOverviewComponent', () => {
  let component: TimetableHearingOverviewComponent;
  let fixture: ComponentFixture<TimetableHearingOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableHearingOverviewComponent, CantonCardComponent],
      imports: [TthModule, TranslateModule.forRoot()],
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
