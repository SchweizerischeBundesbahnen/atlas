import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { MapComponent } from './map.component';
import { AppTestingModule } from '../../../app.testing.module';
import { MAP_STYLES } from './map-options';
import { CoordinatePairWGS84, MapService } from './map.service';
import { Map } from 'maplibre-gl';
import { BehaviorSubject, of } from 'rxjs';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { adminPermissionServiceMock } from '../../../app.testing.mocks';
import { SERVICE_POINT_MIN_ZOOM } from './map-style';
import { mock, mockDeep } from 'vitest-mock-extended';
import { By } from '@angular/platform-browser';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { BusinessOrganisation } from '../../../api';
import { MapBoFilterDialogComponent } from './map-bo-filter-dialog/map-bo-filter-dialog.component';

describe('MapComponent', () => {
  let component: MapComponent;
  let fixture: ComponentFixture<MapComponent>;

  const mapCanvasMock = document.createElement('canvas');
  const mapMock = mockDeep<Map>();
  mapMock.getCanvas.mockReturnValue(mapCanvasMock);

  const mapServiceSpy = mock<MapService>();
  mapServiceSpy.mapInitialized = new BehaviorSubject(true);
  mapServiceSpy.servicePointsShown = new BehaviorSubject(false);
  mapServiceSpy.map = mapMock;
  mapServiceSpy.initMap.mockReturnValue(mapMock);
  mapServiceSpy.clickedGeographyCoordinates = new BehaviorSubject<CoordinatePairWGS84>({
    lat: 0,
    lng: 0,
  });

  const dialogService = mock<DialogService>();

  const sbb = { sboid: 'ch:1:sboid:100001' } as BusinessOrganisation;

  function setBoFilter(businessOrganisations: BusinessOrganisation[]) {
    (mapServiceSpy as unknown as { boFilter: () => BusinessOrganisation[] }).boFilter = () => businessOrganisations;
  }

  function setBoFilterActive(active: boolean) {
    (mapServiceSpy as unknown as { boFilterActive: () => boolean }).boFilterActive = () => active;
  }

  beforeEach(() => {
    dialogService.openDialogDataWithCustomResult.mockReset();
    mapServiceSpy.applyBoFilter.mockClear();
    setBoFilter([]);
    setBoFilterActive(false);

    TestBed.configureTestingModule({
      imports: [AppTestingModule, MapComponent],
      providers: [
        { provide: MapService, useValue: mapServiceSpy },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: DialogService, useValue: dialogService },
      ],
    });

    fixture = TestBed.createComponent(MapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open map style selection', () => {
    expect(component.showMapStyleSelection).toBe(false);

    component.toggleStyleSelection();
    expect(component.showMapStyleSelection).toBe(true);
  });

  it('should switch map style selection via service', () => {
    mapServiceSpy.switchToStyle.mockImplementation((i) => i);

    const newStyle = MAP_STYLES[1];
    component.switchToStyle(newStyle);

    expect(mapServiceSpy.switchToStyle).toHaveBeenCalled();
    expect(component.currentMapStyle).toEqual(newStyle);
    expect(component.showMapStyleSelection).toBe(false);
  });

  it('should toggle legend', () => {
    expect(component.showMapLegend).toBe(false);

    component.toggleLegend();
    expect(component.showMapLegend).toBe(true);

    component.toggleLegend();
    expect(component.showMapLegend).toBe(false);
  });

  it('should increase zoom when zoomIn() is called', () => {
    component.zoomIn();
    expect(component.map.zoomTo).toHaveBeenCalledWith(component.map.getZoom() + 0.75, {
      duration: 500,
    });
  });

  it('should decrease zoom when zoomOut() is called', () => {
    component.zoomOut();
    expect(component.map.zoomTo).toHaveBeenCalledWith(component.map.getZoom() - 0.75, {
      duration: 500,
    });
  });

  it('should zoom to SERVICE_POINT_MIN_ZOOM', () => {
    component.zoomToServicePointMin();
    expect(component.map.zoomTo).toHaveBeenCalledWith(SERVICE_POINT_MIN_ZOOM, {
      duration: 500,
    });
  });

  it('should center into swiss country when goHome() is called', () => {
    component.goHome();
    expect(component.map.flyTo).toHaveBeenCalledWith({
      center: [8.2275, 46.8182],
      zoom: 7.25,
      speed: 0.8,
    });
  });

  describe('bo filter button', () => {
    function boFilterButton(): HTMLElement {
      return fixture.debugElement.query(By.css('[data-cy="sepodi-map-bo-filter"]')).nativeElement as HTMLElement;
    }

    it('should open the bo filter dialog pre-filled with the currently applied filter', () => {
      // Given
      setBoFilter([sbb]);
      dialogService.openDialogDataWithCustomResult.mockReturnValue(of(undefined));

      // When
      component.openBoFilterDialog();

      // Then
      expect(dialogService.openDialogDataWithCustomResult).toHaveBeenCalledWith(
        expect.objectContaining({ businessOrganisations: [sbb] }),
        MapBoFilterDialogComponent,
        expect.objectContaining({ width: expect.any(String) })
      );
    });

    it('should open the bo filter dialog with a stable width', () => {
      // Given
      dialogService.openDialogDataWithCustomResult.mockReturnValue(of(undefined));

      // When
      component.openBoFilterDialog();

      // Then
      expect(dialogService.openDialogDataWithCustomResult).toHaveBeenCalledWith(
        expect.anything(),
        MapBoFilterDialogComponent,
        { width: 'min(90vw, 800px)' }
      );
    });

    it('should apply the selection returned by the dialog', () => {
      // Given
      dialogService.openDialogDataWithCustomResult.mockReturnValue(of([sbb]));

      // When
      component.openBoFilterDialog();

      // Then
      expect(mapServiceSpy.applyBoFilter).toHaveBeenCalledWith([sbb]);
    });

    it('should not touch the active filter when the dialog is cancelled', () => {
      // Given
      dialogService.openDialogDataWithCustomResult.mockReturnValue(of(undefined));

      // When
      component.openBoFilterDialog();

      // Then
      expect(mapServiceSpy.applyBoFilter).not.toHaveBeenCalled();
    });

    it('should open the dialog when the filter button is clicked', () => {
      // Given
      dialogService.openDialogDataWithCustomResult.mockReturnValue(of(undefined));

      // When
      boFilterButton().click();

      // Then
      expect(dialogService.openDialogDataWithCustomResult).toHaveBeenCalled();
    });

    function boFilterIcon(): HTMLElement {
      return boFilterButton().querySelector('[data-cy="bo-filter-icon"]') as HTMLElement;
    }

    it('should show the outlined funnel icon when no filter is active', () => {
      // Given
      setBoFilterActive(false);
      fixture.detectChanges();

      // Then
      expect(boFilterIcon().classList).toContain('bi-funnel');
      expect(boFilterIcon().classList).not.toContain('bi-funnel-fill');
    });

    it('should show the filled funnel icon when a filter is active', () => {
      // Given
      setBoFilterActive(true);
      setBoFilter([sbb]);
      fixture.detectChanges();

      // Then
      expect(boFilterIcon().classList).toContain('bi-funnel-fill');
      expect(boFilterIcon().classList).not.toContain('bi-funnel');
    });

    it('should not show a count badge when no filter is active', () => {
      // Given
      setBoFilterActive(false);
      fixture.detectChanges();

      // Then
      expect(boFilterButton().querySelector('[data-cy="bo-filter-count"]')).toBeFalsy();
    });

    it('should show the number of filtered business organisations as badge', () => {
      // Given
      setBoFilterActive(true);
      setBoFilter([sbb, { sboid: 'ch:1:sboid:100002' } as BusinessOrganisation]);
      fixture.detectChanges();

      // Then
      expect(boFilterButton().querySelector('[data-cy="bo-filter-count"]')?.textContent?.trim()).toBe('2');
    });

    it('should expose the active filter state to screen readers', () => {
      // Given
      setBoFilterActive(true);
      setBoFilter([sbb]);
      fixture.detectChanges();

      // Then
      expect(boFilterButton().getAttribute('aria-pressed')).toBe('true');
      expect(boFilterButton().getAttribute('aria-label')).toContain('SEPODI.MAP_BO_FILTER.TOOLTIP_ACTIVE');
    });

    it('should expose the inactive filter state to screen readers', () => {
      // Given
      setBoFilterActive(false);
      fixture.detectChanges();

      // Then
      expect(boFilterButton().getAttribute('aria-pressed')).toBe('false');
      expect(boFilterButton().getAttribute('aria-label')).toContain('SEPODI.MAP_BO_FILTER.TOOLTIP');
    });
  });
});
