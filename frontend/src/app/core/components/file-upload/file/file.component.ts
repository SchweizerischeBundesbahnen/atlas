import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'atlas-file',
  templateUrl: './file.component.html',
  styleUrls: ['./file.component.scss'],
})
export class FileComponent {
  @Input() file!: File | { name: string; size: number };
  @Input() deleteEnabled = false;
  @Input() downloadEnabled = false;

  @Output() fileDeleted = new EventEmitter<File | { name: string; size: number }>();
  @Output() downloadFile = new EventEmitter<File | { name: string; size: number }>();

  onDelete() {
    this.fileDeleted.emit(this.file);
  }

  download() {
    this.downloadFile.emit(this.file);
  }
}
