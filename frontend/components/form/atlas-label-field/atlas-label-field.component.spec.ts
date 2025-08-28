import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtlasLabelFieldComponent } from './atlas-label-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../src/app/app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { NgClass } from '@angular/common';
import { InfoIconComponent } from '@atlas/form';
import { InfoLinkDirective } from '@atlas/form/info-icon/info-link.directive';
import { FieldExample } from '../../../src/app/core/form-components/text-field/field-example';
import { By } from '@angular/platform-browser';

describe('AtlasLabelFieldComponent', () => {
  let component: AtlasLabelFieldComponent;
  let fixture: ComponentFixture<AtlasLabelFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AtlasLabelFieldComponent,
        NgClass,
        InfoIconComponent,
        InfoLinkDirective,
        TranslatePipe,
      ],
      providers: [TranslatePipe, translateServiceProvider, provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(AtlasLabelFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
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
