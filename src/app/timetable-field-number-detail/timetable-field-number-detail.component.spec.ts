import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimetableFieldNumberDetailComponent } from './timetable-field-number-detail.component';

describe('TimetableFieldNumberDetailComponent', () => {
  let component: TimetableFieldNumberDetailComponent;
  let fixture: ComponentFixture<TimetableFieldNumberDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableFieldNumberDetailComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TimetableFieldNumberDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
