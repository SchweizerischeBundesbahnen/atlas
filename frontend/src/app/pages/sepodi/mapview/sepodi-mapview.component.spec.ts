import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SepodiMapviewComponent } from './sepodi-mapview.component';
import { AuthService } from '../../../core/auth/auth.service';
import { Component, Input } from '@angular/core';
import { ServicePointSearchType } from '../../../core/search-service-point/service-point-search';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { MapService } from '../map/map.service';
import { BehaviorSubject } from 'rxjs';
import { Map } from 'maplibre-gl';
import { GeoJsonProperties } from 'geojson';

@Component({
  selector: 'atlas-map',
  template: '',
})
export class MockAtlasMapComponent {
  @Input() isSidePanelOpen = false;
}

@Component({
  selector: 'app-search-service-point-panel',
  template: '<h1>SearchServicePointMockComponent</h1>',
})
class SearchServicePointMockComponent {
  @Input() searchType!: ServicePointSearchType;
}

const authService: Partial<AuthService> = {};
const mapSpy = jasmine.createSpyObj<Map>(['once', 'on']);
const mapService = jasmine.createSpyObj<MapService>([
  'initMap',
  'removeMap',
  'initMapEvents',
]);

mapService.servicePointsShown = new BehaviorSubject(false);
mapService.mapInitialized = new BehaviorSubject(false);
mapService.selectedElement = new BehaviorSubject({} as GeoJsonProperties);

mapService.initMap.and.returnValue(mapSpy);

describe('SepodiMapviewComponent', () => {
  let component: SepodiMapviewComponent;
  let fixture: ComponentFixture<SepodiMapviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SepodiMapviewComponent,
        MockAtlasMapComponent,
        SearchServicePointMockComponent,
        AtlasButtonComponent,
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: MapService, useValue: mapService },
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TranslatePipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SepodiMapviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should style side panel to open and closed', () => {
    component.setRouteActive(true);
    expect(component.detailContainer.nativeElement.classList).toContain(
      'side-panel-open'
    );

    component.setRouteActive(false);
    expect(component.detailContainer.nativeElement.classList.value).toEqual(
      'detail-container'
    );
  });
});
