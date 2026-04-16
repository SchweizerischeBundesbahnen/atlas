import { Component, ContentChild, EventEmitter, Input, Output, TemplateRef } from '@angular/core';
import { FileSizePipe } from '../file-size/file-size.pipe';
import { NgTemplateOutlet } from '@angular/common';

@Component({
  selector: 'atlas-file',
  templateUrl: './file.component.html',
  styleUrls: ['./file.component.scss'],
  imports: [FileSizePipe, NgTemplateOutlet],
})
export class FileComponent {
  @Input() file!: File | { name: string; size: number };
  @Input() deleteEnabled = false;
  @Input() downloadEnabled = false;

  @Output() fileDeleted = new EventEmitter<File | { name: string; size: number }>();
  @Output() downloadFile = new EventEmitter<File | { name: string; size: number }>();

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @ContentChild('checkBox') checkBox!: TemplateRef<any>;

  onDelete() {
    this.fileDeleted.emit(this.file);
  }

  download() {
    this.downloadFile.emit(this.file);
  }
}
