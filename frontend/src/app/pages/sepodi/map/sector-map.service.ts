import { inject, Injectable, OnDestroy } from '@angular/core';
import { GeoJSONSource, MapGeoJSONFeature } from 'maplibre-gl';
import { MAP_SECTOR_LAYER_NAME } from './map-style';
import { Feature } from 'geojson';
import { CoordinatePair } from '../../../api';
import { Pages } from '../../pages';
import { MapService } from './map.service';
import { filter, takeUntil } from 'rxjs/operators';
import { Subject, take } from 'rxjs';
import { SectorInternalService } from '../../../api/service/sepodi/sector-internal.service';

export interface DisplayableSector {
  sloid: string;
  designation: string;
  trafficPointSloid: string;
  servicePointNumber: number;
  coordinates: CoordinatePair;
}

@Injectable({
  providedIn: 'root',
})
export class SectorMapService implements OnDestroy {
  private readonly mapService = inject(MapService);
  private readonly sectorInternalService = inject(SectorInternalService);
  private readonly onDestroy$ = new Subject<boolean>();

  static buildSectorPopupInformation(features: MapGeoJSONFeature[]) {
    let popupHtml = '';

    features.forEach((point) => {
      const description = point.properties.designation
        ? `${point.properties.designation} - ${point.properties.sloid}`
        : point.properties.sloid;

      const trafficPointSloid: string = point.properties.trafficPointSloid;
      const servicePointNumber = point.properties.servicePointNumber;
      popupHtml +=
        `<a href="${Pages.SEPODI.path}/${Pages.SERVICE_POINTS.path}/${servicePointNumber}/${Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path}/${trafficPointSloid}/${Pages.SECTORS.path}/${point.properties.sloid}">` +
        `${description}</a> <br/>`;
    });

    return popupHtml;
  }

  ngOnDestroy(): void {
    this.onDestroy$.next(true);
    this.onDestroy$.complete();
  }

  displaySectorsOnMap(servicePointNumber: number, trafficPointSloid: string) {
    this.mapService.mapInitialized
      .pipe(
        filter((initialized) => initialized),
        take(1),
        takeUntil(this.onDestroy$)
      )
      .subscribe(() => {
        this.sectorInternalService
          .getSectorsValidToday(trafficPointSloid)
          .subscribe((points) => {
            const sectors: DisplayableSector[] = points.map((point) => {
              return {
                sloid: point.sloid!,
                designation: point.designation,
                coordinates: point.sectorGeolocation!.wgs84,
                trafficPointSloid: point.trafficPointSloid,
                servicePointNumber: servicePointNumber,
              };
            });
            this.setDisplayedSectors(sectors);
          });
      });
  }

  private setDisplayedSectors(sectors: DisplayableSector[]) {
    const source = this.mapService.map.getSource(
      MAP_SECTOR_LAYER_NAME
    ) as GeoJSONSource;
    const sectorGeoInformation: Feature[] = sectors.map((point) => {
      return {
        type: 'Feature',
        geometry: {
          type: 'Point',
          coordinates: [point.coordinates.east, point.coordinates.north],
        },
        properties: {
          sloid: point.sloid,
          designation: point.designation,
          trafficPointSloid: point.trafficPointSloid,
          servicePointNumber: point.servicePointNumber,
        },
      };
    });
    source?.setData({
      type: 'FeatureCollection',
      features: sectorGeoInformation,
    });
  }

  clearDisplayedSectors() {
    this.setDisplayedSectors([]);
  }

  displayCurrentSector(coordinates?: CoordinatePair) {
    this.mapService.mapInitialized
      .pipe(
        filter((initialized) => initialized),
        take(1),
        takeUntil(this.onDestroy$)
      )
      .subscribe(() => {
        const source = this.mapService.map.getSource(
          'current_sector'
        ) as GeoJSONSource;
        const coordinatesToSet = [
          coordinates?.east ?? 0,
          coordinates?.north ?? 0,
        ];
        source.setData({
          type: 'Feature',
          geometry: {
            type: 'Point',
            coordinates: coordinatesToSet,
          },
          properties: {},
        });
        this.mapService.centerOn(coordinates);
      });
  }

  clearCurrentSector() {
    this.displayCurrentSector();
  }
}
