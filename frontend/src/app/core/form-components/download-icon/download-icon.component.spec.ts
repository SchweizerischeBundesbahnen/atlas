import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { DownloadIconComponent } from './download-icon.component';
import { inputBinding, signal } from '@angular/core';

describe('DownloadIconComponent', () => {
  let component: DownloadIconComponent;
  let fixture: ComponentFixture<DownloadIconComponent>;

  let disabledInput: ReturnType<typeof signal<boolean>>;
  let readonlyInput: ReturnType<typeof signal<boolean>>;

  beforeEach(() => {
    disabledInput = signal(false);
    readonlyInput = signal(false);

    const disabledInputName: keyof DownloadIconComponent = 'disabled';
    const readonlyInputName: keyof DownloadIconComponent = 'readonly';
    fixture = TestBed.createComponent(DownloadIconComponent, {
      bindings: [
        inputBinding(disabledInputName, () => disabledInput()),
        inputBinding(readonlyInputName, () => readonlyInput()),
      ],
    });
    component = fixture.componentInstance;
  });

  it('should return #d3d3d3 when disabled is true', () => {
    disabledInput.set(true);
    fixture.detectChanges();
    expect(component.fill).toBe('#d3d3d3');
  });

  it('should return #2B2B2B when readonly is true and disabled is false', () => {
    disabledInput.set(false);
    readonlyInput.set(true);
    fixture.detectChanges();
    expect(component.fill).toBe('#2B2B2B');
  });

  it('should return #adb5bd when readonly and disabled are both false', () => {
    disabledInput.set(false);
    readonlyInput.set(false);
    fixture.detectChanges();
    expect(component.fill).toBe('#adb5bd');
  });
});
