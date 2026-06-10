import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach, vi, type Mocked } from 'vitest';

import { TimetableHearingOverviewComponent } from './timetable-hearing-overview.component';
import { By } from '@angular/platform-browser';
import { CantonCardComponent } from './canton-card/canton-card.component';
import { AppTestingModule } from '../../../app.testing.module';
import { OverviewToTabShareDataService } from '../overview-tab/service/overview-to-tab-share-data.service';

describe('TimetableHearingOverviewComponent', () => {
  let component: TimetableHearingOverviewComponent;
  let fixture: ComponentFixture<TimetableHearingOverviewComponent>;
  let service: Mocked<Pick<OverviewToTabShareDataService, 'setCantonShort'>>;

  beforeEach(async () => {
    service = { setCantonShort: vi.fn() };

    await TestBed.configureTestingModule({
      imports: [AppTestingModule, TimetableHearingOverviewComponent, CantonCardComponent],
      providers: [{ provide: OverviewToTabShareDataService, useValue: service }],
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

  it('should call setCantonShort on service when onCantonCardClick is triggered', () => {
    const canton = 'BE';
    component.onCantonCardClick(canton);
    expect(service.setCantonShort).toHaveBeenCalledWith(canton);
  });
});
