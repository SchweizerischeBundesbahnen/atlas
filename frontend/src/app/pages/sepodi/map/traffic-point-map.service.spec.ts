import { TestBed } from '@angular/core/testing';
import { Map, MapGeoJSONFeature } from 'maplibre-gl';
import { TrafficPointMapService } from './traffic-point-map.service';
import { MapService } from './map.service';
import { TrafficPointElementsService } from '../../../api';
import { BehaviorSubject, of } from 'rxjs';
import { MAP_TRAFFIC_POINT_LAYER_NAME } from './map-style';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../test/data/traffic-point-element';

const mapService = jasmine.createSpyObj<MapService>(['centerOn']);
mapService.mapInitialized = new BehaviorSubject<boolean>(true);
const mapSpy = jasmine.createSpyObj<Map>(['getSource']);
const sourceSpy = jasmine.createSpyObj('source', ['setData']);
mapSpy.getSource.and.returnValue(sourceSpy);
mapService.map = mapSpy;

const trafficPointElementsService = jasmine.createSpyObj([
  'getTrafficPointsOfServicePointValidToday',
]);

describe('TrafficPointMapService', () => {
  let service: TrafficPointMapService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: MapService,
          useValue: mapService,
        },
        {
          provide: TrafficPointElementsService,
          useValue: trafficPointElementsService,
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
        },
      },
    ] as unknown as MapGeoJSONFeature[];

    const result = TrafficPointMapService.buildTrafficPointPopupInformation(features);
    expect(result).toEqual(
      '<a href="service-point-directory/traffic-point-elements/ch:1:sloid:0:245">A - ch:1:sloid:0:245</a> <br/>',
    );
  });

  it('should display TrafficPoints on map', () => {
    trafficPointElementsService.getTrafficPointsOfServicePointValidToday.and.returnValue(
      of(BERN_WYLEREGG_TRAFFIC_POINTS),
    );

    service.displayTrafficPointsOnMap(8507000);

    expect(mapSpy.getSource).toHaveBeenCalledWith(MAP_TRAFFIC_POINT_LAYER_NAME);
    expect(sourceSpy.setData).toHaveBeenCalled();
    const data = sourceSpy.setData.calls.mostRecent().args[0];
    expect(data.features).toHaveSize(2);
  });

  it('should clear TrafficPoints on map', () => {
    service.clearDisplayedTrafficPoints();

    expect(sourceSpy.setData).toHaveBeenCalled();
    const data = sourceSpy.setData.calls.mostRecent().args[0];
    expect(data.features).toHaveSize(0);
  });

  it('should display current TrafficPointVersion on map', () => {
    service.displayCurrentTrafficPoint({
      north: 46.96102079646,
      east: 7.44908190053,
      spatialReference: 'WGS84',
    });

    expect(mapSpy.getSource).toHaveBeenCalledWith('current_traffic_point');
    expect(sourceSpy.setData).toHaveBeenCalled();
    const data = sourceSpy.setData.calls.mostRecent().args[0];
    expect(data.geometry.coordinates).toEqual([7.44908190053, 46.96102079646]);
  });
});
