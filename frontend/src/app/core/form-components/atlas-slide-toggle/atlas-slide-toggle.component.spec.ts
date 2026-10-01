import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach } from 'vitest';
import { AtlasSlideToggleComponent } from './atlas-slide-toggle.component';
import { FormControl, FormGroup } from '@angular/forms';
import { inputBinding, signal } from '@angular/core';
import { disabled, form } from '@angular/forms/signals';
import { Field } from '@angular/forms/signals';

describe('AtlasSlideToggleComponent', () => {
  let component: AtlasSlideToggleComponent;
  let fixture: ComponentFixture<AtlasSlideToggleComponent>;
  let formGroupInput: ReturnType<typeof signal<FormGroup | undefined>>;
  let controlNameInput: ReturnType<typeof signal<string | undefined>>;
  let fieldInput: ReturnType<typeof signal<Field<boolean> | undefined>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AtlasSlideToggleComponent],
    });

    const formGroupInputName: keyof AtlasSlideToggleComponent = 'formGroup';
    const controlNameInputName: keyof AtlasSlideToggleComponent = 'controlName';
    formGroupInput = signal(undefined);
    controlNameInput = signal(undefined);
    fieldInput = signal(undefined);
    fixture = TestBed.createComponent(AtlasSlideToggleComponent, {
      bindings: [
        inputBinding(formGroupInputName, formGroupInput),
        inputBinding(controlNameInputName, controlNameInput),
        inputBinding('field', fieldInput),
      ],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be created with form', () => {
    formGroupInput.set(
      new FormGroup({
        value: new FormControl(false),
      })
    );
    controlNameInput.set('value');
    fixture.detectChanges();
    expect(component.formControl).toBeTruthy();

    component.handleToggleClick();

    expect(component.formControl?.value).toBe(true);
    expect(component.formControl?.dirty).toBe(true);
  });

  it('marks a signal field dirty and respects disabled state', () => {
    const editable = signal(true);
    const permissionForm = TestBed.runInInjectionContext(() =>
      form(signal({ flag: false }), (path) => disabled(path, { when: () => !editable() }))
    );
    fieldInput.set(permissionForm.flag);
    fixture.detectChanges();

    component.handleToggleClick();
    expect(permissionForm.flag().value()).toBe(true);
    expect(permissionForm().dirty()).toBe(true);

    editable.set(false);
    fixture.detectChanges();
    component.handleToggleClick();
    expect(permissionForm.flag().value()).toBe(true);
    expect((fixture.nativeElement as HTMLElement).querySelector('button')?.disabled).toBe(true);
  });
});
