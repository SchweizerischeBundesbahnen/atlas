import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FileUploadComponent } from './file-upload.component';
import { AppTestingModule } from '../../../app.testing.module';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';

function getMockFileList(
  fileName: string,
  properties: FilePropertyBag,
  fileBits = '',
  filecount = 1
) {
  const dt = new DataTransfer();
  for (let i = 0; i < filecount; i++) {
    dt.items.add(new File([fileBits], fileName, properties));
  }
  return dt.files;
}

describe('FileUploadComponent', () => {
  let component: FileUploadComponent;
  let fixture: ComponentFixture<FileUploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FileUploadComponent, MockAtlasButtonComponent],
      imports: [AppTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileUploadComponent);
    component = fixture.componentInstance;

    component.acceptedFileExtension = '.pdf';
    component.acceptedFileType = 'application/pdf';
    component.maxFileCount = 1;
    component.maxFileSize = 10;
    component.alreadySavedFileNames = ['savedFile.pdf'];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.errorFiles.length).toBe(0);
    expect(component.uploadedFiles.length).toBe(0);
  });

  it('should add file validating type', () => {
    component.addFileListToFile(getMockFileList('test.csv', { type: 'application/csv' }, ''));

    expect(component.uploadedFiles.length).toBe(0);

    expect(component.errorFiles.length).toBe(1);
    expect(component.errorFiles[0].errorMessage).toBe('COMMON.FILEUPLOAD.ERROR.TYPE');
  });

  it('should add file validating size', () => {
    component.addFileListToFile(
      getMockFileList('test.pdf', { type: 'application/pdf' }, 'asdfghjklertzui')
    );

    expect(component.uploadedFiles.length).toBe(0);

    expect(component.errorFiles.length).toBe(1);
    expect(component.errorFiles[0].errorMessage).toBe('COMMON.FILEUPLOAD.ERROR.FILE_SIZE');
  });

  it('should add file validating file count', () => {
    component.addFileListToFile(getMockFileList('test.pdf', { type: 'application/pdf' }, 'as', 2));

    expect(component.uploadedFiles.length).toBe(1);

    expect(component.errorFiles.length).toBe(1);
    expect(component.errorFiles[0].errorMessage).toBe('COMMON.FILEUPLOAD.ERROR.FILE_COUNT');
  });

  it('should add file validating file duplication upload', () => {
    component.maxFileCount = 2;
    component.addFileListToFile(getMockFileList('test.pdf', { type: 'application/pdf' }, 'as', 1));
    component.addFileListToFile(getMockFileList('test.pdf', { type: 'application/pdf' }, 'as', 1));

    expect(component.uploadedFiles.length).toBe(1);

    expect(component.errorFiles.length).toBe(1);
    expect(component.errorFiles[0].errorMessage).toBe('COMMON.FILEUPLOAD.ERROR.ALREADY_ADDED');
  });

  it('should add file validating file duplication already saved', () => {
    component.addFileListToFile(
      getMockFileList('savedFile.pdf', { type: 'application/pdf' }, 'as', 1)
    );

    expect(component.uploadedFiles.length).toBe(0);

    expect(component.errorFiles.length).toBe(1);
    expect(component.errorFiles[0].errorMessage).toBe('COMMON.FILEUPLOAD.ERROR.ALREADY_SAVED');
  });

  it('should add file successfully and delete it', () => {
    spyOn(component.uploadedFilesChange, 'emit');

    component.addFileListToFile(getMockFileList('test.pdf', { type: 'application/pdf' }, 'as', 1));
    expect(component.uploadedFiles.length).toBe(1);
    expect(component.errorFiles.length).toBe(0);
    expect(component.uploadedFilesChange.emit).toHaveBeenCalled();

    component.fileDeleted({ name: 'test.pdf' });
    expect(component.uploadedFiles.length).toBe(0);
    expect(component.uploadedFilesChange.emit).toHaveBeenCalled();
  });
});
