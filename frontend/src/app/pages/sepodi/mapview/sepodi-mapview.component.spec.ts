import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import { SepodiMapviewComponent } from './sepodi-mapview.component';
import { AuthService } from '../../../core/auth/auth.service';
import { Component, input, output } from '@angular/core';
import { ServicePointSearchType } from '../../../core/search-service-point/service-point-search';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { MapService } from '../map/map.service';
import { BehaviorSubject, Subject } from 'rxjs';
import { GeoJsonProperties } from 'geojson';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { MapComponent } from '../map/map.component';
import { SearchServicePointPanelComponent } from '../../../core/search-service-point-panel/search-service-point-panel.component';

@Component({
  selector: 'atlas-map',
  template: '',
})
export class MockAtlasMapComponent {
  readonly isSidePanelOpen = input(false);
}

@Component({
  selector: 'atlas-search-service-point-panel',
  template: '<h1>SearchServicePointMockComponent</h1><ng-content />',
})
class SearchServicePointMockComponent {
  readonly searchType = input.required<ServicePointSearchType>();
  readonly toggleEvent = output<void>();
}

describe('SepodiMapviewComponent', () => {
  let component: SepodiMapviewComponent;
  let fixture: ComponentFixture<SepodiMapviewComponent>;

  const authService: Partial<AuthService> = {};

  let mapServiceSpy: Mocked<
    Pick<MapService, 'initMap' | 'removeMap' | 'initMapEvents'> & {
      selectedElement: Subject<GeoJsonProperties>;
      servicePointsShown: BehaviorSubject<boolean>;
      mapInitialized: BehaviorSubject<boolean>;
    }
  >;

  beforeEach(() => {
    mapServiceSpy = {
      initMap: vi.fn(),
      removeMap: vi.fn(),
      initMapEvents: vi.fn(),
      selectedElement: new Subject<GeoJsonProperties>(),
      servicePointsShown: new BehaviorSubject(false),
      mapInitialized: new BehaviorSubject(false),
    };
    mapServiceSpy.initMap.mockReturnValue({} as ReturnType<MapService['initMap']>);

    TestBed.configureTestingModule({
      imports: [
        SepodiMapviewComponent,
        MockAtlasMapComponent,
        SearchServicePointMockComponent,
        AtlasButtonComponent,
        RouterModule.forRoot([]),
      ],
      providers: [
        translateServiceProvider,
        { provide: AuthService, useValue: authService },
        { provide: MapService, useValue: mapServiceSpy },
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TranslatePipe },
      ],
    });

    TestBed.overrideComponent(SepodiMapviewComponent, {
      remove: { imports: [MapComponent, SearchServicePointPanelComponent] },
      add: { imports: [MockAtlasMapComponent, SearchServicePointMockComponent] },
    });

    fixture = TestBed.createComponent(SepodiMapviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should style side panel to open and closed', () => {
    component.setRouteActive(true);
    expect(component.detailContainer.nativeElement.classList).toContain('side-panel-open');

    component.setRouteActive(false);
    expect(component.detailContainer.nativeElement.classList.value).toEqual('detail-container');
  });
});
