import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { Component } from '@angular/core';
import { By } from '@angular/platform-browser';
import { FileDropDirective } from './file-drop.directive';
import { mock } from 'vitest-mock-extended';

@Component({
  imports: [FileDropDirective],
  template: ` <div atlasFileDrop>Test Dropzone</div>`,
})
class TestComponent {}

describe('FileDropDirective', () => {
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    fixture.detectChanges();
  });

  it('should add fileover class on drag events', () => {
    const elementWithDirective = fixture.debugElement.query(By.directive(FileDropDirective));
    expect(elementWithDirective).toBeTruthy();

    const dragoverEvent = mock<DragEvent>();
    elementWithDirective.triggerEventHandler('dragover', dragoverEvent);

    fixture.detectChanges();
    expect(elementWithDirective.classes['fileover']).toBe(true);

    const dragleaveEvent = mock<DragEvent>();
    elementWithDirective.triggerEventHandler('dragleave', dragleaveEvent);
    fixture.detectChanges();
    expect(elementWithDirective.classes['fileover']).toBeFalsy();
  });
});
