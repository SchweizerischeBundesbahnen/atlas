import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SearchServicePointPanelComponent } from './search-service-point-panel.component';
import { Component, input, inputBinding } from '@angular/core';
import { ServicePointSearch, ServicePointSearchType } from '../search-service-point/service-point-search';
import { translateServiceProvider } from '../../app.testing.mocks';
import { SearchServicePointComponent } from '../search-service-point/search-service-point.component';

@Component({
  selector: 'atlas-search-service-point',
  template: '<h1>SearchServicePointComponent</h1>',
})
class SearchServicePointMockComponent {
  readonly searchType = input.required<ServicePointSearchType>();
}

describe('SearchServicePointPanelComponent', () => {
  let component: SearchServicePointPanelComponent;
  let fixture: ComponentFixture<SearchServicePointPanelComponent>;

  beforeEach(() => {
    const searchTypeInputName: keyof SearchServicePointPanelComponent = 'searchType';

    fixture = TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    })
      .overrideComponent(SearchServicePointPanelComponent, {
        remove: {
          imports: [SearchServicePointComponent],
        },
        add: {
          imports: [SearchServicePointMockComponent],
        },
      })
      .createComponent(SearchServicePointPanelComponent, {
        bindings: [inputBinding(searchTypeInputName, () => ServicePointSearch.SePoDi)],
      });

    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle', () => {
    //given
    vi.spyOn(component.toggleEvent, 'emit');
    //when
    component.toggle();
    //then
    expect(component.toggleEvent.emit).toHaveBeenCalledExactlyOnceWith();
    expect(component.showSearchPanel).toBe(false);
  });
});
