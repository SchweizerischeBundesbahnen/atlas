import { ChangeDetectionStrategy, Component, ContentChild, input, output, TemplateRef } from '@angular/core';
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
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./file.component.scss'],
  imports: [FileSizePipe, NgTemplateOutlet],
})
export class FileComponent {
  readonly file = input.required<FileEvent>();
  readonly deleteEnabled = input(false);
  readonly downloadEnabled = input(false);

  readonly fileDeleted = output<FileEvent>();
  readonly downloadFile = output<FileEvent>();

  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  @ContentChild('checkBox') checkBox!: TemplateRef<any>;

  onDelete() {
    this.fileDeleted.emit(this.file());
  }

  download() {
    this.downloadFile.emit(this.file());
  }
}
