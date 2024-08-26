import {ComponentFixture, TestBed} from '@angular/core/testing';

import {MapComponent} from './map.component';
import {AppTestingModule} from '../../../app.testing.module';
import {MAP_STYLES} from './map-options';
import {CoordinatePairWGS84, MapService} from './map.service';
import maplibregl, {Map} from 'maplibre-gl';
import {BehaviorSubject} from 'rxjs';
import {PermissionService} from "../../../core/auth/permission/permission.service";
import {adminPermissionServiceMock} from "../../../app.testing.mocks";
import {SERVICE_POINT_MIN_ZOOM} from "./map-style";

const clickedGeographyCoordinatesSubject = new BehaviorSubject<CoordinatePairWGS84>({
  lat: 0,
  lng: 0,
});

const mapCanvasMock = document.createElement('canvas');
const mapSpy = jasmine.createSpyObj<Map>([
  'once',
  'flyTo',
  'getCanvas',
  'on',
  'off',
  'fire',
  'getZoom',
  'setZoom',
  'zoomTo',
  'zoomOut',
]);
const mapService = jasmine.createSpyObj<MapService>([
  'initMap',
  'switchToStyle',
  'removeMap',
  'initMapEvents',
  'placeMarkerAndFlyTo',
]);
const markerSpy = jasmine.createSpyObj('Marker', ['addTo', 'setLngLat', 'remove']);

mapSpy.getCanvas.and.returnValue(mapCanvasMock);
mapService.clickedGeographyCoordinates = clickedGeographyCoordinatesSubject; // Weise dem Spion den BehaviorSubject zu
mapService.servicePointsShown = new BehaviorSubject(false);

mapService.initMap.and.returnValue(mapSpy);

mapSpy.on.and.callFake(() => {
  return mapSpy;
});

describe('MapComponent', () => {
  let component: MapComponent;
  let fixture: ComponentFixture<MapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MapComponent],
      imports: [AppTestingModule],
      providers: [
        {provide: MapService, useValue: mapService},
        {provide: PermissionService, useValue: adminPermissionServiceMock},
      ],
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

  it('should increase zoom when zoomIn() is called', () => {
    component.zoomIn();
    expect(component.map.zoomTo).toHaveBeenCalledWith(component.map.getZoom() + 0.75, {
      duration: 500,
    });
  });

  it('should decrease zoom when zoomOut() is called', () => {
    component.zoomOut();
    expect(component.map.zoomTo).toHaveBeenCalledWith(component.map.getZoom() - 0.75, {
      duration: 500,
    });
  });

  it('should zoom to SERVICE_POINT_MIN_ZOOM', () => {
    component.zoomToServicePointMin();
    expect(component.map.zoomTo).toHaveBeenCalledWith(SERVICE_POINT_MIN_ZOOM, {duration: 500});
  });

  it('should center into swiss country when goHome() is called', () => {
    component.goHome();
    expect(component.map.flyTo).toHaveBeenCalledWith({
      center: [8.2275, 46.8182],
      zoom: 7.25,
      speed: 0.8,
    });
  });
});
