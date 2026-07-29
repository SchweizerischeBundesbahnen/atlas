import { Component, computed, input } from '@angular/core';
import { NgClass, NgOptimizedImage } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasFieldErrorComponent, AtlasLabelFieldComponent } from '@atlas/form';
import { Field } from '@angular/forms/signals';
import { MeanOfTransport } from '../../../api';
import { GetIconPipe } from '../means-of-transport-picker/get-icon.pipe';

@Component({
  selector: 'atlas-means-of-transport-picker',
  templateUrl: './atlas-means-of-transport-picker.component.html',
  styleUrls: ['../means-of-transport-picker/means-of-transport-picker.component.scss'],
  imports: [AtlasLabelFieldComponent, NgClass, AtlasFieldErrorComponent, TranslatePipe, NgOptimizedImage, GetIconPipe],
})
export class AtlasMeansOfTransportPickerComponent {
  readonly field = input.required<Field<MeanOfTransport[]>>();
  readonly required = input(true);
  readonly meansOfTransportToShow = input<MeanOfTransport[]>();
  readonly multiSelectMode = input(true);

  protected readonly means = computed(() => this.meansOfTransportToShow() ?? Object.values(MeanOfTransport));
  protected readonly selectedMeans = computed(() => this.field()().value() ?? []);

  protected onSelection(meanOfTransport: MeanOfTransport) {
    if (this.field()().disabled()) {
      return;
    }

    const current = this.selectedMeans();
    const next = this.multiSelectMode()
      ? this.toggleMultiSelect(current, meanOfTransport)
      : this.toggleSingleSelect(current, meanOfTransport);

    this.field()().value.set(next);
    this.field()().markAsDirty();
    this.field()().markAsTouched();
  }

  private toggleMultiSelect(current: MeanOfTransport[], meanOfTransport: MeanOfTransport): MeanOfTransport[] {
    return current.includes(meanOfTransport)
      ? current.filter((mean) => mean !== meanOfTransport)
      : [...current, meanOfTransport];
  }

  private toggleSingleSelect(current: MeanOfTransport[], meanOfTransport: MeanOfTransport): MeanOfTransport[] {
    return current.includes(meanOfTransport) ? [] : [meanOfTransport];
  }
}
