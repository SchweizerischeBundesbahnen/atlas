import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Map } from 'maplibre-gl';

@Component({
  selector: 'app-sepodi-overview',
  templateUrl: './sepodi-overview.component.html',
  styleUrls: ['./sepodi-overview.component.scss'],
})
export class SepodiOverviewComponent implements AfterViewInit, OnDestroy {
  map: Map | undefined;

  @ViewChild('map')
  private mapContainer!: ElementRef<HTMLElement>;

  ngAfterViewInit() {
    const initialState = { lng: 8.0, lat: 46.5, zoom: 7 };

    this.map = new Map({
      container: this.mapContainer.nativeElement,
      style: 'https://demotiles.maplibre.org/style.json', // style URL
      center: [initialState.lng, initialState.lat],
      zoom: initialState.zoom,
    });
  }

  ngOnDestroy() {
    this.map?.remove();
  }
}
