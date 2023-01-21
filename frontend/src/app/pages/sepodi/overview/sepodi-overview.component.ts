import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import { Map, MapMouseEvent, Popup, ResourceTypeEnum } from 'maplibre-gl';
import { MapOptionsService } from '../map-options.service';
import {
  MAP_SOURCE_NAME,
  MAP_STYLE_SPEC,
  MAP_ZOOM_DETAILS,
  SWISS_BOUNDING_BOX,
} from '../map-configuration';

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
    console.debug(MAP_STYLE_SPEC);
    this.map = new Map({
      container: this.mapContainer.nativeElement,
      style: MAP_STYLE_SPEC,
      bounds: SWISS_BOUNDING_BOX,
      transformRequest: (url: string, resourceType?: ResourceTypeEnum) =>
        this.mapOptionsService.authoriseRequest(url, resourceType),
    });

    this.map.once('style.load', () => {
      const mapInstance = this.map!;
      mapInstance.on('click', MAP_SOURCE_NAME, (e) => this.onClick(e));
      mapInstance.on('mouseenter', MAP_SOURCE_NAME, () => {
        if (this.showDetails()) {
          mapInstance.getCanvas().style.cursor = 'pointer';
        }
      });
      mapInstance.on('mouseleave', MAP_SOURCE_NAME, () => {
        mapInstance.getCanvas().style.cursor = '';
      });
    });
  }

  private showDetails(): boolean {
    return this.map != undefined && this.map.getZoom() >= MAP_ZOOM_DETAILS;
  }

  private onClick(e: MapMouseEvent & { features?: GeoJSON.Feature[] }) {
    if (!this.showDetails()) {
      return;
    }
    new Popup()
      .setLngLat(e.lngLat)
      .setHTML(
        (e.features ?? [])
          .map((feature) =>
            Object.entries(feature.properties ?? {})
              .map((entry) => {
                const [key, value] = entry;
                return `${key}:${value}`;
              })
              .join('<br/>')
          )
          .join('<hr/>')
      )
      .addTo(this.map!);
  }

  ngOnDestroy() {
    this.map?.remove();
  }
}
