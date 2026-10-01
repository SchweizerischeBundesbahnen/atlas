import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BusinessOrganisationSelectComponent } from './business-organisation-select.component';
import { FormControl, FormGroup } from '@angular/forms';
import { form } from '@angular/forms/signals';
import { of } from 'rxjs';
import { BusinessOrganisation } from '../../../api';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { mock, mockClear } from 'vitest-mock-extended';
import { inputBinding, provideZonelessChangeDetection, signal } from '@angular/core';
import { By } from '@angular/platform-browser';
import { NgSelectComponent } from '@ng-select/ng-select';

describe('BusinessOrganisationSelectComponent', () => {
  let component: BusinessOrganisationSelectComponent;
  let fixture: ComponentFixture<BusinessOrganisationSelectComponent>;
  let formGroupInput: ReturnType<typeof signal<FormGroup>>;
  let controlNameInput: ReturnType<typeof signal<string>>;

  const businessOrganisationServiceSpy = mock<BusinessOrganisationService>();
  businessOrganisationServiceSpy.getAllBusinessOrganisations.mockReturnValue(of({ objects: [] }));

  beforeEach(() => {
    mockClear(businessOrganisationServiceSpy);

    TestBed.configureTestingModule({
      providers: [
        provideZonelessChangeDetection(),
        {
          provide: BusinessOrganisationService,
          useValue: businessOrganisationServiceSpy,
        },
        translateServiceProvider,
      ],
    });

    const formGroupInputName: keyof BusinessOrganisationSelectComponent = 'formGroup';
    const controlNameInputName: keyof BusinessOrganisationSelectComponent = 'controlName';
    formGroupInput = signal(
      new FormGroup({
        testControl: new FormControl(null),
      })
    );
    controlNameInput = signal('testControl');
    fixture = TestBed.createComponent(BusinessOrganisationSelectComponent, {
      bindings: [
        inputBinding(formGroupInputName, formGroupInput),
        inputBinding(controlNameInputName, controlNameInput),
      ],
    });
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  // To be able to find ch:1:sboid:1 we should sort by sboid instead of organisation number
  it('should search by businessOrganisation sorted by sboid', () => {
    component.searchBusinessOrganisation('ch:1:sboid:1');
    expect(businessOrganisationServiceSpy.getAllBusinessOrganisations).toHaveBeenCalledExactlyOnceWith(
      ['ch:1:sboid:1'],
      [],
      undefined,
      undefined,
      undefined,
      undefined,
      ['sboid,ASC']
    );
  });

  it('preserves the legacy FormGroup value changes and replaces its subscription on input change', () => {
    const selectionChanged = vi.fn();
    component.selectedBusinessOrganisationChanged.subscribe(selectionChanged);
    expect(fixture.debugElement.query(By.directive(SearchSelectComponent))).not.toBeNull();

    const oldControl = formGroupInput().controls['testControl'];
    oldControl.setValue('ch:1:sboid:1');
    expect(selectionChanged).toHaveBeenCalledWith('ch:1:sboid:1');
    expect(businessOrganisationServiceSpy.getAllBusinessOrganisations).toHaveBeenCalledWith(
      ['ch:1:sboid:1'],
      [],
      undefined,
      undefined,
      undefined,
      undefined,
      ['sboid,ASC']
    );

    formGroupInput.set(new FormGroup({ testControl: new FormControl(null) }));
    fixture.detectChanges();
    selectionChanged.mockClear();
    oldControl.setValue('old');
    expect(selectionChanged).not.toHaveBeenCalled();

    formGroupInput().controls['testControl'].setValue('new');
    expect(selectionChanged).toHaveBeenCalledExactlyOnceWith('new');
  });

  it('binds a Signal Form field and searches on programmatic value changes', async () => {
    const model = signal<{ businessOrganisation: BusinessOrganisation | string | null }>({
      businessOrganisation: null,
    });
    const boField = TestBed.runInInjectionContext(() => form(model).businessOrganisation);
    const signalFixture = TestBed.createComponent(BusinessOrganisationSelectComponent, {
      bindings: [inputBinding('field', signal(boField))],
    });
    const signalComponent = signalFixture.componentInstance;
    const selectionChanged = vi.fn();
    signalComponent.selectedBusinessOrganisationChanged.subscribe(selectionChanged);
    signalFixture.detectChanges();

    expect(signalFixture.debugElement.query(By.directive(SearchSelectComponent))).toBeNull();
    expect(signalFixture.nativeElement.querySelector('ng-select')).not.toBeNull();

    boField().value.set('ch:1:sboid:42');
    await signalFixture.whenStable();
    expect(businessOrganisationServiceSpy.getAllBusinessOrganisations).toHaveBeenCalledWith(
      ['ch:1:sboid:42'],
      [],
      undefined,
      undefined,
      undefined,
      undefined,
      ['sboid,ASC']
    );
    expect(selectionChanged).toHaveBeenCalledExactlyOnceWith('ch:1:sboid:42');

    boField().value.set(null);
    await signalFixture.whenStable();
    expect(selectionChanged).toHaveBeenLastCalledWith(null);
  });

  it('writes a selected BO into its Signal Form and emits the selected organisation', async () => {
    const selectedBo: BusinessOrganisation = {
      sboid: 'ch:1:sboid:42',
      descriptionDe: 'Bern',
      descriptionFr: 'Bern',
      descriptionIt: 'Bern',
      descriptionEn: 'Bern',
      abbreviationDe: 'BE',
      abbreviationFr: 'BE',
      abbreviationIt: 'BE',
      abbreviationEn: 'BE',
      validFrom: new Date(0),
      validTo: new Date(0),
    };
    businessOrganisationServiceSpy.getAllBusinessOrganisations.mockReturnValue(of({ objects: [selectedBo] }));
    const model = signal<{ businessOrganisation: BusinessOrganisation | string | null }>({
      businessOrganisation: null,
    });
    const boField = TestBed.runInInjectionContext(() => form(model).businessOrganisation);
    const signalFixture = TestBed.createComponent(BusinessOrganisationSelectComponent, {
      bindings: [inputBinding('field', signal(boField))],
    });
    const selected = vi.fn();
    signalFixture.componentInstance.boSelectionChanged.subscribe(selected);
    signalFixture.detectChanges();
    signalFixture.componentInstance.searchBusinessOrganisation('ch:1:sboid:42');
    await signalFixture.whenStable();

    const ngSelect = signalFixture.debugElement.query(By.directive(NgSelectComponent))
      .componentInstance as NgSelectComponent;
    ngSelect.select(ngSelect.itemsList.items[0]);
    await signalFixture.whenStable();

    expect(boField().value()).toBe('ch:1:sboid:42');
    expect(selected).toHaveBeenCalledExactlyOnceWith(selectedBo);
  });
});
