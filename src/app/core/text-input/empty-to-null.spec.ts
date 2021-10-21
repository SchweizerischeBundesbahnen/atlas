import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement } from '@angular/core';
import { EmptyToNullDirective } from './empty-to-null';
import { FormControl } from '@angular/forms';
import { CoreModule } from '../module/core.module';
import { By } from '@angular/platform-browser';

@Component({
  template: ` <input [formControl]="form" emptyToNull /> `,
})
class TestComponent {
  form = new FormControl();
}

const keyUpEvent = new KeyboardEvent('keyup');

describe('EmptyToNullDirective', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let inputField: DebugElement;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      declarations: [EmptyToNullDirective, TestComponent],
      imports: [CoreModule],
    }).createComponent(TestComponent);

    component = fixture.componentInstance;

    inputField = fixture.debugElement.query(By.css('input'));
    fixture.detectChanges();
  });

  it('should convert empty string to null', () => {
    component.form.setValue('');

    inputField.nativeElement.dispatchEvent(keyUpEvent);
    fixture.detectChanges();

    expect(component.form.value).toBe(null);
  });

  it('should do nothing on given value', () => {
    component.form.setValue('cool ');

    inputField.nativeElement.dispatchEvent(keyUpEvent);
    fixture.detectChanges();

    expect(component.form.value).toBe('cool ');
  });
});
