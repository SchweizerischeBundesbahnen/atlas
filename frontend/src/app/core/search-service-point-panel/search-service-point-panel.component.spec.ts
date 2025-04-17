import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchServicePointPanelComponent } from './search-service-point-panel.component';
import { AtlasButtonComponent } from '../components/button/atlas-button.component';
import { Component, Input } from '@angular/core';
import { ServicePointSearchType } from '../search-service-point/service-point-search';
import { AppTestingModule } from '../../app.testing.module';

@Component({
  selector: 'app-search-service-point',
  template: '<h1>SearchServicePointComponent</h1>',
  imports: [AppTestingModule],
})
class SearchServicePointMockComponent {
  @Input() searchType!: ServicePointSearchType;
}

describe('SearchServicePointPanelComponent', () => {
  let component: SearchServicePointPanelComponent;
  let fixture: ComponentFixture<SearchServicePointPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        SearchServicePointPanelComponent,
        AtlasButtonComponent,
        SearchServicePointMockComponent,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SearchServicePointPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle', () => {
    //given
    spyOn(component.toggleEvent, 'emit');
    //when
    component.toggle();
    //then
    expect(component.toggleEvent.emit).toHaveBeenCalledOnceWith();
    expect(component.showSearchPanel).toBeFalsy();
  });
});
