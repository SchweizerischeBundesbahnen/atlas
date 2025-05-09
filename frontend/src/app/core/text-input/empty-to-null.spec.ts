import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement } from '@angular/core';
import { EmptyToNullDirective } from './empty-to-null';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { AppTestingModule } from '../../app.testing.module';

@Component({
  template: ` <input [formControl]="form" emptyToNull /> `,
  imports: [EmptyToNullDirective, ReactiveFormsModule],
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
      imports: [AppTestingModule, EmptyToNullDirective, TestComponent],
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

  it('should do nothing on number value', () => {
    component.form.setValue(5.2);

    inputField.nativeElement.dispatchEvent(keyUpEvent);
    fixture.detectChanges();

    expect(component.form.value).toBe(5.2);
  });
});
