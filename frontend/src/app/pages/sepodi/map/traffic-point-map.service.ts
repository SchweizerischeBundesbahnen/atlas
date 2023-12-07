import { Injectable } from '@angular/core';
import { GeoJSONSource, MapGeoJSONFeature } from 'maplibre-gl';
import { MAP_TRAFFIC_POINT_LAYER_NAME } from './map-style';
import { Feature } from 'geojson';
import { CoordinatePair, TrafficPointElementsService, TrafficPointElementType } from '../../../api';
import { Pages } from '../../pages';
import { MapService } from './map.service';
import { filter } from 'rxjs/operators';
import { take } from 'rxjs';

export interface DisplayableTrafficPoint {
  type: TrafficPointElementType;
  sloid: string;
  designation: string;
  coordinates: CoordinatePair;
}

@Injectable({
  providedIn: 'root',
})
export class TrafficPointMapService {
  constructor(
    private mapService: MapService,
    private trafficPointElementsService: TrafficPointElementsService,
  ) {}

  static buildTrafficPointPopupInformation(features: MapGeoJSONFeature[]) {
    let popupHtml = '';

    features.forEach((point) => {
      const description = point.properties.designation
        ? `${point.properties.designation} - ${point.properties.sloid}`
        : point.properties.sloid;
      popupHtml +=
        `<a href="${Pages.SEPODI.path}/${Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path}/${point.properties.sloid}">` +
        `${description}</a> <br/>`;
    });

    return popupHtml;
  }

  displayTrafficPointsOnMap(servicePointNumber: number) {
    this.mapService.mapInitialized
      .pipe(
        filter((initialized) => initialized),
        take(1),
      )
      .subscribe(() => {
        this.trafficPointElementsService
          .getTrafficPointsOfServicePointValidToday(servicePointNumber)
          .subscribe((points) => {
            const trafficPoints: DisplayableTrafficPoint[] = points
              .filter((point) => !!point.trafficPointElementGeolocation?.wgs84)
              .map((point) => {
                return {
                  sloid: point.sloid!,
                  designation: point.designation!,
                  type: point.trafficPointElementType,
                  coordinates: point.trafficPointElementGeolocation!.wgs84!,
                };
              });
            this.setDisplayedTrafficPoints(trafficPoints);
          });
      });
  }

  setDisplayedTrafficPoints(trafficPoints: DisplayableTrafficPoint[]) {
    const source = this.mapService.map.getSource(MAP_TRAFFIC_POINT_LAYER_NAME) as
      | GeoJSONSource
      | undefined;

    const trafficPointGeoInformation: Feature[] = trafficPoints.map((point) => {
      return {
        type: 'Feature',
        geometry: {
          type: 'Point',
          coordinates: [point.coordinates.east, point.coordinates.north],
        },
        properties: {
          type: point.type,
          sloid: point.sloid,
          designation: point.designation,
        },
      };
    });
    source?.setData({
      type: 'FeatureCollection',
      features: trafficPointGeoInformation,
    });
  }

  clearDisplayedTrafficPoints() {
    this.setDisplayedTrafficPoints([]);
  }

  displayCurrentTrafficPoint(coordinates?: CoordinatePair) {
    // todo: no fly to
    this.mapService.mapInitialized
      .pipe(
        filter((initialized) => initialized),
        take(1),
      )
      .subscribe(() => {
        const source = this.mapService.map.getSource('current_traffic_point') as GeoJSONSource;
        const coordinatesToSet = [coordinates?.east ?? 0, coordinates?.north ?? 0];
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

  clearCurrentTrafficPoint() {
    this.displayCurrentTrafficPoint();
  }
}
