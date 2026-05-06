import { Component, Input, input, OnChanges, OnInit, output, SimpleChanges } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MeanOfTransport } from '../../../api';
import { AsyncPipe, NgClass, NgOptimizedImage } from '@angular/common';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';
import { required } from '../../util/values';
import { GetIconPipe } from './get-icon.pipe';
import { distinctUntilChanged, of, startWith } from 'rxjs';

@Component({
  selector: 'atlas-means-of-transport-picker',
  templateUrl: './means-of-transport-picker.component.html',
  styleUrls: ['./means-of-transport-picker.component.scss'],
  imports: [
    ReactiveFormsModule,
    AtlasLabelFieldComponent,
    NgClass,
    AtlasFieldErrorComponent,
    TranslatePipe,
    NgOptimizedImage,
    GetIconPipe,
    AsyncPipe,
  ],
  providers: [TranslatePipe],
})
export class MeansOfTransportPickerComponent implements OnInit, OnChanges {
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() controlName!: string;
  readonly disabled = input(false);
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() formGroup!: FormGroup;
  readonly showInfo = input(false);
  readonly meansOfTransportToShow = input<MeanOfTransport[]>();
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() showSectorWarning = false;
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() multiSelectMode = true;
  readonly selectChange = output<MeanOfTransport[]>();

  protected selectedMeans$ = of([]);
  protected means!: MeanOfTransport[];
  protected sectorWarning = false;

  ngOnInit(): void {
    this.initMeansOfTransportToShow();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.formGroup) {
      this.sectorWarning = false;
      this.selectedMeans$ = this.formControl.valueChanges.pipe(
        startWith(this.formControl.value ?? []),
        distinctUntilChanged()
      );
    }
  }

  private initMeansOfTransportToShow() {
    const meansOfTransportToShow = this.meansOfTransportToShow();
    this.means = meansOfTransportToShow ?? Object.values(MeanOfTransport);
  }

  protected onSelection(meanOfTransport: MeanOfTransport) {
    if (this.multiSelectMode) {
      this.setControlForMultiSelect(meanOfTransport);
    } else {
      this.setControlForSingleSelect(meanOfTransport);
    }
    this.formControl.markAsDirty();
    this.selectChange.emit([...this.formControl.value]);
  }

  private setControlForMultiSelect(meanOfTransport: MeanOfTransport) {
    if (this.currentlySelectedMeans.includes(meanOfTransport)) {
      if (meanOfTransport === MeanOfTransport.Train) {
        this.sectorWarning = true;
      }
      this.formControl.setValue(this.currentlySelectedMeans.filter((i) => i != meanOfTransport));
    } else {
      this.formControl.setValue([...this.currentlySelectedMeans, meanOfTransport]);
    }
  }

  private setControlForSingleSelect(meanOfTransport: MeanOfTransport) {
    if (!this.currentlySelectedMeans.includes(meanOfTransport)) {
      this.formControl.setValue([meanOfTransport]);
    } else {
      this.formControl.setValue([]);
    }
  }

  private get currentlySelectedMeans() {
    if (!this.formControl.value) return [];
    return this.formControl.value as MeanOfTransport[];
  }

  private get formControl() {
    return required(this.formGroup.get(this.controlName), 'mean of transport control must be defined');
  }
}
