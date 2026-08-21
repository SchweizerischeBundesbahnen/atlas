import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { Component, input, output } from '@angular/core';
import { By } from '@angular/platform-browser';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Field } from '@angular/forms/signals';
import { AppTestingModule } from '../../../../app.testing.module';
import { BusinessOrganisation } from '../../../../api';
import { MapBoFilterDialogComponent } from './map-bo-filter-dialog.component';
import { MapBoFilterDialogData } from './map-bo-filter-dialog-data';
import { AtlasBoSelectComponent } from '../../../../core/form-components/atlas-bo-select/atlas-bo-select.component';
import { DialogCloseComponent } from '../../../../core/components/dialog/close/dialog-close.component';
import { DialogContentComponent } from '../../../../core/components/dialog/content/dialog-content.component';
import { DialogFooterComponent } from '../../../../core/components/dialog/footer/dialog-footer.component';

@Component({
  selector: 'atlas-bo-select',
  template: '',
  standalone: true,
})
class MockBoSelectComponent {
  readonly field = input.required<Field<BusinessOrganisation | string | null>>();
  readonly formModus = input(true);
  readonly valueExtraction = input('sboid');
  readonly boSelectionChanged = output<BusinessOrganisation | string | null>();
}

const sbb: BusinessOrganisation = {
  sboid: 'ch:1:sboid:100001',
  organisationNumber: 1,
  descriptionDe: 'Schweizerische Bundesbahnen',
  descriptionFr: 'Chemins de fer fédéraux suisses',
  descriptionIt: 'Ferrovie federali svizzere',
  descriptionEn: 'Swiss Federal Railways',
  abbreviationDe: 'SBB',
  abbreviationFr: 'CFF',
  abbreviationIt: 'FFS',
  abbreviationEn: 'SBB',
  validFrom: new Date('2020-01-01'),
  validTo: new Date('2099-12-31'),
};

const bls: BusinessOrganisation = {
  sboid: 'ch:1:sboid:100002',
  organisationNumber: 2,
  descriptionDe: 'BLS AG',
  descriptionFr: 'BLS SA',
  descriptionIt: 'BLS SA',
  descriptionEn: 'BLS Ltd',
  abbreviationDe: 'BLS',
  abbreviationFr: 'BLS',
  abbreviationIt: 'BLS',
  abbreviationEn: 'BLS',
  validFrom: new Date('2020-01-01'),
  validTo: new Date('2099-12-31'),
};

