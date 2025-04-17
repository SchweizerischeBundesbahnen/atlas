import { Component, Input } from '@angular/core';

@Component({
  selector: 'download-icon',
  templateUrl: './download-icon.component.html',
})
export class DownloadIconComponent {
  @Input() readonly!: boolean;
  @Input() disabled!: boolean;

  get fill(): string {
    if (this.disabled) {
      return '#d3d3d3';
    }
    return this.readonly ? '#2B2B2B' : '#adb5bd';
  }
}
