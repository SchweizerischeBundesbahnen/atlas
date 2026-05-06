import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach } from 'vitest';
import { TimetableFieldNumberSelectComponent } from './timetable-field-number-select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormControl, FormGroup } from '@angular/forms';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { inputBinding, signal } from '@angular/core';

describe('TimetableFieldNumberSelectComponent', () => {
  let component: TimetableFieldNumberSelectComponent;
  let fixture: ComponentFixture<TimetableFieldNumberSelectComponent>;
  let formGroupInput: ReturnType<typeof signal<FormGroup>>;
  let controlNameInput: ReturnType<typeof signal<string>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NgSelectModule,
        TimetableFieldNumberSelectComponent,
        SearchSelectComponent,
        AtlasLabelFieldComponent,
        AtlasFieldErrorComponent,
      ],
      providers: [TranslatePipe, translateServiceProvider, provideHttpClientTesting()],
    }).compileComponents();

    const formGroupInputName: keyof TimetableFieldNumberSelectComponent = 'formGroup';
    const controlNameInputName: keyof TimetableFieldNumberSelectComponent = 'controlName';
    formGroupInput = signal(
      new FormGroup({
        testControl: new FormControl(null),
      })
    );
    controlNameInput = signal('testControl');
    fixture = TestBed.createComponent(TimetableFieldNumberSelectComponent, {
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
