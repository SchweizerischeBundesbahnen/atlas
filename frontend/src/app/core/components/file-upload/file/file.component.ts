import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'atlas-file',
  templateUrl: './file.component.html',
  styleUrls: ['./file.component.scss'],
})
export class FileComponent {
  @Input() file!: File;
  @Output() fileDeleted = new EventEmitter<File>();

  onDelete() {
    this.fileDeleted.emit(this.file);
  }
}
