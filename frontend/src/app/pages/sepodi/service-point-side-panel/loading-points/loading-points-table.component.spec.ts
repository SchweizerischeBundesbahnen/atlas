import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingPointsTableComponent } from './loading-points-table.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { MockAtlasButtonComponent, MockTableComponent } from '../../../../app.testing.mocks';

describe('LoadingPointsTableComponent', () => {
  let component: LoadingPointsTableComponent;
  let fixture: ComponentFixture<LoadingPointsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoadingPointsTableComponent, MockTableComponent, MockAtlasButtonComponent],
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
