import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { signal } from '@angular/core';
import { Field, form, required, validateTree } from '@angular/forms/signals';
import { beforeEach, describe, expect, it } from 'vitest';
import { AtlasFieldErrorComponent } from './atlas-field-error.component';

const createField = (initialValue: string): Field<string> =>
  TestBed.runInInjectionContext(() => {
    const model = signal({ value: initialValue });
    const testForm = form(model, (schemaPath) => {
      required(schemaPath.value, { message: () => 'Required' });
      validateTree(schemaPath.value, (ctx) => {
        const value = ctx.valueOf(schemaPath.value);
        if (!value || value.length < 3) {
          return [{ kind: 'tooShort', message: 'Too short' }];
        }
        return null;
      });
    });
    return testForm.value;
  });

describe('AtlasFieldErrorComponent', () => {
  let fixture: ComponentFixture<AtlasFieldErrorComponent<unknown>>;
  let component: AtlasFieldErrorComponent<unknown>;

  const getRenderedMessages = () =>
    fixture.debugElement
      .queryAll(By.css('span.font-regular-xs'))
      .map((element) => element.nativeElement.textContent.trim());

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AtlasFieldErrorComponent],
    });

    fixture = TestBed.createComponent(AtlasFieldErrorComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not render any errors while the field is untouched', () => {
    fixture.componentRef.setInput('field', createField(''));
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('div'))).toBeNull();
    expect(getRenderedMessages()).toEqual([]);
  });

  it('should not render any errors while the field is valid', () => {
    const field = createField('Atlas');
    field().markAsTouched();

    fixture.componentRef.setInput('field', field);
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('div'))).toBeNull();
    expect(getRenderedMessages()).toEqual([]);
  });

  it('should render all validation messages when the field is touched and invalid', () => {
    const field = createField('');
    field().markAsTouched();

    fixture.componentRef.setInput('field', field);
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.css('div'))).not.toBeNull();
    expect(getRenderedMessages()).toEqual(['Required', 'Too short']);
  });
});
