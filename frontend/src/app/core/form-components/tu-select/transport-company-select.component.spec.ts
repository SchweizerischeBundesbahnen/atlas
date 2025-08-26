import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TransportCompanySelectComponent } from './transport-company-select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormControl, FormGroup } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SearchSelectComponent } from '../search-select/search-select.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '@atlas/form/atlas-label-field/atlas-label-field.component';
import { translateServiceProvider } from '../../../app.testing.mocks';

describe('TransportCompanySelectComponent', () => {
  let component: TransportCompanySelectComponent;
  let fixture: ComponentFixture<TransportCompanySelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NgSelectModule,
        HttpClientTestingModule,
        TransportCompanySelectComponent,
        SearchSelectComponent,
        AtlasLabelFieldComponent,
        AtlasFieldErrorComponent,
      ],
      providers: [TranslatePipe, translateServiceProvider],
    }).compileComponents();

    fixture = TestBed.createComponent(TransportCompanySelectComponent);
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
