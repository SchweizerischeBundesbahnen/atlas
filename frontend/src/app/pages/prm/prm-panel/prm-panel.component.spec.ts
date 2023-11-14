import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrmPanelComponent } from './prm-panel.component';

describe('PrmPanelComponent', () => {
  let component: PrmPanelComponent;
  let fixture: ComponentFixture<PrmPanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PrmPanelComponent]
    });
    fixture = TestBed.createComponent(PrmPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
