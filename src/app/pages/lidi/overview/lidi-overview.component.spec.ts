import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LidiOverviewComponent } from './lidi-overview.component';

describe('OverviewComponent', () => {
  let component: LidiOverviewComponent;
  let fixture: ComponentFixture<LidiOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LidiOverviewComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LidiOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
