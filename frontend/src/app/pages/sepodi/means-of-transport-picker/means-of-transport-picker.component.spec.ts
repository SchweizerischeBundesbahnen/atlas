import { ComponentFixture, TestBed } from '@angular/core/testing';

import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { FormControl, FormGroup } from '@angular/forms';
import { FormModule } from '../../../core/module/form.module';
import { MeansOfTransportPickerComponent } from './means-of-transport-picker.component';
import { MeanOfTransport } from '../../../api';

describe('MeansOfTransportPickerComponent', () => {
  let component: MeansOfTransportPickerComponent;
  let fixture: ComponentFixture<MeansOfTransportPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeansOfTransportPickerComponent],
      imports: [
        FormModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();

    fixture = TestBed.createComponent(MeansOfTransportPickerComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      meansOfTransport: new FormControl([MeanOfTransport.Bus]),
    });
    component.controlName = 'meansOfTransport';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
