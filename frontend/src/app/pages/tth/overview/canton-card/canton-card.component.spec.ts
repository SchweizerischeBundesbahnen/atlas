import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CantonCardComponent } from './canton-card.component';

describe('CantonCardComponent', () => {
  let component: CantonCardComponent;
  let fixture: ComponentFixture<CantonCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CantonCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CantonCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
