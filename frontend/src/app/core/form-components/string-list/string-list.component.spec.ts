import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { FormControl, FormGroup } from '@angular/forms';
import { StringListComponent } from './string-list.component';
import { inputBinding, signal } from '@angular/core';
import { translateServiceProvider } from '../../../app.testing.mocks';

describe('StringListComponent', () => {
  let component: StringListComponent;
  let fixture: ComponentFixture<StringListComponent>;
  let formGroupInput: ReturnType<typeof signal<FormGroup | undefined>>;
  let controlNameInput: ReturnType<typeof signal<string | undefined>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    const fieldLabelInputName: keyof StringListComponent = 'fieldLabel';
    const requiredInputName: keyof StringListComponent = 'required';
    const placeHolderTextInputName: keyof StringListComponent = 'placeHolderText';
    const formGroupInputName: keyof StringListComponent = 'formGroup';
    const controlNameInputName: keyof StringListComponent = 'controlName';
    formGroupInput = signal(undefined);
    controlNameInput = signal(undefined);
    fixture = TestBed.createComponent(StringListComponent, {
      bindings: [
        inputBinding(formGroupInputName, formGroupInput),
        inputBinding(controlNameInputName, controlNameInput),
        inputBinding(fieldLabelInputName, () => 'test label'),
        inputBinding(requiredInputName, () => false),
        inputBinding(placeHolderTextInputName, () => 'test placeholder text'),
      ],
    });
    component = fixture.componentInstance;
  });

  it('should throw when no initial value defined', () => {
    formGroupInput.set(
      new FormGroup({
        emails: new FormControl(undefined),
      })
    );
    controlNameInput.set('emails');
    expect(fixture.detectChanges).toThrow();
  });

  it('should create', () => {
    formGroupInput.set(
      new FormGroup({
        emails: new FormControl([]),
      })
    );
    controlNameInput.set('emails');
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('get strListCtrl should throw when controlName not defined', () => {
    expect(fixture.detectChanges).toThrow();
  });

  it('get strListCtrl should throw when ctrl not defined', () => {
    formGroupInput.set(
      new FormGroup({
        emails: new FormControl([]),
      })
    );
    controlNameInput.set('false');
    expect(fixture.detectChanges).toThrow();
  });

  it('should not add item when its already there', () => {
    formGroupInput.set(
      new FormGroup({
        emails: new FormControl(['a@a.ch']),
      })
    );
    controlNameInput.set('emails');
    fixture.detectChanges();
    component.strListFormGroup.setValue({
      input: 'a@a.ch',
    });
    component.addItem();
    expect(component.formGroup()?.get(component.controlName()!)?.value).toEqual(['a@a.ch']);
    expect(component.formGroup()?.dirty).toBe(false);
    expect(component.strListFormGroup.get('input')?.value).toEqual('');
  });

  it('should add item when its not already there', () => {
    formGroupInput.set(
      new FormGroup({
        emails: new FormControl(['a@a.ch']),
      })
    );
    controlNameInput.set('emails');
    fixture.detectChanges();
    component.strListFormGroup.setValue({
      input: 'b@b.ch',
    });
    component.addItem();
    expect(component.formGroup()?.get(component.controlName()!)?.value).toEqual(['a@a.ch', 'b@b.ch']);
    expect(component.formGroup()?.dirty).toBe(true);
    expect(component.strListFormGroup.get('input')?.value).toEqual('');
  });

  it('should do nothing when input not valid', () => {
    formGroupInput.set(
      new FormGroup({
        emails: new FormControl(['a@a.ch']),
      })
    );
    controlNameInput.set('emails');
    fixture.detectChanges();
    component.strListFormGroup.setValue({
      input: null,
    });
    component.addItem();
    expect(component.formGroup()?.get(component.controlName()!)?.value).toEqual(['a@a.ch']);
    expect(component.formGroup()?.dirty).toBe(false);
    expect(component.strListFormGroup.get('input')?.value).toEqual(null);
  });

  it('should remove item', () => {
    formGroupInput.set(
      new FormGroup({
        emails: new FormControl(['a@a.ch']),
      })
    );
    controlNameInput.set('emails');
    fixture.detectChanges();
    component.removeItem(0);
    expect(component.formGroup()?.get(component.controlName()!)?.value).toEqual([]);
    expect(component.formGroup()?.dirty).toBe(true);
  });
});
