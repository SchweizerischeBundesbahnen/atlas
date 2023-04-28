import { Directive, EventEmitter, HostBinding, HostListener, Output } from '@angular/core';

@Directive({
  selector: '[atlas-file-drop]',
})
export class FileDropDirective {
  @HostBinding('class.fileover') fileOver!: boolean;
  @Output() filesDropped = new EventEmitter<FileList>();

  @HostListener('dragover', ['$event']) onDragOver(dragEvent: DragEvent) {
    dragEvent.preventDefault();
    dragEvent.stopPropagation();
    this.fileOver = true;
  }

  @HostListener('dragleave', ['$event'])
  public onDragLeave(dragEvent: DragEvent) {
    dragEvent.preventDefault();
    dragEvent.stopPropagation();
    this.fileOver = false;
  }

  @HostListener('drop', ['$event'])
  public onDrop(dragEvent: DragEvent) {
    dragEvent.preventDefault();
    dragEvent.stopPropagation();
    this.fileOver = false;
    const files = dragEvent.dataTransfer!.files;
    if (files.length > 0) {
      this.filesDropped.emit(files);
    }
  }
}
