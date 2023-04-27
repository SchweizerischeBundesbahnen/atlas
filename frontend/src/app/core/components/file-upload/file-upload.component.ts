import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { FileUploadError } from './file-upload-error';

@Component({
  selector: 'atlas-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss'],
})
export class FileUploadComponent {
  @Input() acceptedFileExtension = '.pdf';
  @Input() acceptedFileType = 'application/pdf';
  @Input() maxFileSize = 20 * 1024 * 1024;
  @Input() maxFileCount = 3;

  files: File[] = [];

  errorFiles: FileUploadError[] = [];

  @ViewChild('fileInput') fileInputRef!: ElementRef;

  onFilesDropped(fileList: FileList) {
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

  addFileListToFile(fileList: FileList) {
    this.clearErrors();
    for (let i = 0; i < fileList.length; i++) {
      if (fileList.item(i)) {
        const item = fileList.item(i)!;
        if (this.validateFile(item)) {
          this.files.push(item);
        }
      }
    }
  }

  get combinedFileSize() {
    return this.files.map((file) => file.size).reduce((sum, current) => sum + current, 0);
  }

  private validateFile(file: File) {
    if (file.type !== this.acceptedFileType) {
      this.addFileError(file, 'COMMON.FILEUPLOAD.ERROR.TYPE');
      return false;
    }
    if (this.combinedFileSize + file.size > this.maxFileSize) {
      this.addFileError(file, 'COMMON.FILEUPLOAD.ERROR.FILE_SIZE');
      return false;
    }
    if (this.files.length >= this.maxFileCount) {
      if (this.fileCountErrorAlreadyAdded()) {
        return false;
      }
      this.addFileError(file, 'COMMON.FILEUPLOAD.ERROR.FILE_COUNT');
      return false;
    }
    return true;
  }

  private fileCountErrorAlreadyAdded() {
    return (
      this.errorFiles.filter((error) => error.errorMessage === 'COMMON.FILEUPLOAD.ERROR.FILE_COUNT')
        .length > 0
    );
  }

  private addFileError(file: File, errorMessage: string) {
    this.errorFiles.push({
      errorMessage: errorMessage,
      file: file,
      maxFileCount: this.maxFileCount,
    });
  }

  private clearErrors() {
    this.errorFiles = [];
  }

  fileDeleted(file: File) {
    this.files = this.files.filter((item) => item.name !== file.name);
    this.clearErrors();
  }
}
