import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FileComponent } from './file.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { FileSizePipe } from '../file-size/file-size.pipe';
import { By } from '@angular/platform-browser';

describe('FileComponent', () => {
  let component: FileComponent;
  let fixture: ComponentFixture<FileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FileComponent, FileSizePipe],
      imports: [AppTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileComponent);
    component = fixture.componentInstance;
    component.file = {
      name: 'filename.pdf',
      size: 10,
      type: 'application/pdf',
    } as File;
    component.downloadEnabled = true;
    component.deleteEnabled = true;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should trigger delete', () => {
    const trashIcon = fixture.debugElement.query(By.css('.bi-trash'));
    expect(trashIcon).toBeTruthy();

    spyOn(component.fileDeleted, 'emit');

    trashIcon.nativeElement.click();
    expect(component.fileDeleted.emit).toHaveBeenCalled();
  });

  it('should trigger download', () => {
    const downloadIcon = fixture.debugElement.query(By.css('.bi-download'));
    expect(downloadIcon).toBeTruthy();

    spyOn(component.downloadFile, 'emit');

    downloadIcon.nativeElement.click();
    expect(component.downloadFile.emit).toHaveBeenCalled();
  });
});
