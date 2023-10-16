import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SearchSelectComponent } from '../../../../../core/form-components/search-select/search-select.component';
import { AtlasFieldErrorComponent } from '../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { MaterialModule } from '../../../../../core/module/material.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { ServicePointDetailSearchComponent } from './service-point-detail-search.component';

describe('ServicePointDetailSearchComponent', () => {
  let component: ServicePointDetailSearchComponent;
  let fixture: ComponentFixture<ServicePointDetailSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        ServicePointDetailSearchComponent,
        SearchSelectComponent,
        AtlasFieldErrorComponent,
      ],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        NgSelectModule,
        MaterialModule,
        HttpClientTestingModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ServicePointDetailSearchComponent);
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
