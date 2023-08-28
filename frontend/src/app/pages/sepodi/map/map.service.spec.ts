import { TestBed } from '@angular/core/testing';
import { MapService } from './map.service';
import { AuthService } from '../../../core/auth/auth.service';
import { MAP_STYLES, MapOptionsService } from './map-options.service';
import { Map, MapGeoJSONFeature } from 'maplibre-gl';

const authService: Partial<AuthService> = {};

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

    service
      .centerOn({
        north: 46.96096807883433,
        east: 7.448919722210154,
      })
      .then();

    expect(mapSpy.flyTo).toHaveBeenCalled();
  });

  it('should deselect service point', () => {
    const mapSpy = jasmine.createSpyObj<Map>(['removeFeatureState']);
    service.map = mapSpy;

    service.deselectServicePoint();

    expect(mapSpy.removeFeatureState).toHaveBeenCalled();
  });

  it('should select service point', () => {
    const mapSpy = jasmine.createSpyObj<Map>([
      'removeFeatureState',
      'queryRenderedFeatures',
      'setFeatureState',
    ]);
    const feature = { properties: { number: 8507000 } } as unknown as MapGeoJSONFeature;
    const renderedFeatures = [feature];
    mapSpy.queryRenderedFeatures.and.returnValue(renderedFeatures);
    service.map = mapSpy;

    service.selectServicePoint(8507000);

    expect(mapSpy.removeFeatureState).toHaveBeenCalled();
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
    expect(result).toBe(
      '<a href="service-point-directory/service-points/8507000" "><b>85 07000</b> - Bern</a> <br/>'
    );
  });
});
