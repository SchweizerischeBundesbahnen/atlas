import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TimetableFieldNumberSelectComponent } from './timetable-field-number-select.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormControl, FormGroup } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { MaterialModule } from '../../module/material.module';

describe('TimetableFieldNumberSelectComponent', () => {
  let component: TimetableFieldNumberSelectComponent;
  let fixture: ComponentFixture<TimetableFieldNumberSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TimetableFieldNumberSelectComponent, SearchSelectComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        NgSelectModule,
        MaterialModule,
        HttpClientTestingModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TimetableFieldNumberSelectComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      testControl: new FormControl(null),
    });
    component.controlName = 'testControl';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
