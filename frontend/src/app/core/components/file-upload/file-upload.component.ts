import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { FileError, FileErrorType } from './fileError';

@Component({
  selector: 'atlas-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss'],
})
export class FileUploadComponent {
  @Input() acceptedFileExtension = '.pdf';
  @Input() acceptedFileType = 'application/pdf';
  @Input() maxFileSize = 20_000_000;

  files: File[] = [];

  errorFiles: FileError[] = [];

  @ViewChild('fileInput') fileInputRef!: ElementRef;

  onFileDropped(fileList: FileList) {
    this.addFileListToFile(fileList);
  }

  selectFilesFromSystem() {
    this.fileInputRef.nativeElement.click();
  }

  onFileInputChanged($event: Event) {
    const element = $event.target as HTMLInputElement;
    const fileList = element.files;
    if (fileList) {
      this.addFileListToFile(fileList);
    }
  }

  // TODO: check all file sizes combined
  addFileListToFile(fileList: FileList) {
    for (let i = 0; i < fileList.length; i++) {
      if (fileList.item(i)) {
        const item = fileList.item(i)!;
        if (item.type === this.acceptedFileType) {
          if (item.size < this.maxFileSize) {
            this.files.push(item);
          } else {
            this.errorFiles.push({
              errorType: FileErrorType.SIZE,
              file: item,
            });
          }
        } else {
          this.errorFiles.push({
            errorType: FileErrorType.TYPE,
            file: item,
          });
        }
      }
    }
  }
}
