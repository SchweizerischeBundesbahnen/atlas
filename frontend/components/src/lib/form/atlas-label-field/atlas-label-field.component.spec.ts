import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FieldExample } from '../../../../../src/app/core/form-components/text-field/field-example';
import { By } from '@angular/platform-browser';
import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
import { beforeEach, describe, expect, it } from 'vitest';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { inputBinding } from '@angular/core';

describe('AtlasLabelFieldComponent', () => {
  let component: AtlasLabelFieldComponent;
  let fixture: ComponentFixture<AtlasLabelFieldComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService({
          loader: provideTranslateHttpLoader({
            prefix: './assets/i18n/',
            suffix: '.json',
            enforceLoading: true,
            useHttpBackend: true,
          }),
        }),
      ],
    });

    const fieldLabelInputName: keyof AtlasLabelFieldComponent = 'fieldLabel';
    fixture = TestBed.createComponent(AtlasLabelFieldComponent, {
      bindings: [inputBinding(fieldLabelInputName, () => 'test label')],
    });
    component = fixture.componentInstance;
  });

  it('should translate without arg', () => {
    const fieldExample: FieldExample = {
      label: 'label',
      translate: true,
      numberOfChars: 2,
    };
    expect(component.translate(fieldExample)).toEqual('label');
  });

  it('should translate with arg', () => {
    const fieldExample: FieldExample = {
      label: 'hallo',
      translate: true,
      numberOfChars: 2,
      arg: { key: 'key', value: 'value' },
    };
    expect(component.translate(fieldExample)).toEqual('hallo');
  });

  it('should return only label', () => {
    const fieldExample: FieldExample = {
      label: '',
    };
    expect(component.translate(fieldExample)).toEqual('');
  });

  it('should translate without arg', () => {
    const fieldExample: FieldExample = {
      label: 'hallo',
      translate: true,
      numberOfChars: 2,
    };
    component.fieldExamples = [fieldExample];
    fixture.detectChanges();
    const element = fixture.debugElement.query(By.css('.font-regular-sm'));
    expect(element.nativeElement.textContent).toEqual('hallo');
  });
});
