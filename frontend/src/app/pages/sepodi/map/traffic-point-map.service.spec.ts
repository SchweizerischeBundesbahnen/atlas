import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { GeoJSONSource, Map, MapGeoJSONFeature } from 'maplibre-gl';
import { TrafficPointMapService } from './traffic-point-map.service';
import { MapService } from './map.service';
import { BehaviorSubject, of } from 'rxjs';
import { MAP_TRAFFIC_POINT_LAYER_NAME } from './map-style';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../test/data/traffic-point-element';
import { TrafficPointElementInternalService } from '../../../api/service/sepodi/traffic-point-element-internal.service';
import { Point } from 'geojson';
import { mock, mockDeep } from 'vitest-mock-extended';

describe('TrafficPointMapService', () => {
  let service: TrafficPointMapService;

  const sourceMock = mock<GeoJSONSource>();
  const mapMock = mockDeep<Map>();
  mapMock.getSource.mockReturnValue(sourceMock);
  const mapServiceSpy = mock<MapService>();
  mapServiceSpy.mapInitialized = new BehaviorSubject(true);
  mapServiceSpy.map = mapMock;

  const trafficPointElementInternalService = mock<TrafficPointElementInternalService>();

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: MapService,
          useValue: mapServiceSpy,
        },
        {
          provide: TrafficPointElementInternalService,
          useValue: trafficPointElementInternalService,
        },
      ],
    });
    service = TestBed.inject(TrafficPointMapService);
  });

  it('should build popup information correctly', () => {
    const features = [
      {
        geometry: {
          coordinates: [7.439133524894714, 46.94883407094761],
        },
        properties: {
          sloid: 'ch:1:sloid:0:245',
          designation: 'A',
          type: 'BORDING_PLATFORM',
          servicePointNumber: 857000,
        },
      },
    ] as unknown as MapGeoJSONFeature[];

    const result = TrafficPointMapService.buildTrafficPointPopupInformation(features);
    expect(result).toEqual(
      '<a href="service-point-directory/service-points/857000/traffic-point-elements/ch:1:sloid:0:245">A - ch:1:sloid:0:245</a> <br/>'
    );
  });

  it('should display TrafficPoints on map', () => {
    trafficPointElementInternalService.getTrafficPointsOfServicePointValidToday.mockReturnValue(
      of(BERN_WYLEREGG_TRAFFIC_POINTS)
    );

    service.displayTrafficPointsOnMap(8507000);

    expect(mapServiceSpy.map.getSource).toHaveBeenCalledWith(MAP_TRAFFIC_POINT_LAYER_NAME);
    expect(sourceMock.setData).toHaveBeenCalled();
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.FeatureCollection;
    expect(data.features).toHaveLength(2);
  });

  it('should clear TrafficPoints on map', () => {
    service.clearDisplayedTrafficPoints();

    expect(sourceMock.setData).toHaveBeenCalled();
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.FeatureCollection;
    expect(data.features).toHaveLength(0);
  });

  it('should display current TrafficPointVersion on map', () => {
    service.displayCurrentTrafficPoint({
      north: 46.96102079646,
      east: 7.44908190053,
      spatialReference: 'WGS84',
    });

    expect(mapServiceSpy.map.getSource).toHaveBeenCalledWith('current_traffic_point');
    expect(sourceMock.setData).toHaveBeenCalled();
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.Feature<Point>;
    expect(data.geometry.coordinates).toEqual([7.44908190053, 46.96102079646]);
  });

  it('should highlight a displayed TrafficPoint by sloid', () => {
    trafficPointElementInternalService.getTrafficPointsOfServicePointValidToday.mockReturnValue(
      of(BERN_WYLEREGG_TRAFFIC_POINTS)
    );
    service.displayTrafficPointsOnMap(8507000);

    service.highlightTrafficPointBySloid('ch:1:sloid:89008:0:2');

    expect(mapServiceSpy.map.getSource).toHaveBeenCalledWith('hovered_traffic_point');
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.Feature<Point>;
    expect(data.geometry.coordinates).toEqual([7.4479859044, 46.96057330114]);
  });

  it('should not highlight anything when sloid is not currently displayed on the map', () => {
    trafficPointElementInternalService.getTrafficPointsOfServicePointValidToday.mockReturnValue(
      of(BERN_WYLEREGG_TRAFFIC_POINTS)
    );
    service.displayTrafficPointsOnMap(8507000);
    sourceMock.setData.mockClear();

    service.highlightTrafficPointBySloid('unknown-sloid');

    expect(sourceMock.setData).not.toHaveBeenCalled();
  });

  it('should clear highlighted TrafficPoint on map', () => {
    service.clearHighlightedTrafficPoint();

    expect(mapServiceSpy.map.getSource).toHaveBeenCalledWith('hovered_traffic_point');
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.Feature<Point>;
    expect(data.geometry.coordinates).toEqual([0, 0]);
  });
});
