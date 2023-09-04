import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchServicePointComponent } from './search-service-point.component';

describe('SearchServicePointComponent', () => {
  let component: SearchServicePointComponent;
  let fixture: ComponentFixture<SearchServicePointComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SearchServicePointComponent],
    });
    fixture = TestBed.createComponent(SearchServicePointComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
