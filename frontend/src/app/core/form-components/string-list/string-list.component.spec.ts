import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { StringListComponent } from './string-list.component';
import { TextFieldComponent } from '../text-field/text-field.component';
import { MatChipsModule } from '@angular/material/chips';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import {
  MockAtlasFieldErrorComponent,
  MockAtlasLabelFieldComponent,
} from '../../../app.testing.mocks';

describe('StringListComponent', () => {
  let component: StringListComponent;
  let fixture: ComponentFixture<StringListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        StringListComponent,
        TextFieldComponent,
        MockAtlasFieldErrorComponent,
        MockAtlasLabelFieldComponent,
      ],
      imports: [
        ReactiveFormsModule,
        MatChipsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StringListComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      emails: new FormControl(['me@sbb.ch']),
    });
    component.controlName = 'emails';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
