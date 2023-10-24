import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { SearchSelectComponent } from '../../../../../core/form-components/search-select/search-select.component';
import { AtlasFieldErrorComponent } from '../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { MaterialModule } from '../../../../../core/module/material.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { KilometerMasterSearchComponent } from './kilometer-master-search.component';
import { ServicePointsService } from '../../../../../api';
import { of } from 'rxjs';
import SpyObj = jasmine.SpyObj;

describe('KilometerMasterSearchComponent', () => {
  let component: KilometerMasterSearchComponent;
  let fixture: ComponentFixture<KilometerMasterSearchComponent>;
  let servicePointsServiceSpy: SpyObj<ServicePointsService>;

  beforeEach(async () => {
    servicePointsServiceSpy = jasmine.createSpyObj<ServicePointsService>('servicePointsService', [
      'searchServicePointsWithRouteNetworkTrue',
    ]);
    servicePointsServiceSpy.searchServicePointsWithRouteNetworkTrue
      .withArgs({ value: 'be' })
      .and.returnValue(of());

    await TestBed.configureTestingModule({
      declarations: [
        KilometerMasterSearchComponent,
        SearchSelectComponent,
        AtlasFieldErrorComponent,
      ],
      providers: [{ provide: ServicePointsService, useValue: servicePointsServiceSpy }],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        NgSelectModule,
        MaterialModule,
        HttpClientTestingModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(KilometerMasterSearchComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      testControl: new FormControl(null),
    });
    component.controlName = 'testControl';
    fixture.detectChanges();
  });

  it('should create kilometer master search component', () => {
    expect(component).toBeTruthy();
  });

  it('should call searchServicePoints method', fakeAsync(() => {
    //when
    component.searchServicePoint('be');
    //then
    expect(servicePointsServiceSpy.searchServicePointsWithRouteNetworkTrue).toHaveBeenCalled();
  }));
});
