import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TableFilterComponent } from './table-filter.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { By } from '@angular/platform-browser';
import moment from 'moment';
import { DateIconComponent } from '../../form-components/date-icon/date-icon.component';
import { MockAtlasFieldErrorComponent } from '../../../app.testing.mocks';
import { AtlasSpacerComponent } from '../spacer/atlas-spacer.component';
import { InstanceOfPipe } from './instance-of.pipe';
import { MatChipInputEvent } from '@angular/material/chips';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  MatDatepickerControl,
  MatDatepickerInputEvent,
  MatDatepickerPanel,
} from '@angular/material/datepicker';
import { Moment } from 'moment/moment';
import { BusinessOrganisation, TimetableFieldNumber, TransportCompany } from '../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { TableFilterMultiSelect } from './config/table-filter-multiselect';
import { TableFilterDateSelect } from './config/table-filter-date-select';
import { TableFilterChip } from './config/table-filter-chip';
import { TableFilterSearchSelect } from './config/table-filter-search-select';
import { TableFilterSearchType } from './config/table-filter-search-type';
import { AtlasLabelFieldComponent } from '../../form-components/atlas-label-field/atlas-label-field.component';

/* eslint-disable  @typescript-eslint/no-explicit-any */

@Component({
  selector: 'input [matChipInputFor]',
  template: '',
})
class MockMatChipInputComponent {
  @Input() matChipInputFor: any;
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
  @Output() dateChange: EventEmitter<Partial<MatDatepickerInputEvent<Moment | null, any>>> =
    new EventEmitter();

  @Input() matDatepicker!: MatDatepickerPanel<MatDatepickerControl<any>, any | null, any>;
  @Input() formControl: FormControl = new FormControl();
}

@Component({
  selector: 'mat-datepicker',
  template: '',
})
class MockMatDatepickerComponent {}

@Component({
  selector: 'mat-icon',
  template: '',
})
class MockMatIconComponent {}

@Component({
  selector: 'mat-chip-row',
  template: '',
})
class MockMatChipRowComponent {
  @Output() removed: EventEmitter<void> = new EventEmitter<void>();
}

@Component({
  selector: 'bo-select',
  template: '',
})
class MockBoSelectComponent {
  @Output() boSelectionChanged = new EventEmitter<BusinessOrganisation>();

  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
}

@Component({
  selector: 'ttfn-select',
  template: '',
})
class MockTtfnSelectComponent {
  @Output() ttfnSelectionChanged = new EventEmitter<TimetableFieldNumber>();

  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Input() disabled!: boolean;
}

@Component({
  selector: 'tu-select',
  template: '',
})
class MockTuSelectComponent {
  @Output() tuSelectionChanged = new EventEmitter<TransportCompany>();

  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Input() disabled!: boolean;
}

@Component({
  selector: 'mat-chip-grid',
  template: '<ng-content></ng-content>',
})
class MockMatChipGridComponent {
  @Input() disabled = false;
}

@Component({
  selector: 'mat-datepicker-toggle',
  template: '',
})
class MockMatDatepickerToggleComponent<D> {
  @Input() for!: MatDatepickerPanel<MatDatepickerControl<any>, D>;
}

describe('TableFilterComponent', () => {
  let component: TableFilterComponent<unknown>;
  let fixture: ComponentFixture<TableFilterComponent<unknown>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        // Tested component
        TableFilterComponent,
        // Mocks
        MockBoSelectComponent,
        MockAtlasFieldErrorComponent,
        MockMatChipInputComponent,
        MockAtlasSelectComponent,
        MockMatDatepickerInputComponent,
        MockMatChipRowComponent,
        MockTtfnSelectComponent,
        MockTuSelectComponent,
        MockMatChipGridComponent,
        MockMatDatepickerToggleComponent,
        MockMatDatepickerComponent,
        MockMatIconComponent,
        // Real dependents
        DateIconComponent,
        AtlasSpacerComponent,
        AtlasLabelFieldComponent,
        InstanceOfPipe,
      ],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [TranslatePipe],
    }).compileComponents();
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
    const multiSelectFilter = new TableFilterMultiSelect('', '', ['one', 'two'], 1, 'col-3');
    component.filterConfigurations = [[multiSelectFilter]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const mockAtlasSelectComponent: MockAtlasSelectComponent = fixture.debugElement.query(
      By.directive(MockAtlasSelectComponent),
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
      fixture.debugElement.query(By.directive(MockMatDatepickerInputComponent)).componentInstance;

    const momentInputValue: Moment = moment('31.12.2021', 'DD.MM.yyyy');
    dateSelect.formControl.setValue(momentInputValue.toDate());

    mockMatDatepickerInputComponent.dateChange.emit({
      value: momentInputValue,
    });

    expect(dateSelect.getActiveSearch()).toEqual(moment('31.12.2021', 'DD.MM.yyyy').toDate());
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });

  it('should not set date when invalid', () => {
    const dateSelect = new TableFilterDateSelect(1, 'col-3');
    component.filterConfigurations = [[dateSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const mockMatDatepickerInputComponent: MockMatDatepickerInputComponent =
      fixture.debugElement.query(By.directive(MockMatDatepickerInputComponent)).componentInstance;

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

    const mockMatChipInputComponent: MockMatChipInputComponent = fixture.debugElement.query(
      By.directive(MockMatChipInputComponent),
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

    const mockMatChipInputComponent: MockMatChipInputComponent = fixture.debugElement.query(
      By.directive(MockMatChipInputComponent),
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
    const mockMatChipRowComponent: MockMatChipRowComponent = fixture.debugElement.query(
      By.directive(MockMatChipRowComponent),
    ).componentInstance;
    mockMatChipRowComponent.removed.emit();

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
      }),
    );
    component.filterConfigurations = [[searchSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const mockBoSelectComponent: MockBoSelectComponent = fixture.debugElement.query(
      By.directive(MockBoSelectComponent),
    ).componentInstance;
    mockBoSelectComponent.boSelectionChanged.emit({ sboid: 'Test' } as BusinessOrganisation);

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
      }),
    );
    component.filterConfigurations = [[searchSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const mockTtfnSelectComponent: MockTtfnSelectComponent = fixture.debugElement.query(
      By.directive(MockTtfnSelectComponent),
    ).componentInstance;
    mockTtfnSelectComponent.ttfnSelectionChanged.emit({ ttfnid: 'Test' } as TimetableFieldNumber);

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
      }),
    );
    component.filterConfigurations = [[searchSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const mockTuSelectComponent: MockTuSelectComponent = fixture.debugElement.query(
      By.directive(MockTuSelectComponent),
    ).componentInstance;
    mockTuSelectComponent.tuSelectionChanged.emit({ number: 'Test' });

    expect(searchSelect.getActiveSearch()).toEqual({ number: 'Test' });
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });
});
