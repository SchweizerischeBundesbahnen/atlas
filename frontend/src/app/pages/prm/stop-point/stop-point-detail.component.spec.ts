import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointDetailComponent } from './stop-point-detail.component';

describe('PrmDetailPanelComponent', () => {
  let component: StopPointDetailComponent;
  let fixture: ComponentFixture<StopPointDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StopPointDetailComponent],
    });
    fixture = TestBed.createComponent(StopPointDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
