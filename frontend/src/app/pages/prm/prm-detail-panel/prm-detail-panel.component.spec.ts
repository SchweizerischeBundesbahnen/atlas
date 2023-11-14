import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrmDetailPanelComponent } from './prm-detail-panel.component';

describe('PrmDetailPanelComponent', () => {
  let component: PrmDetailPanelComponent;
  let fixture: ComponentFixture<PrmDetailPanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PrmDetailPanelComponent]
    });
    fixture = TestBed.createComponent(PrmDetailPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
