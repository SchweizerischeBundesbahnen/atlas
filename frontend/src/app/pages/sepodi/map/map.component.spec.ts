import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MapComponent } from './map.component';
import { AppTestingModule } from '../../../app.testing.module';
import { MAP_STYLES } from './map-options.service';
import { MapService } from './map.service';
import maplibregl, { Map, MapLibreGL } from 'maplibre-gl';
import { BehaviorSubject } from 'rxjs';
import { CoordinatePair } from 'src/app/api';

const isEditModeSubject = new BehaviorSubject<boolean>(true);
const clickedGeographyCoordinatesSubject = new BehaviorSubject<CoordinatePair>({
  north: 0,
  east: 0,
});

const mapCanvasMock = document.createElement('canvas');
const mapSpy = jasmine.createSpyObj<Map>(['once', 'flyTo', 'getCanvas', 'on', 'off', 'fire']);
const mapService = jasmine.createSpyObj<MapService>(['initMap', 'switchToStyle', 'removeMap']);
const markerSpy = jasmine.createSpyObj('Marker', ['addTo', 'setLngLat', 'remove']);

mapSpy.getCanvas.and.returnValue(mapCanvasMock);
mapService.isEditMode = isEditModeSubject;
mapService.clickedGeographyCoordinates = clickedGeographyCoordinatesSubject; // Weise dem Spion den BehaviorSubject zu

mapService.initMap.and.returnValue(mapSpy);

let clickCallback: any;
mapSpy.on.and.callFake((event: string, callback: any) => {
  if (event === 'click') {
    clickCallback = callback;
  }
  return mapSpy;
});

describe('MapComponent', () => {
  let component: MapComponent;
  let fixture: ComponentFixture<MapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MapComponent],
      imports: [AppTestingModule],
      providers: [{ provide: MapService, useValue: mapService }],
    }).compileComponents();

    spyOn(maplibregl, 'Marker').and.returnValue(markerSpy);

    fixture = TestBed.createComponent(MapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open map style selection', () => {
    expect(component.showMapStyleSelection).toBeFalse();

    component.toggleStyleSelection();
    expect(component.showMapStyleSelection).toBeTrue();
  });

  it('should switch map style selection via service', () => {
    mapService.switchToStyle.and.callFake((i) => i);

    const newStyle = MAP_STYLES[1];
    component.switchToStyle(newStyle);

    expect(mapService.switchToStyle).toHaveBeenCalled();
    expect(component.currentMapStyle).toEqual(newStyle);
    expect(component.showMapStyleSelection).toBeFalse();
  });

  it('should toggle legend', () => {
    expect(component.showMapLegend).toBeFalse();

    component.toggleLegend();
    expect(component.showMapLegend).toBeTrue();

    component.toggleLegend();
    expect(component.showMapLegend).toBeFalse();
  });

  it('should add marker to map on map click', () => {
    markerSpy.setLngLat.and.returnValue(markerSpy);

    component.handleMapClick();

    expect(clickCallback).toBeDefined();

    clickCallback({
      lngLat: {
        lng: 10,
        lat: 10,
      },
    });
    expect(markerSpy.setLngLat).toHaveBeenCalledWith({ lng: 10, lat: 10 });
    expect(markerSpy.addTo).toHaveBeenCalledWith(component.map);
  });
});
