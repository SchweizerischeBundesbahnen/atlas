import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Map, Popup, ResourceTypeEnum } from 'maplibre-gl';
import { MapOptionsService } from '../map-options.service';
import {
  MAP_SOURCE_NAME,
  MAP_STYLE_SPEC,
  SERVICE_POINTS_LAYER_SPEC,
  SWISS_BOUNDING_BOX,
} from '../map-constants';
import { GeoJsonProperties } from 'geojson';

@Component({
  selector: 'app-sepodi-overview',
  templateUrl: './sepodi-overview.component.html',
  styleUrls: ['./sepodi-overview.component.scss'],
})
export class SepodiOverviewComponent implements AfterViewInit, OnDestroy {
  map: Map | undefined;

  @ViewChild('map')
  private mapContainer!: ElementRef<HTMLElement>;

  constructor(private mapOptionsService: MapOptionsService) {}

  ngAfterViewInit() {
    this.map = new Map({
      container: this.mapContainer.nativeElement,
      style: MAP_STYLE_SPEC,
      bounds: SWISS_BOUNDING_BOX,
      transformRequest: (url: string, resourceType?: ResourceTypeEnum) =>
        this.mapOptionsService.authoriseRequest(url, resourceType),
    });

    this.map.once('style.load', () => {
      this.map?.addLayer(SERVICE_POINTS_LAYER_SPEC);
      this.map?.on('click', MAP_SOURCE_NAME, (e) => {
        new Popup()
          .setLngLat(e.lngLat)
          .setHTML(
            (e.features ?? [])
              .map((feature: GeoJSON.Feature) =>
                Object.entries(feature.properties ?? {})
                  .map((entry) => {
                    const [key, value] = entry;
                    return `${key}:${value}<br/>`;
                  })
                  .join()
              )
              .join('<br/>')
          )
          .addTo(this.map!);
      });
    });

    this.map.on('mouseenter', MAP_SOURCE_NAME, () => {
      this.map!.getCanvas().style.cursor = 'pointer';
    });

    this.map.on('mouseleave', MAP_SOURCE_NAME, () => {
      this.map!.getCanvas().style.cursor = '';
    });
  }

  ngOnDestroy() {
    this.map?.remove();
  }
}
