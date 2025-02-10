import { Component, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import {
  LineConcessionType,
  LineType,
  MeanOfTransport,
  OfferCategory,
} from '../../../../../api';
import { SelectOptionGroup } from '../../../../../core/form-components/select/select.component';
import { ColumnDropDownEvent } from '../../../../../core/components/table/column-drop-down-event';

interface Category {
  value: OfferCategory;
}

interface OfferCategoryGroup {
  name: MeanOfTransport | 'ALL';
  category: Category[];
}

@Component({
  selector: 'line-detail-form',
  templateUrl: './line-detail-form.component.html',
  styleUrls: ['./line-detail-form.component.scss'],
})
export class LineDetailFormComponent {
  @Input() form!: FormGroup;
  @Input() newRecord = false;
  @Input() boSboidRestriction: string[] = [];
  @Input() isLineConcessionTypeRequired = true;

  TYPE_OPTIONS = Object.values(LineType);
  LINE_CONCESSION_TYPE_OPTIONS = Object.values(LineConcessionType);
  OFFER_CATEGORY_GROUP: OfferCategoryGroup[] = [
    {
      name: MeanOfTransport.Train,
      category: [
        { value: 'IC' },
        { value: 'EC' },
        { value: 'EN' },
        { value: 'IR' },
        { value: 'RE' },
        { value: 'R' },
        { value: 'S' },
        { value: 'SN' },
        { value: 'PE' },
        { value: 'EXT' },
        { value: 'ATZ' },
        { value: 'ICE' },
        { value: 'TGV' },
        { value: 'RJ' },
        { value: 'TE2' },
        { value: 'TER' },
        { value: 'RB' },
        { value: 'IRE' },
      ],
    },
    {
      name: MeanOfTransport.Tram,
      category: [{ value: 'T' }, { value: 'TN' }],
    },
    {
      name: MeanOfTransport.Metro,
      category: [{ value: 'M' }],
    },
    {
      name: MeanOfTransport.Bus,
      category: [
        { value: 'CAX' },
        { value: 'CAR' },
        { value: 'EXB' },
        { value: 'B' },
        { value: 'BN' },
        { value: 'RUB' },
        { value: 'BP' },
      ],
    },
    {
      name: MeanOfTransport.CableRailway,
      category: [{ value: 'FUN' }],
    },
    {
      name: MeanOfTransport.CableCar,
      category: [{ value: 'PB' }, { value: 'GB' }],
    },
    {
      name: MeanOfTransport.Chairlift,
      category: [{ value: 'SL' }],
    },
    {
      name: MeanOfTransport.Elevator,
      category: [{ value: 'ASC' }],
    },
    {
      name: MeanOfTransport.Boat,
      category: [{ value: 'BAT' }, { value: 'FAE' }],
    },
    {
      name: 'ALL',
      category: [{ value: 'EV' }],
    },
  ];

  offerCategoryOptionGroup: SelectOptionGroup = {
    options: this.OFFER_CATEGORY_GROUP,
    groupValueExtractorProperty: 'category',
    valueExtractor: (category: Category) => category.value,
  };

  updateMandatoryFields($event: ColumnDropDownEvent) {
    if ($event.value !== LineType.Orderly) {
      this.form.controls.lineConcessionType.reset();
      this.form.controls.swissLineNumber.reset();
    }
  }
}
