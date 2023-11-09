import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingPointsTableComponent } from './loading-points-table.component';

describe('LoadingPointsDetailComponent', () => {
  let component: LoadingPointsTableComponent;
  let fixture: ComponentFixture<LoadingPointsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoadingPointsTableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(LoadingPointsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
