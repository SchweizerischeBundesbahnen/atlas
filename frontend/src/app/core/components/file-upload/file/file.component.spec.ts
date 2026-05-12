import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { FileComponent } from './file.component';
import { By } from '@angular/platform-browser';
import { inputBinding } from '@angular/core';

describe('FileComponent', () => {
  let component: FileComponent;
  let fixture: ComponentFixture<FileComponent>;

  beforeEach(() => {
    const fileInputName: keyof FileComponent = 'file';
    const downloadEnabledInputName: keyof FileComponent = 'downloadEnabled';
    const deleteEnabledInputName: keyof FileComponent = 'deleteEnabled';
    fixture = TestBed.createComponent(FileComponent, {
      bindings: [
        inputBinding(
          fileInputName,
          () =>
            ({
              name: 'filename.pdf',
              size: 10,
              type: 'application/pdf',
            }) as File
        ),
        inputBinding(downloadEnabledInputName, () => true),
        inputBinding(deleteEnabledInputName, () => true),
      ],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should trigger delete', () => {
    const trashIcon = fixture.debugElement.query(By.css('.bi-trash'));
    expect(trashIcon).toBeTruthy();

    vi.spyOn(component.fileDeleted, 'emit').mockImplementation(() => {});

    trashIcon.nativeElement.click();
    expect(component.fileDeleted.emit).toHaveBeenCalled();
  });

  it('should trigger download', () => {
    const downloadIcon = fixture.debugElement.query(By.css('.bi-download'));
    expect(downloadIcon).toBeTruthy();

    vi.spyOn(component.downloadFile, 'emit').mockImplementation(() => {});

    downloadIcon.nativeElement.click();
    expect(component.downloadFile.emit).toHaveBeenCalled();
  });
});
