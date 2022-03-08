import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement } from '@angular/core';
import { FormControl } from '@angular/forms';
import { CoreModule } from '../module/core.module';
import { By } from '@angular/platform-browser';
import { WithDefaultValueDirective } from './with-default-value.directive';

const keyUpEvent = new KeyboardEvent('keyup');

@Component({
  template: ` <input [formControl]="form" withDefaultValue="defaultValue" /> `,
})
class TestComponent {
  form = new FormControl();
}

describe('WithDefaultValueDirective', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let inputField: DebugElement;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      declarations: [WithDefaultValueDirective, TestComponent],
      imports: [CoreModule],
    }).createComponent(TestComponent);

    component = fixture.componentInstance;

    inputField = fixture.debugElement.query(By.css('input'));
    fixture.detectChanges();
  });

  it('should convert empty to default value', () => {
    component.form.setValue('');

    inputField.nativeElement.dispatchEvent(keyUpEvent);
    fixture.detectChanges();

    expect(component.form.value).toBe('defaultValue');
  });

  it('should do nothing on given value', () => {
    component.form.setValue('cool ');

    inputField.nativeElement.dispatchEvent(keyUpEvent);
    fixture.detectChanges();

    expect(component.form.value).toBe('cool ');
  });
});
