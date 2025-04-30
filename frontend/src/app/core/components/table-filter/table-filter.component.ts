import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MAX_DATE, MIN_DATE } from '../../date/date.service';
import { Moment } from 'moment/moment';
import { TableFilterChip } from './config/table-filter-chip';
import { TableFilterSearchSelect } from './config/table-filter-search-select';
import { TableFilterMultiSelect } from './config/table-filter-multiselect';
import { TableFilterDateSelect } from './config/table-filter-date-select';
import { TableFilter } from './config/table-filter';
import { TableFilterSingleSearch } from './config/table-filter-single-search';
import { TableFilterBoolean } from './config/table-filter-boolean';
import { NgFor, NgIf, NgClass } from '@angular/common';
import { AtlasLabelFieldComponent } from '../../form-components/atlas-label-field/atlas-label-field.component';
import {
  MatChipGrid,
  MatChipRow,
  MatChipRemove,
  MatChipInput,
} from '@angular/material/chips';
import { AtlasSlideToggleComponent } from '../../form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { BusinessOrganisationSelectComponent } from '../../form-components/bo-select/business-organisation-select.component';
import { ReactiveFormsModule } from '@angular/forms';
import { TimetableFieldNumberSelectComponent } from '../../form-components/ttfn-select/timetable-field-number-select.component';
import { TransportCompanySelectComponent } from '../../form-components/tu-select/transport-company-select.component';
import { SelectComponent } from '../../form-components/select/select.component';
import { AtlasSpacerComponent } from '../spacer/atlas-spacer.component';
import { MatInput } from '@angular/material/input';
import {
  MatDatepickerInput,
  MatDatepickerToggle,
  MatDatepickerToggleIcon,
  MatDatepicker,
} from '@angular/material/datepicker';
import { MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { DateIconComponent } from '../../form-components/date-icon/date-icon.component';
import { AtlasFieldErrorComponent } from '../../form-components/atlas-field-error/atlas-field-error.component';
import { InstanceOfPipe } from './instance-of.pipe';

@Component({
  selector: 'app-table-filter',
  templateUrl: './table-filter.component.html',
  styleUrls: ['./table-filter.component.scss'],
  imports: [
    NgFor,
    NgIf,
    AtlasLabelFieldComponent,
    NgClass,
    MatChipGrid,
    MatChipRow,
    MatChipRemove,
    MatChipInput,
    AtlasSlideToggleComponent,
    BusinessOrganisationSelectComponent,
    ReactiveFormsModule,
    TimetableFieldNumberSelectComponent,
    TransportCompanySelectComponent,
    SelectComponent,
    AtlasSpacerComponent,
    MatInput,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatSuffix,
    MatIcon,
    MatDatepickerToggleIcon,
    DateIconComponent,
    MatDatepicker,
    AtlasFieldErrorComponent,
    InstanceOfPipe,
  ],
})
export class TableFilterComponent<TFilterConfig> {
  @Input() filterConfigurations: TableFilter<TFilterConfig>[][] = [];
  @Output() searchEvent: EventEmitter<void> = new EventEmitter();

  public readonly TableFilterChipClass = TableFilterChip;
  public readonly TableFilterSearchSelectClass = TableFilterSearchSelect;
  public readonly TableFilterMultiSelectClass = TableFilterMultiSelect;
  public readonly TableFilterDateSelectClass = TableFilterDateSelect;
  public readonly TableFilterSingleSearchClass = TableFilterSingleSearch;
  public readonly TableFilterBooleanClass = TableFilterBoolean;

  MIN_DATE = MIN_DATE;
  MAX_DATE = MAX_DATE;

  handleDateChange(
    dateSelect: TableFilterDateSelect,
    value: Moment | null
  ): void {
    if (dateSelect.formControl.invalid) {
      return;
    }
    dateSelect.setActiveSearch(value);
    this.emitSearch();
  }

  emitSearch(): void {
    this.searchEvent.emit();
  }
}
