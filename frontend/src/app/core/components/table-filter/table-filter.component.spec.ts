import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TableFilterComponent } from './table-filter.component';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { By } from '@angular/platform-browser';
import moment from 'moment';
import {
  MatChipGrid,
  MatChipInput,
  MatChipInputEvent,
  MatChipRow,
} from '@angular/material/chips';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  MatDatepickerControl,
  MatDatepickerInput,
  MatDatepickerInputEvent,
  MatDatepickerPanel,
} from '@angular/material/datepicker';
import { Moment } from 'moment/moment';
import { FormControl, FormGroup } from '@angular/forms';
import { TableFilterMultiSelect } from './config/table-filter-multiselect';
import { TableFilterDateSelect } from './config/table-filter-date-select';
import { TableFilterChip } from './config/table-filter-chip';
import { TableFilterSearchSelect } from './config/table-filter-search-select';
import { TableFilterSearchType } from './config/table-filter-search-type';
import { BusinessOrganisation, TimetableFieldNumber } from '../../../api';
import { TableFilterSingleSearch } from './config/table-filter-single-search';
import { AtlasSlideToggleComponent } from '../../form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { TableFilterBoolean } from './config/table-filter-boolean';
import { provideNativeDateAdapter } from '@angular/material/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TransportCompanySelectComponent } from '../../form-components/tu-select/transport-company-select.component';
import { TimetableFieldNumberSelectComponent } from '../../form-components/ttfn-select/timetable-field-number-select.component';
import { SelectComponent } from '../../form-components/select/select.component';
import { BusinessOrganisationSelectComponent } from '../../form-components/bo-select/business-organisation-select.component';

/* eslint-disable  @typescript-eslint/no-explicit-any */

@Component({
  selector: 'input [matChipInputFor]',
  template: '',
})
class MockMatChipInputComponent {
  @Input() matChipInputFor: any;
  @Input() formControl = new FormControl();
  @Output() matChipInputTokenEnd: EventEmitter<MatChipInputEvent> =
    new EventEmitter<MatChipInputEvent>();
}

@Component({
  selector: 'atlas-select',
  template: '',
})
class MockAtlasSelectComponent {
  @Output() selectChanged = new EventEmitter();

  @Input() label: string | undefined;
  @Input() placeHolderLabel = 'FORM.DROPDOWN_PLACEHOLDER';
  @Input() optionTranslateLabelPrefix: string | undefined;
  @Input() multiple = false;
  @Input() dataCy!: string;
  @Input() controlName: string | null = null;
  @Input() formGroup!: FormGroup;
  @Input() options: any[] = [];
  @Input() value: any;
  @Input() additionalLabelspace = true;
  @Input() required = false;
  @Input() disabled = false;
}

@Component({
  selector: 'input [matDatepicker]',
  template: '',
})
class MockMatDatepickerInputComponent {
  @Output() dateChange: EventEmitter<
    Partial<MatDatepickerInputEvent<Moment | null, any>>
  > = new EventEmitter();

  @Input() matDatepicker!: MatDatepickerPanel<
    MatDatepickerControl<any>,
    any | null,
    any
  >;
  @Input() formControl: FormControl = new FormControl();
}

@Component({
  selector: 'mat-chip-grid',
  template: '<ng-content></ng-content>',
})
class MockMatChipGridComponent {
  @Input() disabled = false;
}

