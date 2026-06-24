import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach } from 'vitest';
import { AtlasSlideToggleComponent } from './atlas-slide-toggle.component';
import { FormControl, FormGroup } from '@angular/forms';
import { inputBinding, signal } from '@angular/core';

describe('AtlasSlideToggleComponent', () => {
  let component: AtlasSlideToggleComponent;
  let fixture: ComponentFixture<AtlasSlideToggleComponent>;
  let formGroupInput: ReturnType<typeof signal<FormGroup | undefined>>;
  let controlNameInput: ReturnType<typeof signal<string | undefined>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AtlasSlideToggleComponent],
    });

    const formGroupInputName: keyof AtlasSlideToggleComponent = 'formGroup';
    const controlNameInputName: keyof AtlasSlideToggleComponent = 'controlName';
    formGroupInput = signal(undefined);
    controlNameInput = signal(undefined);
    fixture = TestBed.createComponent(AtlasSlideToggleComponent, {
      bindings: [
        inputBinding(formGroupInputName, formGroupInput),
        inputBinding(controlNameInputName, controlNameInput),
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
});
