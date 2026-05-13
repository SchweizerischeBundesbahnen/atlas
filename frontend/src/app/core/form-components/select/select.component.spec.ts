import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OverlayContainer } from '@angular/cdk/overlay';
import { By } from '@angular/platform-browser';
import { FormControl, FormGroup } from '@angular/forms';
import { inputBinding, signal } from '@angular/core';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { SelectComponent } from './select.component';
import { translateServiceProvider } from '../../../app.testing.mocks';

describe('SelectComponent', () => {
  let component: SelectComponent<unknown>;
  let fixture: ComponentFixture<SelectComponent<unknown>>;

  let overlayContainer: OverlayContainer;
  let overlayContainerElement: HTMLElement;

  let formGroupInput: ReturnType<typeof signal<FormGroup>>;
  let controlNameInput: ReturnType<typeof signal<string>>;
  let optionsInput: ReturnType<typeof signal<string[]>>;
  let multipleInput: ReturnType<typeof signal<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    const formGroupInputName: keyof SelectComponent<unknown> = 'formGroup';
    const controlNameInputName: keyof SelectComponent<unknown> = 'controlName';
    const optionsInputName: keyof SelectComponent<unknown> = 'options';
    const multipleInputName: keyof SelectComponent<unknown> = 'multiple';

    formGroupInput = signal(
      new FormGroup({
        testControl: new FormControl(null),
      })
    );
    controlNameInput = signal('testControl');
    optionsInput = signal(['selectedValueOne', 'selectedValueTwo']);
    multipleInput = signal(false);

    fixture = TestBed.createComponent(SelectComponent, {
      bindings: [
        inputBinding(formGroupInputName, formGroupInput),
        inputBinding(controlNameInputName, controlNameInput),
        inputBinding(optionsInputName, optionsInput),
        inputBinding(multipleInputName, multipleInput),
      ],
    });
    component = fixture.componentInstance;
    overlayContainer = TestBed.inject(OverlayContainer);
    overlayContainerElement = overlayContainer.getContainerElement();
  });

  afterEach(() => overlayContainer.ngOnDestroy());

  const clickElement = (element: HTMLElement) => element.click();

  const openSelect = () => {
    const selectTrigger = fixture.debugElement.query(By.css('.mat-mdc-select-trigger')).nativeElement as HTMLElement;
    clickElement(selectTrigger);
  };

  const getRenderedOptions = () => Array.from(overlayContainerElement.querySelectorAll('mat-option')) as HTMLElement[];

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should emit correct object on selectionChange when multiple=true', () => {
    multipleInput.set(true);
    fixture.detectChanges();

    const emitSpy = vi.spyOn(component.selectChanged, 'emit').mockImplementation(() => {});

    openSelect();
    let renderedOptions = getRenderedOptions();
    clickElement(renderedOptions[0]);

    emitSpy.mockClear();

    renderedOptions = getRenderedOptions();
    clickElement(renderedOptions[1]);

    expect(emitSpy).toHaveBeenCalledExactlyOnceWith({ value: ['selectedValueOne', 'selectedValueTwo'] });
  });

  it('should emit correct object on selectionChange when multiple=false', () => {
    fixture.detectChanges();
    const emitSpy = vi.spyOn(component.selectChanged, 'emit').mockImplementation(() => {});

    openSelect();
    const renderedOptions = getRenderedOptions();
    clickElement(renderedOptions[0]);

    expect(emitSpy).toHaveBeenCalledExactlyOnceWith({ value: ['selectedValueOne'] });
  });
});
