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
import { MockAtlasFieldErrorComponent, MockBoSelectComponent } from '../../../app.testing.mocks';
import { SelectComponent } from '../../form-components/select/select.component';
import { AtlasSpacerComponent } from '../spacer/atlas-spacer.component';
import {
  TableFilterChipClass,
  TableFilterDateSelectClass,
  TableFilterMultiSelectClass,
} from './table-filter-config-class';
import { InstanceOfPipe } from './instance-of.pipe';
import { MatChipInputEvent } from '@angular/material/chips';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'input',
  template: '',
})
class MockMatChipInputComponent {
  @Input() matChipInputFor: any;
  @Output() matChipInputTokenEnd: EventEmitter<MatChipInputEvent> =
    new EventEmitter<MatChipInputEvent>();
}

fdescribe('TableFilterComponent', () => {
  let component: TableFilterComponent<unknown>;
  let fixture: ComponentFixture<TableFilterComponent<unknown>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        TableFilterComponent,
        DateIconComponent,
        SelectComponent,
        AtlasSpacerComponent,
        MockBoSelectComponent,
        MockAtlasFieldErrorComponent,
        MockMatChipInputComponent,
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
    const multiSelectFilter = new TableFilterMultiSelectClass('', '', ['one', 'two'], 'col-3');
    component.filterConfigurations = [[multiSelectFilter]];
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

    expect(multiSelectFilter.getActiveSearch()).toEqual(['one']);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();

    option.nativeElement.click();
    fixture.detectChanges();
    expect(multiSelectFilter.getActiveSearch()).toEqual([]);
    expect(component.searchEvent.emit).toHaveBeenCalledTimes(2);
  });

  it('should emitSearch on valid Date', () => {
    const dateSelect = new TableFilterDateSelectClass('col-3');
    component.filterConfigurations = [[dateSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const dateInputElement = fixture.debugElement.query(By.css('input'));
    dateSelect.formControl.setValue(moment('31.12.2021', 'DD.MM.yyyy').toDate());
    dateInputElement.nativeElement.dispatchEvent(new Event('change'));
    fixture.detectChanges();

    expect(dateSelect.getActiveSearch()).toEqual(moment('31.12.2021', 'DD.MM.yyyy').toDate());
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });

  it('should not set date when invalid', () => {
    const dateSelect = new TableFilterDateSelectClass('col-3');
    component.filterConfigurations = [[dateSelect]];
    fixture.detectChanges();

    spyOn(component.searchEvent, 'emit');
    const dateInputElement = fixture.debugElement.query(By.css('input'));
    dateSelect.formControl.setValue(moment('31.12.1600', 'DD.MM.yyyy').toDate());
    dateInputElement.nativeElement.dispatchEvent(new Event('change'));
    fixture.detectChanges();

    expect(dateSelect.getActiveSearch()).toEqual(undefined);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });

  fit('should add Search', () => {
    const chipSelect = new TableFilterChipClass('col-6');
    component.filterConfigurations = [[chipSelect]];
    fixture.detectChanges();

    const mockMatChipInputComponent: MockMatChipInputComponent = fixture.debugElement.query(
      By.directive(MockMatChipInputComponent)
    ).componentInstance;

    spyOn(component.searchEvent, 'emit');
    const matChipInputSpy = jasmine.createSpyObj('MatChipInputEvent', [], {
      value: 'Test',
      chipInput: {
        clear: () => undefined,
      },
    });
    mockMatChipInputComponent.matChipInputTokenEnd.emit(matChipInputSpy);
    fixture.detectChanges();

    expect(chipSelect.getActiveSearch()).toEqual(['Test']);
    expect(component.searchEvent.emit).toHaveBeenCalledOnceWith();
  });

  /* it("should not add search if it's already there", () => {
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
  });*/
});
