import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MapComponent } from './map.component';
import { AppTestingModule } from '../../../app.testing.module';
import { MAP_STYLES } from './map-options.service';
import { MapService } from './map.service';
import { Map } from 'maplibre-gl';

const mapSpy = jasmine.createSpyObj<Map>(['once']);
const mapService = jasmine.createSpyObj<MapService>(['initMap', 'switchToStyle', 'removeMap']);
mapService.initMap.and.returnValue(mapSpy);

describe('MapComponent', () => {
  let component: MapComponent;
  let fixture: ComponentFixture<MapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MapComponent],
      imports: [AppTestingModule],
      providers: [{ provide: MapService, useValue: mapService }],
    }).compileComponents();

    fixture = TestBed.createComponent(MapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open map style selection', () => {
    expect(component.showMapStyleSelection).toBeFalse();

    component.openStyleSelection();
    expect(component.showMapStyleSelection).toBeTrue();
  });

  it('should switch map style selection via service', () => {
    mapService.switchToStyle.and.callFake((i) => i);

    const newStyle = MAP_STYLES[1];
    component.switchToStyle(newStyle);

    expect(mapService.switchToStyle).toHaveBeenCalled();
    expect(component.currentMapStyle).toEqual(newStyle);
    expect(component.showMapStyleSelection).toBeFalse();
  });
});
