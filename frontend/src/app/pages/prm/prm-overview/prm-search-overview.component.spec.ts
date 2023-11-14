import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PrmSearchOverviewComponent} from './prm-search-overview.component';

describe('PrmOverviewComponent', () => {
  let component: PrmSearchOverviewComponent;
  let fixture: ComponentFixture<PrmSearchOverviewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PrmSearchOverviewComponent],
    });
    fixture = TestBed.createComponent(PrmSearchOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
