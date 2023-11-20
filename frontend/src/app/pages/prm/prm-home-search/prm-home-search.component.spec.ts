import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrmHomeSearchComponent } from './prm-home-search.component';

describe('PrmOverviewComponent', () => {
  let component: PrmHomeSearchComponent;
  let fixture: ComponentFixture<PrmHomeSearchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PrmHomeSearchComponent],
    });
    fixture = TestBed.createComponent(PrmHomeSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
