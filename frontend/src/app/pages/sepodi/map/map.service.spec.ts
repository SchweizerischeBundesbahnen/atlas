import { TestBed } from '@angular/core/testing';
import { MapService } from './map.service';
import { AuthService } from '../../../core/auth/auth.service';
import { MAP_STYLES, MapOptionsService } from './map-options.service';
import { GeoJSONSource, Map, MapGeoJSONFeature, MapMouseEvent } from 'maplibre-gl';
import { SpatialReference } from '../../../api';

const authService: Partial<AuthService> = {};

const markerSpy = jasmine.createSpyObj('Marker', ['addTo', 'setLngLat', 'remove']);
const mapSpy = jasmine.createSpyObj<Map>([
  'once',
  'flyTo',
  'getCanvas',
  'on',
  'off',
  'fire',
  'getSource',
  'setZoom',
  'getZoom',
  'setCenter',
  'setLayoutProperty',
  'resize',
]);
mapSpy.getSource = jasmine.createSpy('getSource').and.returnValue({
  setData: jasmine.createSpy('setData'),
});
mapSpy.on.and.callFake(() => {
  return mapSpy;
});
describe('MapService', () => {
  let service: MapService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: AuthService, useValue: authService }, { provide: MapOptionsService }],
    });
    service = TestBed.inject(MapService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should init map', () => {
    const htmlDivElement = document.createElement('div');
    const map = service.initMap(htmlDivElement);
    expect(map).toBeTruthy();
  });

  it('should fly to coordinates on map', () => {
    const mapSpy = jasmine.createSpyObj<Map>(['flyTo', 'resize', 'once']);
    mapSpy.flyTo.and.returnValue(mapSpy);

    service.map = mapSpy;

    service.centerOn({
      north: 46.96096807883433,
      east: 7.448919722210154,
      spatialReference: SpatialReference.Wgs84,
    });

    expect(mapSpy.flyTo).toHaveBeenCalled();
  });

  it('should deselect service point', () => {
    service.map = mapSpy;
    service.map.getSource = jasmine.createSpy('getSource').and.returnValue({
      setData: jasmine.createSpy('setData'),
    });
    service.deselectServicePoint();

    expect(service.map.getSource).toHaveBeenCalledWith('current_coordinates');
    expect(
      (service.map.getSource('current_coordinates') as GeoJSONSource).setData,
    ).toHaveBeenCalledWith({
      type: 'Feature',
      geometry: {
        type: 'Point',
        coordinates: [0, 0],
      },
      properties: {},
    });
  });

  it('should switch to different map style', () => {
    const mapSpy = jasmine.createSpyObj<Map>(['setLayoutProperty']);
    service.map = mapSpy;

    service.switchToStyle(MAP_STYLES[1]);

    expect(mapSpy.setLayoutProperty).toHaveBeenCalledWith('osm', 'visibility', 'visible');
  });

  it('should remove map', () => {
    const mapSpy = jasmine.createSpyObj<Map>(['remove']);
    service.map = mapSpy;

    service.removeMap();

    expect(mapSpy.remove).toHaveBeenCalledWith();
  });

  it('should build popup information correctly', () => {
    const features = [
      {
        geometry: {
          coordinates: [7.439133524894714, 46.94883407094761],
        },
        properties: {
          number: 8507000,
          designationOfficial: 'Bern',
          id: 10019,
          type: 'STOP_POINT_AND_FREIGHT_SERVICE_POINT',
        },
      },
    ] as unknown as MapGeoJSONFeature[];

    const result = service.buildServicePointPopupInformation(features);
    expect(result).toEqual(
      '<a href="service-point-directory/service-points/8507000"><b>85 07000</b> - Bern</a> <br/>',
    );
  });

  it('should show popup on features coordinates', () => {
    const mouseEvent = {
      features: [
        {
          geometry: {
            coordinates: [7.439133524894714, 46.94883407094761],
          },
          properties: {
            number: 8507000,
            designationOfficial: 'Bern',
            id: 10019,
            type: 'STOP_POINT_AND_FREIGHT_SERVICE_POINT',
          },
        },
      ],
    } as unknown as MapMouseEvent & { features?: MapGeoJSONFeature[] };

    spyOn(service.popup, 'addTo');
    service.showServicePointPopup(mouseEvent);

    expect(service.popup.getLngLat().lat).toEqual(46.94883407094761);
    expect(service.popup.getLngLat().lng).toEqual(7.439133524894714);
  });

  it('should select service point on click if only one is on coordinates', () => {
    const mapSpy = jasmine.createSpyObj<Map>(['getZoom']);
    mapSpy.getZoom.and.returnValue(12);
    service.map = mapSpy;

    const mouseEvent = {
      features: [
        {
          geometry: {
            coordinates: [7.439133524894714, 46.94883407094761],
          },
          properties: {
            number: 8507000,
            designationOfficial: 'Bern',
            id: 10019,
            type: 'STOP_POINT_AND_FREIGHT_SERVICE_POINT',
          },
        },
      ],
    } as unknown as MapMouseEvent & { features?: MapGeoJSONFeature[] };

    spyOn(service.selectedElement, 'next');
    service.onClick(mouseEvent);

    expect(service.selectedElement.next).toHaveBeenCalled();
  });

  it('should fix popup on click if only multiple service points are on coordinates', () => {
    // Given
    const mapSpy = jasmine.createSpyObj<Map>(['getZoom']);
    mapSpy.getZoom.and.returnValue(12);
    service.map = mapSpy;

    const mouseEvent = {
      features: [
        {
          geometry: {
            coordinates: [7.439133524894714, 46.94883407094761],
          },
          properties: {
            number: 8507000,
            designationOfficial: 'Bern',
            id: 10019,
            type: 'STOP_POINT_AND_FREIGHT_SERVICE_POINT',
          },
        },
        {
          geometry: {
            coordinates: [7.439133524894714, 46.94883407094761],
          },
          properties: {
            number: 8507001,
            designationOfficial: 'Bern Bhf Aufzug',
            id: 10019,
            type: 'STOP_POINT',
          },
        },
      ],
    } as unknown as MapMouseEvent & { features?: MapGeoJSONFeature[] };
    expect(service.keepPopup).toBeFalse();

    spyOn(service.selectedElement, 'next');
    spyOn(service, 'setPopupToFixed');

    // when
    service.onClick(mouseEvent);

    // then
    expect(service.selectedElement.next).not.toHaveBeenCalled();
    expect(service.keepPopup).toBeTrue();
    expect(service.setPopupToFixed).toHaveBeenCalled();
  });

  it('should add marker to map and fly to coordinates', () => {
    service.coordinateSelectionMode = true;

    const latLngCoordinates = { lat: 40, lng: -74 };
    const htmlDivElement = document.createElement('div');
    service.initMap(htmlDivElement);
    markerSpy.setLngLat.and.returnValue(markerSpy);
    service.marker = markerSpy;
    service.map = mapSpy;

    service.placeMarkerAndFlyTo(latLngCoordinates);

    expect(markerSpy.setLngLat).toHaveBeenCalledWith(latLngCoordinates);
    expect(markerSpy.addTo).toHaveBeenCalledWith(service.map);
    expect(mapSpy.flyTo).toHaveBeenCalled();
  });
});
