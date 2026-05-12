import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { SearchSelectComponent } from './search-select.component';
import { NgSelectComponent } from '@ng-select/ng-select';
import { FormControl, FormGroup } from '@angular/forms';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { inputBinding, signal } from '@angular/core';
import { mock } from 'vitest-mock-extended';

describe('SearchSelectComponent', () => {
  let component: SearchSelectComponent<unknown>;
  let fixture: ComponentFixture<SearchSelectComponent<unknown>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    const formGroupInputName: keyof SearchSelectComponent<unknown> = 'formGroup';
    const controlNameInputName: keyof SearchSelectComponent<unknown> = 'controlName';
    fixture = TestBed.createComponent(SearchSelectComponent, {
      bindings: [
        inputBinding(
          formGroupInputName,
          () =>
            new FormGroup({
              testControl: new FormControl(null),
            })
        ),
        inputBinding(controlNameInputName, () => 'testControl'),
      ],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('isDropdownOpen should return false', () => {
    expect(component.isDropdownOpen()).toBe(false);
  });

  it('isDropdownOpen should return true', () => {
    const ngSelectMock = mock<NgSelectComponent>();
    ngSelectMock.isOpen.mockImplementation(signal(true));
    component.ngSelect = ngSelectMock;
    expect(component.isDropdownOpen()).toBe(true);
  });
});
