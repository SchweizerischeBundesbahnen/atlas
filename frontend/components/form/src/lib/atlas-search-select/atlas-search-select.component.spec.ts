import { Component, input, output, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { disabled, Field, form } from '@angular/forms/signals';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { Subject } from 'rxjs';
import { NgSelectComponent } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { AtlasSearchSelectComponent } from './atlas-search-select.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { translateServiceProvider } from '../../testing.mocks';

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

  readonly ngModelChange = output<TestItem | null | undefined>();
}

@Component({ selector: 'atlas-field-error', standalone: true, template: '' })
class MockAtlasFieldErrorComponent {
  readonly field = input.required<Field<TestItem | null>>();
}

/**
 * Minimal wrapper whose sole purpose is to satisfy contentChild.required('#labelOptionTemplates').
 * All inputs are forwarded via signal bindings so fixture.componentRef.setInput() works on this wrapper.
 */
@Component({
  standalone: true,
  imports: [AtlasSearchSelectComponent],
  template: `
    <atlas-search-select
      [field]="field()"
      [items]="items()"
      [multiple]="multiple()"
      [placeholderTextKey]="placeholderTextKey()"
      [bindValue]="bindValue()"
      (searchTrigger)="searchEmitted.push($event)"
      (changeTrigger)="changeEmitted.push($event)"
    >
      <ng-template #labelOptionTemplates let-item>
        <span class="test-option">{{ item.label }}</span>
      </ng-template>
    </atlas-search-select>
  `,
})
class SearchSelectWrapper {
  readonly field = input.required<Field<TestItem | null>>();
  readonly items = input<TestItem[]>([]);
  readonly multiple = input(false);
  readonly placeholderTextKey = input('');
  readonly bindValue = input('');
  readonly disabled = input(false);

  readonly searchEmitted: string[] = [];
  readonly changeEmitted: Array<TestItem | null> = [];
}

describe('AtlasSearchSelectComponent', () => {
  let fixture: ComponentFixture<SearchSelectWrapper>;
  let inner: AtlasSearchSelectComponent<TestItem>;

  const createField = (): Field<TestItem | null> =>
    TestBed.runInInjectionContext(() => {
      const model = signal({ value: null });
      const testForm = form(model);
      return testForm.value;
    });

  const createDisabledField = (): Field<TestItem | null> =>
    TestBed.runInInjectionContext(() => {
      const model = signal({ value: null });
      return form(model, (path) => disabled(path.value)).value;
    });

  const getNgSelect = () =>
    fixture.debugElement.query(By.directive(MockNgSelectComponent))?.componentInstance as
      MockNgSelectComponent | undefined;

  const getFieldError = () =>
    fixture.debugElement.query(By.directive(MockAtlasFieldErrorComponent))?.componentInstance as
      MockAtlasFieldErrorComponent | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SearchSelectWrapper],
      providers: [translateServiceProvider],
    }).overrideComponent(AtlasSearchSelectComponent, {
      remove: { imports: [NgSelectComponent, AtlasFieldErrorComponent, FormsModule] },
      add: { imports: [MockNgSelectComponent, MockAtlasFieldErrorComponent] },
    });

    fixture = TestBed.createComponent(SearchSelectWrapper);
    fixture.componentRef.setInput('field', createField());
    fixture.detectChanges();

    inner = fixture.debugElement.query(By.directive(AtlasSearchSelectComponent)).componentInstance;
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
    fixture.componentRef.setInput('field', createDisabledField());
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

    getNgSelect()?.ngModelChange.emit(selected);
    fixture.detectChanges();

    expect(inner.field()().value()).toEqual(selected);
    expect(inner.field()().markAsDirty).toHaveBeenCalledOnce();
    expect(inner.field()().markAsTouched).toHaveBeenCalledOnce();
    expect(fixture.componentInstance.changeEmitted).toEqual([selected]);
  });

  it('should emit null when selection is cleared', () => {
    getNgSelect()?.ngModelChange.emit(undefined);
    fixture.detectChanges();

    expect(inner.field()().value()).toBeNull();
    expect(fixture.componentInstance.changeEmitted).toEqual([null]);
  });

  it('should pass field to atlas-field-error', () => {
    const fieldError = getFieldError();

    expect(fieldError).toBeTruthy();
    expect(fieldError?.field()).toBe(inner.field());
  });
});
