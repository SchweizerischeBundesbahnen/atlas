import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LineDetailComponent } from './line-detail.component';

describe('DetailComponent', () => {
  let component: LineDetailComponent;
  let fixture: ComponentFixture<LineDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LineDetailComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
