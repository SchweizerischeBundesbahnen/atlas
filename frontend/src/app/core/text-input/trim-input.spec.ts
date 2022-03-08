import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement } from '@angular/core';
import { FormControl } from '@angular/forms';
import { CoreModule } from '../module/core.module';
import { By } from '@angular/platform-browser';
import { TrimInputDirective } from './trim-input';

@Component({
  template: ` <input [formControl]="form" trim /> `,
})
class TestComponent {
  form = new FormControl();
}

const keyUpEvent = new KeyboardEvent('keyup');

describe('TrimInputDirective', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let inputField: DebugElement;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      declarations: [TrimInputDirective, TestComponent],
      imports: [CoreModule],
    }).createComponent(TestComponent);

    component = fixture.componentInstance;

    inputField = fixture.debugElement.query(By.css('input'));
    fixture.detectChanges();
  });

  it('should trim whitespaces', () => {
    component.form.setValue(' asdf ');

    inputField.nativeElement.dispatchEvent(keyUpEvent);
    fixture.detectChanges();

    expect(component.form.value).toBe('asdf');
  });
});
