import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingPointsTableComponent } from './loading-points-table.component';
import { AppTestingModule } from '../../../../app.testing.module';

describe('LoadingPointsTableComponent', () => {
  let component: LoadingPointsTableComponent;
  let fixture: ComponentFixture<LoadingPointsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoadingPointsTableComponent],
      imports: [AppTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(LoadingPointsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
