import { Component, ContentChild, Input, output, TemplateRef } from '@angular/core';
import { FileSizePipe } from '../file-size/file-size.pipe';
import { NgTemplateOutlet } from '@angular/common';

type FileEvent =
  | File
  | {
      name: string;
      size: number;
    };

@Component({
  selector: 'atlas-file',
  templateUrl: './file.component.html',
  styleUrls: ['./file.component.scss'],
  imports: [FileSizePipe, NgTemplateOutlet],
})
export class FileComponent {
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() file!: File | { name: string; size: number };
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() deleteEnabled = false;
  // TODO: Skipped for migration because:
  //  Your application code writes to the input. This prevents migration.
  @Input() downloadEnabled = false;

  readonly fileDeleted = output<FileEvent>();
  readonly downloadFile = output<FileEvent>();

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @ContentChild('checkBox') checkBox!: TemplateRef<any>;

  onDelete() {
    this.fileDeleted.emit(this.file);
  }

  download() {
    this.downloadFile.emit(this.file);
  }
}
