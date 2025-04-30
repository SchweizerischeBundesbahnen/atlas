import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateIconComponent } from './date-icon.component';

describe('DateIconComponent', () => {
  let component: DateIconComponent;
  let fixture: ComponentFixture<DateIconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DateIconComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DateIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
