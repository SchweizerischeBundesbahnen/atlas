import {
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MeanOfTransport } from '../../../api';
import { NgClass, NgFor, NgIf, NgOptimizedImage } from '@angular/common';
import { AtlasLabelFieldComponent } from '@atlas/form/atlas-label-field/atlas-label-field.component';
import { AtlasSpacerComponent } from '../../components/spacer/atlas-spacer.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'means-of-transport-picker',
  templateUrl: './means-of-transport-picker.component.html',
  styleUrls: ['./means-of-transport-picker.component.scss'],
  imports: [
    ReactiveFormsModule,
    NgIf,
    AtlasLabelFieldComponent,
    AtlasSpacerComponent,
    NgFor,
    NgClass,
    AtlasFieldErrorComponent,
    TranslatePipe,
    NgOptimizedImage,
  ],
  providers: [TranslatePipe],
})
export class MeansOfTransportPickerComponent implements OnInit, OnChanges {
  @Input() controlName!: string;
  @Input() disabled = false;
  @Input() formGroup!: FormGroup;
  @Input() label!: string; // todo: is not even used as string value
  @Input() showInfo = false;
  @Input() meansOfTransportToShow: MeanOfTransport[] | undefined;
  @Input() showSectorWarning = false;

  means!: MeanOfTransport[];
  sectorWarning = false;

  ngOnInit(): void {
    this.getMeansOfTransportToShow();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.formGroup) {
      this.sectorWarning = false;
    }
  }

  private getMeansOfTransportToShow() {
    this.means = this.meansOfTransportToShow
      ? this.meansOfTransportToShow
      : Object.values(MeanOfTransport);
  }

  get currentlySelectedMeans() {
    return this.formControl.value as MeanOfTransport[];
  }

  get formControl() {
    return this.formGroup.get(this.controlName)!;
  }

  clicked(meanOfTransport: MeanOfTransport) {
    if (this.disabled) {
      return;
    }
    if (this.currentlySelectedMeans.includes(meanOfTransport)) {
      if (meanOfTransport === MeanOfTransport.Train) {
        this.sectorWarning = true;
      }
      this.formControl.setValue(
        this.currentlySelectedMeans.filter((i) => i != meanOfTransport)
      );
    } else {
      this.currentlySelectedMeans.push(meanOfTransport);
      this.formControl.setValue(this.currentlySelectedMeans);
    }
    this.formControl.markAsDirty();
  }

  getIcon(mean: MeanOfTransport) {
    if (
      this.currentlySelectedMeans &&
      this.currentlySelectedMeans.includes(mean)
    ) {
      return mean;
    } else return mean + '_GRAY';
  }
}
