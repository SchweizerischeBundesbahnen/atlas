import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrmOverviewComponent } from './prm-overview.component';

describe('PrmOverviewComponent', () => {
  let component: PrmOverviewComponent;
  let fixture: ComponentFixture<PrmOverviewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PrmOverviewComponent],
    });
    fixture = TestBed.createComponent(PrmOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
