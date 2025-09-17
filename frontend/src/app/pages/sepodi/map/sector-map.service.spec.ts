import { TestBed } from '@angular/core/testing';
import { Map, MapGeoJSONFeature } from 'maplibre-gl';
import { TrafficPointMapService } from './traffic-point-map.service';
import { MapService } from './map.service';
import { BehaviorSubject, of } from 'rxjs';
import { MAP_SECTOR_LAYER_NAME } from './map-style';
import { SectorMapService } from './sector-map.service';
import { SectorInternalService } from '../../../api/service/sepodi/sector-internal.service';
import { SpatialReference } from '../../../api';

const mapService = jasmine.createSpyObj<MapService>(['centerOn']);
mapService.mapInitialized = new BehaviorSubject<boolean>(true);
const mapSpy = jasmine.createSpyObj<Map>(['getSource']);
const sourceSpy = jasmine.createSpyObj('source', ['setData']);
mapSpy.getSource.and.returnValue(sourceSpy);
mapService.map = mapSpy;

const sectorInternalService = jasmine.createSpyObj(['getSectorsValidToday']);

describe('SectorMapService', () => {
  let service: SectorMapService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: MapService,
          useValue: mapService,
        },
        {
          provide: SectorInternalService,
          useValue: sectorInternalService,
        },
      ],
    });
    service = TestBed.inject(SectorMapService);
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
          servicePointNumber: 857000,
        },
      },
    ] as unknown as MapGeoJSONFeature[];

    const result =
      TrafficPointMapService.buildTrafficPointPopupInformation(features);
    expect(result).toEqual(
      '<a href="service-point-directory/service-points/857000/traffic-point-elements/ch:1:sloid:0:245">A - ch:1:sloid:0:245</a> <br/>'
    );
  });

  it('should display Sectors on map', () => {
    sectorInternalService.getSectorsValidToday.and.returnValue(
      of([
        {
          trafficPointSloid: 'ch:1:sloid:7000:1',
          validFrom: new Date('2014-12-14'),
          validTo: new Date('2014-12-14'),
          designation: 'A',
          sectorGeolocation: {
            lv95: {
              north: 0,
              east: 0,
              spatialReference: SpatialReference.Lv95,
            },
            spatialReference: 'WGS84WEB',
            wgs84: {
              north: 0,
              east: 0,
              spatialReference: SpatialReference.Wgs84,
            },
            lv03: {
              north: 0,
              east: 0,
              spatialReference: SpatialReference.Lv03,
            },
          },
          sloid: 'ch:1:sloid:7000:1:1',
        },
      ])
    );

    service.displaySectorsOnMap('ch:1:sloid:7000:0:1');

    expect(mapSpy.getSource).toHaveBeenCalledWith(MAP_SECTOR_LAYER_NAME);
    expect(sourceSpy.setData).toHaveBeenCalled();
    const data = sourceSpy.setData.calls.mostRecent().args[0];
    expect(data.features).toHaveSize(1);
  });

  it('should clear Sectors on map', () => {
    service.clearDisplayedSectors();

    expect(sourceSpy.setData).toHaveBeenCalled();
    const data = sourceSpy.setData.calls.mostRecent().args[0];
    expect(data.features).toHaveSize(0);
  });

  it('should display current SectorVersion on map', () => {
    service.displayCurrentSector({
      north: 46.96102079646,
      east: 7.44908190053,
      spatialReference: 'WGS84',
    });

    expect(mapSpy.getSource).toHaveBeenCalledWith('current_sector');
    expect(sourceSpy.setData).toHaveBeenCalled();
    const data = sourceSpy.setData.calls.mostRecent().args[0];
    expect(data.geometry.coordinates).toEqual([7.44908190053, 46.96102079646]);
  });
});
