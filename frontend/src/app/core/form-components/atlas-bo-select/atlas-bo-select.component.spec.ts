import { Component, input, output, Pipe, PipeTransform, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Field, form } from '@angular/forms/signals';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { BusinessOrganisation } from '../../../api/index';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';
import { BoSelectionDisplayPipe } from '../../pipe/bo-selection-display.pipe';
import { AtlasBoSelectComponent } from './atlas-bo-select.component';
import { AtlasSearchSelectComponent } from '@atlas/form/lib/atlas-search-select/atlas-search-select.component';
import { AtlasLabelFieldComponent } from '@atlas/form/lib/atlas-label-field/atlas-label-field.component';

@Component({
  selector: 'atlas-search-select',
  standalone: true,
  template: '<ng-content />',
})
class MockAtlasSearchSelectComponent {
  readonly bindValue = input('');
  readonly items = input<BusinessOrganisation[]>([]);
  readonly disabled = input(false);
  readonly field = input.required<Field<BusinessOrganisation | null>>();

  readonly changeTrigger = output<BusinessOrganisation | null>();
  readonly searchTrigger = output<string>();
}

@Component({
  selector: 'atlas-label-field',
  standalone: true,
  template: '',
})
class MockAtlasLabelFieldComponent {
  readonly fieldLabel = input('');
  readonly required = input(false);
  readonly fieldExamples = input<Array<{ label: string }>>([]);
}

@Pipe({
  name: 'boSelectionDisplay',
  standalone: true,
})
class MockBoSelectionDisplayPipe implements PipeTransform {
  transform(value: unknown): string {
    return String(value ?? '');
  }
}

describe('AtlasBoSelectComponent', () => {
  let fixture: ComponentFixture<AtlasBoSelectComponent>;
  let component: AtlasBoSelectComponent;

  let businessOrganisationService: Pick<BusinessOrganisationService, 'getAllBusinessOrganisations'>;

  const createField = (initialValue: BusinessOrganisation | null): Field<BusinessOrganisation | null> =>
    TestBed.runInInjectionContext(() => {
      const model = signal({ value: initialValue });
      const testForm = form(model);
      return testForm.value;
    });

  const toBo = (sboid: string) => ({ sboid }) as BusinessOrganisation;

  const getSearchSelect = () =>
    fixture.debugElement.query(By.directive(MockAtlasSearchSelectComponent))?.componentInstance as
      MockAtlasSearchSelectComponent | undefined;

  const getLabelField = () =>
    fixture.debugElement.query(By.directive(MockAtlasLabelFieldComponent))?.componentInstance as
      MockAtlasLabelFieldComponent | undefined;

  beforeEach(() => {
    businessOrganisationService = {
      getAllBusinessOrganisations: vi.fn(),
    };

    TestBed.configureTestingModule({
      imports: [AtlasBoSelectComponent],
      providers: [{ provide: BusinessOrganisationService, useValue: businessOrganisationService }],
    }).overrideComponent(AtlasBoSelectComponent, {
      remove: {
        imports: [AtlasLabelFieldComponent, BoSelectionDisplayPipe, AtlasSearchSelectComponent],
      },
      add: {
        imports: [MockAtlasLabelFieldComponent, MockBoSelectionDisplayPipe, MockAtlasSearchSelectComponent],
      },
    });

    fixture = TestBed.createComponent(AtlasBoSelectComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('field', createField(null));
    fixture.detectChanges();
  });

  it('correct inputs on child components', () => {
    fixture.componentRef.setInput('valueExtraction', 'customId');
    fixture.componentRef.setInput('disabled', true);
    fixture.componentRef.setInput('formModus', false);
    fixture.detectChanges();

    const searchSelect = getSearchSelect();
    const labelField = getLabelField();

    expect(searchSelect).toBeTruthy();
    expect(labelField).toBeTruthy();
    expect(searchSelect?.bindValue()).toBe('customId');
    expect(searchSelect?.disabled()).toBe(true);
    expect(labelField?.required()).toBe(false);
    expect(labelField?.fieldExamples()).toEqual([]);
  });

  it('call service on searchTrigger', () => {
    const restrictions = ['SBOID_1', 'SBOID_2'];
    const serviceResult = [toBo('SBOID_1'), toBo('SBOID_2')];

    vi.mocked(businessOrganisationService.getAllBusinessOrganisations).mockReturnValue(of({ objects: serviceResult }));

    fixture.componentRef.setInput('sboidsRestrictions', restrictions);
    fixture.detectChanges();

    const searchSelect = getSearchSelect();
    searchSelect?.searchTrigger.emit('bern');
    fixture.detectChanges();

    expect(businessOrganisationService.getAllBusinessOrganisations).toHaveBeenCalledWith(
      ['bern'],
      restrictions,
      undefined,
      undefined,
      undefined,
      undefined,
      ['sboid,ASC']
    );
    expect(searchSelect?.items()).toEqual(serviceResult);
  });

  it("don't call service when empty search string", () => {
    const searchSelect = getSearchSelect();

    searchSelect?.searchTrigger.emit('');
    fixture.detectChanges();

    expect(businessOrganisationService.getAllBusinessOrganisations).not.toHaveBeenCalled();
  });

  it('reemit changeTrigger', () => {
    const selected = toBo('SBOID_123');
    const emitSpy = vi.fn();

    component.boSelectionChanged.subscribe(emitSpy);

    const searchSelect = getSearchSelect();
    searchSelect?.changeTrigger.emit(selected);

    expect(emitSpy).toHaveBeenCalledWith(selected);
  });
});
