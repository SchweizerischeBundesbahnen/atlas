import { Component, Input } from '@angular/core';

@Component({
  selector: 'atlas-download-icon',
  templateUrl: './download-icon.component.html',
})
export class DownloadIconComponent {
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() readonly!: boolean;
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() disabled!: boolean;

  get fill(): string {
    if (this.disabled) {
      return '#d3d3d3';
    }
    return this.readonly ? '#2B2B2B' : '#adb5bd';
  }
}
