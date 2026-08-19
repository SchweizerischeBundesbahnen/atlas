import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, Mocked, vi } from 'vitest';
import { GeoJSONSource, Map, MapGeoJSONFeature } from 'maplibre-gl';
import { MapService } from './map.service';
import { BehaviorSubject, of } from 'rxjs';
import { MAP_SECTOR_LAYER_NAME } from './map-style';
import { SectorMapService } from './sector-map.service';
import { SectorInternalService } from '../../../api/service/sepodi/sector-internal.service';
import { ReadSectorVersion } from '../../../api/model/readSectorVersion';
import { SpatialReference } from '../../../api';
import { Point } from 'geojson';
import { mock, mockDeep } from 'vitest-mock-extended';

describe('SectorMapService', () => {
  let service: SectorMapService;

  const SECTOR_A: ReadSectorVersion = {
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
        north: 46.96102079646,
        east: 7.44908190053,
        spatialReference: SpatialReference.Wgs84,
      },
      lv03: {
        north: 0,
        east: 0,
        spatialReference: SpatialReference.Lv03,
      },
    },
    sloid: 'ch:1:sloid:7000:1:1',
  };

  const SECTOR_B: ReadSectorVersion = {
    ...SECTOR_A,
    designation: 'B',
    sectorGeolocation: {
      ...SECTOR_A.sectorGeolocation!,
      wgs84: {
        north: 46.96057330114,
        east: 7.4479859044,
        spatialReference: SpatialReference.Wgs84,
      },
    },
    sloid: 'ch:1:sloid:7000:1:2',
  };

  const sourceMock = mock<GeoJSONSource>();
  const mapMock = mockDeep<Map>();
  mapMock.getSource.mockReturnValue(sourceMock);
  const mapServiceSpy = mock<MapService>();
  mapServiceSpy.mapInitialized = new BehaviorSubject(true);
  mapServiceSpy.map = mapMock;

  let sectorInternalService: Mocked<Pick<SectorInternalService, 'getSectorsValidToday'>>;

  beforeEach(() => {
    sectorInternalService = {
      getSectorsValidToday: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        {
          provide: MapService,
          useValue: mapServiceSpy,
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
          sloid: 'ch:1:sloid:7000:0:2:1',
          trafficPointSloid: 'ch:1:sloid:7000:0:2',
          designation: 'A',
          servicePointNumber: 8507000,
        },
      },
    ] as unknown as MapGeoJSONFeature[];

    const result = SectorMapService.buildSectorPopupInformation(features);
    expect(result).toEqual(
      '<a' +
        ' href="service-point-directory/service-points/8507000/traffic-point-elements/ch:1:sloid:7000:0:2/sectors/ch:1:sloid:7000:0:2:1">A -' +
        ' ch:1:sloid:7000:0:2:1</a> <br/>'
    );
  });

  it('should display Sectors on map', () => {
    sectorInternalService.getSectorsValidToday.mockReturnValue(
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

    service.displaySectorsOnMap(8507000, 'ch:1:sloid:7000:0:1');

    expect(mapServiceSpy.map.getSource).toHaveBeenCalledWith(MAP_SECTOR_LAYER_NAME);
    expect(sourceMock.setData).toHaveBeenCalled();
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.FeatureCollection;
    expect(data.features).toHaveLength(1);
  });

  it('should clear Sectors on map', () => {
    service.clearDisplayedSectors();

    expect(sourceMock.setData).toHaveBeenCalled();
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.FeatureCollection;
    expect(data.features).toHaveLength(0);
  });

  it('should display current SectorVersion on map', () => {
    service.displayCurrentSector({
      north: 46.96102079646,
      east: 7.44908190053,
      spatialReference: 'WGS84',
    });

    expect(mapServiceSpy.map.getSource).toHaveBeenCalledWith('current_sector');
    expect(sourceMock.setData).toHaveBeenCalled();
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.Feature<Point>;
    expect(data.geometry.coordinates).toEqual([7.44908190053, 46.96102079646]);
  });

  it('should clear current SectorVersion on map', () => {
    service.clearCurrentSector();

    expect(mapServiceSpy.map.getSource).toHaveBeenCalledWith('current_sector');
    expect(sourceMock.setData).toHaveBeenCalled();
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.Feature<Point>;
    expect(data.geometry.coordinates).toEqual([0, 0]);
  });

  it('should highlight a displayed Sector by sloid', () => {
    // Given
    sectorInternalService.getSectorsValidToday.mockReturnValue(of([SECTOR_A, SECTOR_B]));
    service.displaySectorsOnMap(8507000, 'ch:1:sloid:7000:0:1');

    // When
    service.highlightSectorBySloid('ch:1:sloid:7000:1:1');

    // Then
    expect(mapServiceSpy.map.getSource).toHaveBeenCalledWith('hovered_sector');
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.FeatureCollection;
    expect(data.features).toHaveLength(1);
    expect((data.features[0].geometry as Point).coordinates).toEqual([7.44908190053, 46.96102079646]);
  });

  it('should highlight all displayed Sectors matching the given sloids', () => {
    // Given
    sectorInternalService.getSectorsValidToday.mockReturnValue(of([SECTOR_A, SECTOR_B]));
    service.displaySectorsOnMap(8507000, 'ch:1:sloid:7000:0:1');

    // When
    service.highlightSectorsBySloids(['ch:1:sloid:7000:1:1', 'ch:1:sloid:7000:1:2']);

    // Then
    expect(mapServiceSpy.map.getSource).toHaveBeenCalledWith('hovered_sector');
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.FeatureCollection;
    expect(data.features).toHaveLength(2);
    expect(data.features.map((feature) => (feature.geometry as Point).coordinates)).toEqual([
      [7.44908190053, 46.96102079646],
      [7.4479859044, 46.96057330114],
    ]);
  });

  it('should clear the highlight when no sloid is currently displayed on the map', () => {
    // Given
    sectorInternalService.getSectorsValidToday.mockReturnValue(of([SECTOR_A, SECTOR_B]));
    service.displaySectorsOnMap(8507000, 'ch:1:sloid:7000:0:1');

    // When
    service.highlightSectorsBySloids(['unknown-sloid']);

    // Then
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.FeatureCollection;
    expect(data.features).toHaveLength(0);
  });

  it('should clear highlighted Sectors on map', () => {
    service.clearHighlightedSector();

    expect(mapServiceSpy.map.getSource).toHaveBeenCalledWith('hovered_sector');
    const data = sourceMock.setData.mock.calls.at(-1)?.[0] as GeoJSON.FeatureCollection;
    expect(data.features).toHaveLength(0);
  });
});
