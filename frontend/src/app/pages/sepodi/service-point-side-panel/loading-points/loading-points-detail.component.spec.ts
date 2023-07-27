import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingPointsDetailComponent } from './loading-points-detail.component';

describe('LoadingPointsDetailComponent', () => {
  let component: LoadingPointsDetailComponent;
  let fixture: ComponentFixture<LoadingPointsDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoadingPointsDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(LoadingPointsDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
