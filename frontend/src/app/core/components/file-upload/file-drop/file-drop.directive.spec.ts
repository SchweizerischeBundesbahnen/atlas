import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { By } from '@angular/platform-browser';
import { FileDropDirective } from './file-drop.directive';

@Component({
  template: `<div atlas-file-drop>Test Dropzone</div>`,
})
class TestComponent {}

describe('FileDropDirective', () => {
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      declarations: [FileDropDirective, TestComponent],
    }).createComponent(TestComponent);

    fixture.detectChanges();
  });

  it('should add fileover class on drag events', () => {
    const elementWithDirective = fixture.debugElement.query(By.directive(FileDropDirective));
    expect(elementWithDirective).toBeTruthy();

    elementWithDirective.triggerEventHandler('dragover', new DragEvent('dragover'));
    fixture.detectChanges();
    expect(elementWithDirective.classes['fileover']).toBeTrue();

    elementWithDirective.triggerEventHandler('dragleave', new DragEvent('dragleave'));
    fixture.detectChanges();
    expect(elementWithDirective.classes['fileover']).toBeFalsy();
  });
});