describe('TableFilterComponent', () => {
  let component: TableFilterComponent<unknown>;
  let fixture: ComponentFixture<TableFilterComponent<unknown>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot(), TableFilterComponent],
      providers: [
        TranslatePipe,
        provideNativeDateAdapter(),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
      .overrideComponent(TableFilterComponent, {
        remove: {
          imports: [
            MatDatepickerInput,
            MatChipInput,
            MatChipGrid,
            SelectComponent,
          ],
        },
        add: {
          imports: [
            MockMatDatepickerInputComponent,
            MockMatChipInputComponent,
            MockMatChipGridComponent,
            MockAtlasSelectComponent,
          ],
        },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emitSearch on multi select change', () => {
    const multiSelectFilter = new TableFilterMultiSelect(
      '',
      '',
      ['one', 'two'],
      1,
      'col-3'
    );
    component.filterConfigurations = [[multiSelectFilter]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const mockAtlasSelectComponent: MockAtlasSelectComponent =
      fixture.debugElement.query(
        By.directive(MockAtlasSelectComponent)
      ).componentInstance;
    mockAtlasSelectComponent.selectChanged.emit({ value: ['one'] });

    expect(multiSelectFilter.getActiveSearch()).toEqual(['one']);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();

    mockAtlasSelectComponent.selectChanged.emit({ value: [] });

    expect(multiSelectFilter.getActiveSearch()).toEqual([]);
    expect(component.searchEvent.emit).toHaveBeenCalledTimes(2);
  });

  it('should emitSearch on valid Date', () => {
    const dateSelect = new TableFilterDateSelect(1, 'col-3');
    component.filterConfigurations = [[dateSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const mockMatDatepickerInputComponent: MockMatDatepickerInputComponent =
      fixture.debugElement.query(
        By.directive(MockMatDatepickerInputComponent)
      ).componentInstance;

    const momentInputValue: Moment = moment('31.12.2021', 'DD.MM.yyyy');
    dateSelect.formControl.setValue(momentInputValue.toDate());

    mockMatDatepickerInputComponent.dateChange.emit({
      value: momentInputValue,
    });

    expect(dateSelect.getActiveSearch()).toEqual(
      moment('31.12.2021', 'DD.MM.yyyy').toDate()
    );
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });

  it('should not set date when invalid', () => {
    const dateSelect = new TableFilterDateSelect(1, 'col-3');
    component.filterConfigurations = [[dateSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const mockMatDatepickerInputComponent: MockMatDatepickerInputComponent =
      fixture.debugElement.query(
        By.directive(MockMatDatepickerInputComponent)
      ).componentInstance;

    const momentInputValue: Moment = moment('31.12.1600', 'DD.MM.yyyy');
    dateSelect.formControl.setErrors({ min: 'Date too early!' });

    mockMatDatepickerInputComponent.dateChange.emit({
      value: momentInputValue,
    });

    expect(dateSelect.getActiveSearch()).toEqual(undefined);
    expect(component.searchEvent.emit).not.toHaveBeenCalled();
  });

  it('should add Search', () => {
    const chipSelect = new TableFilterChip(0, 'col-6');
    component.filterConfigurations = [[chipSelect]];
    fixture.detectChanges();

    const mockMatChipInputComponent: MockMatChipInputComponent =
      fixture.debugElement.query(
        By.directive(MockMatChipInputComponent)
      ).componentInstance;

    spyOn(component.searchEvent, 'emit');
    const chipInputClearSpy = jasmine.createSpy();
    const matChipInputSpy = jasmine.createSpyObj('MatChipInputEvent', [], {
      value: 'Test',
      chipInput: {
        clear: chipInputClearSpy,
      },
    });
    mockMatChipInputComponent.matChipInputTokenEnd.emit(matChipInputSpy);

    expect(chipSelect.getActiveSearch()).toEqual(['Test']);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
    expect(chipInputClearSpy).toHaveBeenCalledOnceWith();
  });

  it("should not add search if it's already there", () => {
    const chipSelect = new TableFilterChip(0, 'col-6');
    chipSelect.addSearchFromString('Test');
    component.filterConfigurations = [[chipSelect]];
    fixture.detectChanges();

    const mockMatChipInputComponent: MockMatChipInputComponent =
      fixture.debugElement.query(
        By.directive(MockMatChipInputComponent)
      ).componentInstance;

    spyOn(component.searchEvent, 'emit');
    const chipInputClearSpy = jasmine.createSpy();
    const matChipInputSpy = jasmine.createSpyObj('MatChipInputEvent', [], {
      value: 'Test',
      chipInput: {
        clear: chipInputClearSpy,
      },
    });
    mockMatChipInputComponent.matChipInputTokenEnd.emit(matChipInputSpy);

    expect(chipSelect.getActiveSearch()).toEqual(['Test']);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
    expect(chipInputClearSpy).toHaveBeenCalledOnceWith();
  });

  it('should remove search', () => {
    const chipSelect = new TableFilterChip(0, 'col-6');
    chipSelect.addSearchFromString('Test');
    component.filterConfigurations = [[chipSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const matChipRow: MatChipRow = fixture.debugElement.query(
      By.directive(MatChipRow)
    ).componentInstance;
    matChipRow.removed.emit();

    expect(chipSelect.getActiveSearch()).toEqual([]);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });

  it('should set active search on bo-select change', () => {
    const searchSelect = new TableFilterSearchSelect(
      TableFilterSearchType.BUSINESS_ORGANISATION,
      0,
      'col-3',
      new FormGroup({
        businessOrganisation: new FormControl(),
      })
    );
    component.filterConfigurations = [[searchSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const boSelectComponent: BusinessOrganisationSelectComponent =
      fixture.debugElement.query(
        By.directive(BusinessOrganisationSelectComponent)
      ).componentInstance;
    boSelectComponent.boSelectionChanged.emit({
      sboid: 'Test',
    } as BusinessOrganisation);

    expect(searchSelect.getActiveSearch()).toEqual({ sboid: 'Test' });
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });

  it('should set active search on ttfn-select change', () => {
    const searchSelect = new TableFilterSearchSelect(
      TableFilterSearchType.TIMETABLE_FIELD_NUMBER,
      0,
      'col-3',
      new FormGroup({
        ttfnid: new FormControl(),
      })
    );
    component.filterConfigurations = [[searchSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const ttfnSelectComponent: TimetableFieldNumberSelectComponent =
      fixture.debugElement.query(
        By.directive(TimetableFieldNumberSelectComponent)
      ).componentInstance;
    ttfnSelectComponent.ttfnSelectionChanged.emit({
      ttfnid: 'Test',
    } as TimetableFieldNumber);

    expect(searchSelect.getActiveSearch()).toEqual({ ttfnid: 'Test' });
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });

  it('should set active search on tu-select change', () => {
    const searchSelect = new TableFilterSearchSelect(
      TableFilterSearchType.TRANSPORT_COMPANY,
      0,
      'col-3',
      new FormGroup({
        transportCompany: new FormControl(),
      })
    );
    component.filterConfigurations = [[searchSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const tuSelectComponent: TransportCompanySelectComponent =
      fixture.debugElement.query(
        By.directive(TransportCompanySelectComponent)
      ).componentInstance;
    tuSelectComponent.tuSelectionChanged.emit({ number: 'Test' });

    expect(searchSelect.getActiveSearch()).toEqual({ number: 'Test' });
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });

  it('should set single search', () => {
    const singleSearch = new TableFilterSingleSearch(
      1,
      'SEPODI.GEOLOCATION.DISTRICT',
      'col-3'
    );
    component.filterConfigurations = [[singleSearch]];
    fixture.detectChanges();

    const mockMatChipInputComponent: MockMatChipInputComponent =
      fixture.debugElement.query(
        By.directive(MockMatChipInputComponent)
      ).componentInstance;

    spyOn(component.searchEvent, 'emit');
    const chipInputClearSpy = jasmine.createSpy();
    const matChipInputSpy = jasmine.createSpyObj('MatChipInputEvent', [], {
      value: 'Test',
      chipInput: {
        clear: chipInputClearSpy,
      },
    });
    mockMatChipInputComponent.matChipInputTokenEnd.emit(matChipInputSpy);

    expect(singleSearch.getActiveSearch()).toEqual('Test');
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
    expect(chipInputClearSpy).toHaveBeenCalledOnceWith();
  });

  it('should set active search on boolean slide toggle change', () => {
    const booleanFilter = new TableFilterBoolean(
      0,
      'col-6 container-right-position',
      'SEPODI.SERVICE_POINTS.WORKFLOW.SLIDE'
    );
    component.filterConfigurations = [[booleanFilter]];
    fixture.detectChanges();

    expect(booleanFilter.getActiveSearch()).toBeFalse();

    spyOn(component.searchEvent, 'emit');

    const slideToggleComponent: AtlasSlideToggleComponent =
      fixture.debugElement.query(
        By.directive(AtlasSlideToggleComponent)
      ).componentInstance;

    slideToggleComponent.toggleChange.emit(true);
    fixture.detectChanges();

    expect(booleanFilter.getActiveSearch()).toBeTrue();
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();

    slideToggleComponent.toggleChange.emit(false);
    fixture.detectChanges();

    expect(booleanFilter.getActiveSearch()).toBeFalse();
    expect(component.searchEvent.emit).toHaveBeenCalledTimes(2);
  });
});
