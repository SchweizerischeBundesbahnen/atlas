import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FormModule} from '../../module/form.module';
import {TranslateFakeLoader, TranslateLoader, TranslateModule, TranslatePipe,} from '@ngx-translate/core';
import {FormControl, FormGroup} from '@angular/forms';
import {InfoIconComponent} from '../info-icon/info-icon.component';
import {StringListComponent} from "./string-list.component";
import {TextFieldComponent} from "../text-field/text-field.component";
import {MockAtlasButtonComponent} from "../../../app.testing.mocks";
import {MatChipsModule} from "@angular/material/chips";

describe('StringListComponent', () => {
  let component: StringListComponent;
  let fixture: ComponentFixture<StringListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StringListComponent, MockAtlasButtonComponent, TextFieldComponent, InfoIconComponent],
      imports: [
        FormModule,
        MatChipsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: TranslatePipe }],
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
