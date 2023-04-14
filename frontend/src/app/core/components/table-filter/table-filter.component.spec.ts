import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TableFilterComponent } from './table-filter.component';
import { TranslatePipe } from '@ngx-translate/core';
import { By } from '@angular/platform-browser';
import moment from 'moment';
import { AppTestingModule } from '../../../app.testing.module';
import { DateIconComponent } from '../../form-components/date-icon/date-icon.component';
import { MockAtlasFieldErrorComponent, MockBoSelectComponent } from '../../../app.testing.mocks';
import {
  FilterType,
  TableFilterChip,
  TableFilterDateSelect,
  TableFilterMultiSelect,
} from './table-filter-config';
import { FormControl } from '@angular/forms';
import { FilterTypeGuardPipe } from './filter-type-guard.pipe';
import { SelectComponent } from '../../form-components/select/select.component';
import { AtlasSpacerComponent } from '../spacer/atlas-spacer.component';

describe('TableFilterComponent', () => {
  let component: TableFilterComponent<unknown>;
  let fixture: ComponentFixture<TableFilterComponent<unknown>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        TableFilterComponent,
        DateIconComponent,
        MockBoSelectComponent,
        SelectComponent,
        AtlasSpacerComponent,
        FilterTypeGuardPipe,
        MockAtlasFieldErrorComponent,
      ],
      imports: [AppTestingModule],
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
    component.filterConfigurations = [
      [
        {
          filterType: FilterType.MULTI_SELECT,
          elementWidthCssClass: 'col-3',
          selectOptions: ['one', 'two'],
          activeSearch: [],
          labelTranslationKey: '',
          typeTranslationKeyPrefix: '',
        },
      ],
    ] as [[TableFilterMultiSelect<string>]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const selectTrigger = fixture.debugElement.query(
      By.css('mat-select[data-cy="table-filter-multi-select-0-0"] div')
    );
    selectTrigger.nativeElement.click();
    fixture.detectChanges();
    const option = fixture.debugElement.query(By.css('mat-option'));
    option.nativeElement.click();
    fixture.detectChanges();
    expect(component.filterConfigurations[0][0].activeSearch as string[]).toEqual(['one']);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();

    option.nativeElement.click();
    fixture.detectChanges();
    expect(component.filterConfigurations[0][0].activeSearch as string[]).toEqual([]);
    expect(component.searchEvent.emit).toHaveBeenCalledTimes(2);
  });

  it('should emitSearch on valid Date', () => {
    component.filterConfigurations = [
      [
        {
          filterType: FilterType.VALID_ON_SELECT,
          elementWidthCssClass: 'col-3',
          activeSearch: undefined,
          formControl: new FormControl(),
        },
      ],
    ] as [[TableFilterDateSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const dateControl = (component.filterConfigurations[0][0] as TableFilterDateSelect).formControl;
    dateControl.setValue(moment('31.12.2021', 'DD.MM.yyyy').toDate());
    const matDatepickerSpy = jasmine.createSpyObj([], {
      value: moment('31.12.2021', 'DD.MM.yyyy'),
    });
    component.onDateChanged(matDatepickerSpy, 0, 0);
    expect(component.filterConfigurations[0][0].activeSearch).toEqual(
      moment('31.12.2021', 'DD.MM.yyyy').toDate()
    );
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });

  it('should not emitSearch on invalid Date', () => {
    component.filterConfigurations = [
      [
        {
          filterType: FilterType.VALID_ON_SELECT,
          elementWidthCssClass: 'col-3',
          activeSearch: undefined,
          formControl: new FormControl(),
        },
      ],
    ] as [[TableFilterDateSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const dateControl = (component.filterConfigurations[0][0] as TableFilterDateSelect).formControl;
    dateControl.setValue(moment('31.12.1600', 'DD.MM.yyyy').toDate());
    const matDatepickerSpy = jasmine.createSpyObj([], {
      value: moment('31.12.1600', 'DD.MM.yyyy'),
    });
    component.onDateChanged(matDatepickerSpy, 0, 0);
    expect(component.filterConfigurations[0][0].activeSearch).toEqual(undefined);
    expect(component.searchEvent.emit).not.toHaveBeenCalled();
  });

  it('should add Search', () => {
    component.filterConfigurations = [
      [
        {
          filterType: FilterType.CHIP_SEARCH,
          elementWidthCssClass: 'col-6',
          activeSearch: [],
        },
      ],
    ] as [[TableFilterChip]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const matChipInputSpy = jasmine.createSpyObj('MatChipInputEvent', [], {
      value: 'Test',
      chipInput: {
        clear: () => undefined,
      },
    });
    component.addSearch(matChipInputSpy, 0, 0);
    expect(component.filterConfigurations[0][0].activeSearch).toEqual(['Test']);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });

  it("should not add search if it's already there", () => {
    component.filterConfigurations = [
      [
        {
          filterType: FilterType.CHIP_SEARCH,
          elementWidthCssClass: 'col-6',
          activeSearch: ['Test'],
        },
      ],
    ] as [[TableFilterChip]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const matChipInputSpy = jasmine.createSpyObj('MatChipInputEvent', [], {
      value: 'Test',
      chipInput: jasmine.createSpyObj(['clear']),
    });
    component.addSearch(matChipInputSpy, 0, 0);
    expect(component.filterConfigurations[0][0].activeSearch).toEqual(['Test']);
    expect(component.searchEvent.emit).not.toHaveBeenCalled();
    expect(matChipInputSpy.chipInput.clear).toHaveBeenCalled();
  });

  it('should remove search', () => {
    component.filterConfigurations = [
      [
        {
          filterType: FilterType.CHIP_SEARCH,
          elementWidthCssClass: 'col-6',
          activeSearch: ['Test', 'Test2'],
        },
      ],
    ] as [[TableFilterChip]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    component.removeSearch('Test', 0, 0);
    expect(component.filterConfigurations[0][0].activeSearch).toEqual(['Test2']);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });
});
