import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OverlayContainer } from '@angular/cdk/overlay';
import { By } from '@angular/platform-browser';
import { inputBinding, signal } from '@angular/core';
import { Field, disabled, form } from '@angular/forms/signals';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { AtlasSelectComponent } from './atlas-select.component';
import { translateServiceProvider } from '../../testing.mocks';

type SelectValue = string | string[] | null;

describe('AtlasSelectComponent', () => {
  let component: AtlasSelectComponent<string>;
  let fixture: ComponentFixture<AtlasSelectComponent<string>>;

  let overlayContainer: OverlayContainer;
  let overlayContainerElement: HTMLElement;

  let fieldInput: ReturnType<typeof signal<Field<SelectValue>>>;
  let optionsInput: ReturnType<typeof signal<string[]>>;
  let multipleInput: ReturnType<typeof signal<boolean>>;

  const createField = (initialValue: SelectValue): Field<SelectValue> =>
    TestBed.runInInjectionContext(() => {
      const model = signal<{ value: SelectValue }>({ value: initialValue });
      return form(model).value;
    });

  const createDisabledField = (): Field<SelectValue> =>
    TestBed.runInInjectionContext(() => {
      const model = signal<{ value: SelectValue }>({ value: null });
      return form(model, (path) => disabled(path.value)).value;
    });

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    fieldInput = signal(createField(null));
    optionsInput = signal(['selectedValueOne', 'selectedValueTwo']);
    multipleInput = signal(false);

    const fieldInputName: keyof AtlasSelectComponent<string> = 'field';
    const optionsInputName: keyof AtlasSelectComponent<string> = 'options';
    const multipleInputName: keyof AtlasSelectComponent<string> = 'multiple';

    fixture = TestBed.createComponent(AtlasSelectComponent<string>, {
      bindings: [
        inputBinding(fieldInputName, fieldInput),
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

  it('should update field value and emit selectChanged when multiple=false', () => {
    fixture.detectChanges();

    const emitSpy = vi.spyOn(component.selectChanged, 'emit').mockImplementation(() => {});

    openSelect();
    clickElement(getRenderedOptions()[0]);

    expect(emitSpy).toHaveBeenCalledExactlyOnceWith({ value: ['selectedValueOne'] });
    expect(fieldInput()().value()).toBe('selectedValueOne');
  });

  it('should update field value and emit selectChanged when multiple=true', () => {
    multipleInput.set(true);
    fixture.detectChanges();

    const emitSpy = vi.spyOn(component.selectChanged, 'emit').mockImplementation(() => {});

    openSelect();
    clickElement(getRenderedOptions()[0]);
    emitSpy.mockClear();
    clickElement(getRenderedOptions()[1]);

    expect(emitSpy).toHaveBeenCalledExactlyOnceWith({ value: ['selectedValueOne', 'selectedValueTwo'] });
    expect(fieldInput()().value()).toEqual(['selectedValueOne', 'selectedValueTwo']);
  });

  it('should mark the field as dirty and touched on selection', () => {
    fixture.detectChanges();
    expect(fieldInput()().dirty()).toBe(false);
    expect(fieldInput()().touched()).toBe(false);

    openSelect();
    clickElement(getRenderedOptions()[0]);

    expect(fieldInput()().dirty()).toBe(true);
    expect(fieldInput()().touched()).toBe(true);
  });

  it('should disable the select when the field is disabled', () => {
    fieldInput.set(createDisabledField());
    fixture.detectChanges();

    const select = fixture.debugElement.query(By.css('mat-select')).nativeElement as HTMLElement;
    expect(select.classList).toContain('mat-mdc-select-disabled');
  });
});






