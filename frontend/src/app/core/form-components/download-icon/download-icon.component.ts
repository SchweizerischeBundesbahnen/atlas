import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'atlas-download-icon',
  templateUrl: './download-icon.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
})
export class DownloadIconComponent {
  readonly readonly = input(false);
  readonly disabled = input(false);

  get fill(): string {
    if (this.disabled()) {
      return '#d3d3d3';
    }
    return this.readonly() ? '#2B2B2B' : '#adb5bd';
  }
}
