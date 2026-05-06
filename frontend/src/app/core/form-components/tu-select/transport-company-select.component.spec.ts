import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach } from 'vitest';
import { TransportCompanySelectComponent } from './transport-company-select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormControl, FormGroup } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { inputBinding, signal } from '@angular/core';

describe('TransportCompanySelectComponent', () => {
  let component: TransportCompanySelectComponent;
  let fixture: ComponentFixture<TransportCompanySelectComponent>;
  let formGroupInput: ReturnType<typeof signal<FormGroup>>;
  let controlNameInput: ReturnType<typeof signal<string>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NgSelectModule,
        TransportCompanySelectComponent,
        SearchSelectComponent,
        AtlasLabelFieldComponent,
        AtlasFieldErrorComponent,
      ],
      providers: [TranslatePipe, translateServiceProvider, provideHttpClientTesting()],
    }).compileComponents();

    const formGroupInputName: keyof TransportCompanySelectComponent = 'formGroup';
    const controlNameInputName: keyof TransportCompanySelectComponent = 'controlName';
    formGroupInput = signal(
      new FormGroup({
        testControl: new FormControl(null),
      })
    );
    controlNameInput = signal('testControl');
    fixture = TestBed.createComponent(TransportCompanySelectComponent, {
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
});
