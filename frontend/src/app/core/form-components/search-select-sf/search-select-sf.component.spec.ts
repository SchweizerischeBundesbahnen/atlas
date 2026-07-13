import { Component, input, output, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Field, form } from '@angular/forms/signals';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { Subject } from 'rxjs';

import { NgSelectComponent } from '@ng-select/ng-select';
import { AtlasFieldErrorSfComponent } from '../atlas-field-error-sf/atlas-field-error-sf.component';
import { SearchSelectSfComponent } from './search-select-sf.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { FormsModule } from '@angular/forms';

type TestItem = { id: string; label: string };

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'ng-select',
  standalone: true,
  template: '',
})
class MockNgSelectComponent {
  readonly bindValue = input('');
  readonly items = input<TestItem[]>([]);
  readonly minTermLength = input(0);
  readonly multiple = input(false);
  readonly ngModel = input<TestItem | null>(null);
  readonly placeholder = input('');
  readonly readonly = input(false);
  readonly typeahead = input<Subject<string> | undefined>(undefined);
  readonly appearance = input();
  readonly notFoundText = input();
  readonly typeToSearchText = input();

  // eslint-disable-next-line @angular-eslint/no-output-native
  readonly change = output<TestItem | null | undefined>();
}

@Component({ selector: 'atlas-field-error-sf', standalone: true, template: '' })
class MockAtlasFieldErrorSfComponent {
  readonly field = input.required<Field<TestItem | null>>();
}

/**
 * Minimal wrapper whose sole purpose is to satisfy contentChild.required('#labelOptionTemplates').
 * All inputs are forwarded via signal bindings so fixture.componentRef.setInput() works on this wrapper.
 */
@Component({
  standalone: true,
  imports: [SearchSelectSfComponent],
  template: `
    <atlas-search-select-sf
      [field]="field()"
      [items]="items()"
      [multiple]="multiple()"
      [placeholderTextKey]="placeholderTextKey()"
      [bindValue]="bindValue()"
      [disabled]="disabled()"
      (searchTrigger)="searchEmitted.push($event)"
      (changeTrigger)="changeEmitted.push($event)"
    >
      <ng-template #labelOptionTemplates let-item>
        <span class="test-option">{{ item.label }}</span>
      </ng-template>
    </atlas-search-select-sf>
  `,
})
class SearchSelectSfWrapper {
  readonly field = input.required<Field<TestItem | null>>();
  readonly items = input<TestItem[]>([]);
  readonly multiple = input(false);
  readonly placeholderTextKey = input('');
  readonly bindValue = input('');
  readonly disabled = input(false);

  readonly searchEmitted: string[] = [];
  readonly changeEmitted: Array<TestItem | null> = [];
}

describe('SearchSelectSfComponent', () => {
  let fixture: ComponentFixture<SearchSelectSfWrapper>;
  let inner: SearchSelectSfComponent<TestItem>;

  const createField = (): Field<TestItem | null> =>
    TestBed.runInInjectionContext(() => {
      const model = signal({ value: null });
      const testForm = form(model);
      return testForm.value;
    });

  const getNgSelect = () =>
    fixture.debugElement.query(By.directive(MockNgSelectComponent))?.componentInstance as
      MockNgSelectComponent | undefined;

  const getFieldError = () =>
    fixture.debugElement.query(By.directive(MockAtlasFieldErrorSfComponent))?.componentInstance as
      MockAtlasFieldErrorSfComponent | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SearchSelectSfWrapper],
      providers: [translateServiceProvider],
    }).overrideComponent(SearchSelectSfComponent, {
      remove: { imports: [NgSelectComponent, AtlasFieldErrorSfComponent, FormsModule] },
      add: { imports: [MockNgSelectComponent, MockAtlasFieldErrorSfComponent] },
    });

    fixture = TestBed.createComponent(SearchSelectSfWrapper);
    fixture.componentRef.setInput('field', createField());
    fixture.detectChanges();

    inner = fixture.debugElement.query(By.directive(SearchSelectSfComponent)).componentInstance;
  });

  it('should bind inputs to ng-select', () => {
    const options: TestItem[] = [
      { id: '1', label: 'One' },
      { id: '2', label: 'Two' },
    ];

    fixture.componentRef.setInput('items', options);
    fixture.componentRef.setInput('multiple', true);
    fixture.componentRef.setInput('placeholderTextKey', 'COMMON.SELECT');
    fixture.componentRef.setInput('bindValue', 'id');
    fixture.componentRef.setInput('disabled', true);
    fixture.detectChanges();

    const ngSelect = getNgSelect();

    expect(ngSelect).toBeTruthy();
    expect(ngSelect?.items()).toEqual(options);
    expect(ngSelect?.multiple()).toBe(true);
    expect(ngSelect?.placeholder()).toBe('COMMON.SELECT');
    expect(ngSelect?.bindValue()).toBe('id');
    expect(ngSelect?.readonly()).toBe(true);
    expect(ngSelect?.ngModel()).toBeNull();
    expect(ngSelect?.minTermLength()).toBe(2);
  });

  it('should emit searchTrigger when typeahead subject emits', () => {
    (getNgSelect()?.typeahead() as Subject<string>).next('bern');

    expect(fixture.componentInstance.searchEmitted).toEqual(['bern']);
  });

  it('should update field state and emit changeTrigger when selection changes', () => {
    const selected: TestItem = { id: '7', label: 'Seven' };
    vi.spyOn(inner.field()(), 'markAsDirty');
    vi.spyOn(inner.field()(), 'markAsTouched');

    getNgSelect()?.change.emit(selected);
    fixture.detectChanges();

    expect(inner.field()().value()).toEqual(selected);
    expect(inner.field()().markAsDirty).toHaveBeenCalledOnce();
    expect(inner.field()().markAsTouched).toHaveBeenCalledOnce();
    expect(fixture.componentInstance.changeEmitted).toEqual([selected]);
  });

  it('should emit null when selection is cleared', () => {
    getNgSelect()?.change.emit(undefined);
    fixture.detectChanges();

    expect(inner.field()().value()).toBeNull();
    expect(fixture.componentInstance.changeEmitted).toEqual([null]);
  });

  it('should pass field to atlas-field-error-sf', () => {
    const fieldError = getFieldError();

    expect(fieldError).toBeTruthy();
    expect(fieldError?.field()).toBe(inner.field());
  });
});
