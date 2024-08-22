import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchServicePointPanelComponent } from './search-service-point-panel.component';

describe('SearchServicePointPanelComponent', () => {
  let component: SearchServicePointPanelComponent;
  let fixture: ComponentFixture<SearchServicePointPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SearchServicePointPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchServicePointPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
