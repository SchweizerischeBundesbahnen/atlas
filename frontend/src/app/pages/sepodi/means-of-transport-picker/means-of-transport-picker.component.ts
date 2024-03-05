import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {MeanOfTransport} from '../../../api';

@Component({
  selector: 'means-of-transport-picker',
  templateUrl: './means-of-transport-picker.component.html',
  styleUrls: ['./means-of-transport-picker.component.scss'],
})
export class MeansOfTransportPickerComponent implements OnInit {
  @Input() controlName!: string;
  @Input() disabled = false;
  @Input() formGroup!: FormGroup;
  @Input() label!: string;
  @Input() showUnknown = true;
  @Input() showInfo = false;
  @Input() meansOfTransportToShows: MeanOfTransport[] | undefined;

  means!: MeanOfTransport[];

  ngOnInit(): void {
    this.getMeansOfTransportToShow();
  }

  private getMeansOfTransportToShow() {
    let meansToShow = Object.values(MeanOfTransport)
    if (this.meansOfTransportToShows) {
      meansToShow = this.meansOfTransportToShows;
    }
    this.means = this.showUnknown
      ? meansToShow
      : meansToShow.filter((value) => value !== MeanOfTransport.Unknown);
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
      this.formControl.setValue(this.currentlySelectedMeans.filter((i) => i != meanOfTransport));
    } else {
      this.currentlySelectedMeans.push(meanOfTransport);
      this.formControl.setValue(this.currentlySelectedMeans);
    }
    this.formControl.markAsDirty();
  }

  getIcon(mean: MeanOfTransport) {
    if (this.currentlySelectedMeans && this.currentlySelectedMeans.includes(mean)) {
      return mean;
    } else return mean + '_GRAY';
  }
}
