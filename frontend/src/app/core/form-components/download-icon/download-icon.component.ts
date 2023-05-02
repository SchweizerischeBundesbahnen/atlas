import { Component, Input } from '@angular/core';

@Component({
  selector: 'download-icon',
  templateUrl: './download-icon.component.html',
})
export class DownloadIconComponent {
  @Input() readonly!: boolean;

  get fill(): string {
    return this.readonly ? '#2B2B2B' : '#adb5bd';
  }
}