describe('MapBoFilterDialogComponent', () => {
  let component: MapBoFilterDialogComponent;
  let fixture: ComponentFixture<MapBoFilterDialogComponent>;
  let dialogRefSpy: Mocked<Pick<MatDialogRef<MapBoFilterDialogComponent>, 'close'>>;

  const dialogData: MapBoFilterDialogData = {
    title: 'SEPODI.MAP_BO_FILTER.TITLE',
    message: '',
    businessOrganisations: [],
  };

  function boSelect(): MockBoSelectComponent {
    return fixture.debugElement.query(By.directive(MockBoSelectComponent)).componentInstance;
  }

  function selectedRows(): HTMLElement[] {
    return fixture.debugElement
      .queryAll(By.css('[data-cy="map-bo-filter-selected"]'))
      .map((row) => row.nativeElement as HTMLElement);
  }

  function pick(businessOrganisation: BusinessOrganisation) {
    boSelect().boSelectionChanged.emit(businessOrganisation);
    fixture.detectChanges();
  }

  function createComponent(businessOrganisations: BusinessOrganisation[] = []) {
    dialogData.businessOrganisations = businessOrganisations;
    fixture = TestBed.createComponent(MapBoFilterDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  beforeEach(() => {
    dialogRefSpy = { close: vi.fn() };

    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        MapBoFilterDialogComponent,
        DialogCloseComponent,
        DialogContentComponent,
        DialogFooterComponent,
      ],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: dialogData },
        { provide: MatDialogRef, useValue: dialogRefSpy },
      ],
    }).overrideComponent(MapBoFilterDialogComponent, {
      remove: { imports: [AtlasBoSelectComponent] },
      add: { imports: [MockBoSelectComponent] },
    });

    createComponent();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should start without a selection when no filter is applied', () => {
    expect(component.selectedBusinessOrganisations()).toEqual([]);
    expect(selectedRows()).toHaveLength(0);
  });

  it('should prefill the selection with the currently applied filter', () => {
    // When
    createComponent([sbb, bls]);

    // Then
    expect(component.selectedBusinessOrganisations()).toEqual([sbb, bls]);
  });

  it('should add a picked business organisation to the selection', () => {
    // When
    pick(sbb);

    // Then
    expect(component.selectedBusinessOrganisations()).toEqual([sbb]);
  });

  it('should not add the same business organisation twice', () => {
    // Given
    pick(sbb);

    // When
    pick({ ...sbb });

    // Then
    expect(component.selectedBusinessOrganisations()).toEqual([sbb]);
  });

  it('should reset the search field after a pick', () => {
    // When
    pick(sbb);

    // Then
    expect(boSelect().field()().value()).toBeNull();
  });

  it('should ignore a plain search string emitted by the bo select', () => {
    // When
    boSelect().boSelectionChanged.emit('SBB');
    fixture.detectChanges();

    // Then
    expect(component.selectedBusinessOrganisations()).toEqual([]);
  });

  it('should ignore an empty selection emitted by the bo select', () => {
    // When
    boSelect().boSelectionChanged.emit(null);
    fixture.detectChanges();

    // Then
    expect(component.selectedBusinessOrganisations()).toEqual([]);
  });

  it('should display name, abbreviation and organisation number per selected business organisation', () => {
    // When
    pick(sbb);

    // Then
    const row = selectedRows()[0];
    expect(row.textContent).toContain('Schweizerische Bundesbahnen');
    expect(row.textContent).toContain('SBB');
    expect(row.textContent).toContain('1');
  });

  it('should remove a single business organisation from the selection', () => {
    // Given
    createComponent([sbb, bls]);

    // When
    component.removeBusinessOrganisation(sbb);
    fixture.detectChanges();

    // Then
    expect(component.selectedBusinessOrganisations()).toEqual([bls]);
    expect(selectedRows()).toHaveLength(1);
  });

  it('should remove a business organisation when its remove button is clicked', () => {
    // Given
    createComponent([sbb, bls]);

    // When
    fixture.debugElement.queryAll(By.css('[data-cy="map-bo-filter-remove"]'))[0].nativeElement.click();
    fixture.detectChanges();

    // Then
    expect(component.selectedBusinessOrganisations()).toEqual([bls]);
  });

  it('should clear the selection on reset', () => {
    // Given
    createComponent([sbb, bls]);

    // When
    component.reset();
    fixture.detectChanges();

    // Then
    expect(component.selectedBusinessOrganisations()).toEqual([]);
    expect(selectedRows()).toHaveLength(0);
  });

  it('should keep the dialog open on reset', () => {
    // Given
    createComponent([sbb]);

    // When
    component.reset();

    // Then
    expect(dialogRefSpy.close).not.toHaveBeenCalled();
  });

  it('should return the selection as dialog result on apply', () => {
    // Given
    createComponent([sbb, bls]);

    // When
    component.apply();

    // Then
    expect(dialogRefSpy.close).toHaveBeenCalledWith([sbb, bls]);
  });

  it('should return an empty selection as dialog result on apply after a reset', () => {
    // Given
    createComponent([sbb]);
    component.reset();

    // When
    component.apply();

    // Then
    expect(dialogRefSpy.close).toHaveBeenCalledWith([]);
  });

  it('should close without a result on cancel', () => {
    // Given
    createComponent([sbb]);

    // When
    component.cancel();

    // Then
    expect(dialogRefSpy.close).toHaveBeenCalledWith();
  });

  it('should close without a result when the close icon is clicked', () => {
    // When
    fixture.debugElement.query(By.directive(DialogCloseComponent)).componentInstance.clicked.emit();

    // Then
    expect(dialogRefSpy.close).toHaveBeenCalledWith();
  });
});
