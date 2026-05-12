import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { TextFieldComponent } from './text-field.component';
import { FormControl, FormGroup } from '@angular/forms';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { inputBinding } from '@angular/core';

describe('TextFieldComponent', () => {
  let component: TextFieldComponent;
  let fixture: ComponentFixture<TextFieldComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    const formGroupInputName: keyof TextFieldComponent = 'formGroup';
    const controlNameInputName: keyof TextFieldComponent = 'controlName';
    fixture = TestBed.createComponent(TextFieldComponent, {
      bindings: [
        inputBinding(
          formGroupInputName,
          () =>
            new FormGroup({
              number: new FormControl('ch:slnid:12345'),
            })
        ),
        inputBinding(controlNameInputName, () => 'number'),
      ],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
