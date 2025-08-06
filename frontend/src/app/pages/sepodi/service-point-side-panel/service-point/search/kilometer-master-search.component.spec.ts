import { ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { SearchSelectComponent } from '../../../../../core/form-components/search-select/search-select.component';
import { AtlasFieldErrorComponent } from '../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { KilometerMasterSearchComponent } from './kilometer-master-search.component';
import { of } from 'rxjs';
import { translateServiceProvider } from '../../../../../app.testing.mocks';
import { ServicePointInternalService } from '../../../../../api/service/sepodi/service-point-internal.service';
import SpyObj = jasmine.SpyObj;

describe('KilometerMasterSearchComponent', () => {
  let component: KilometerMasterSearchComponent;
  let fixture: ComponentFixture<KilometerMasterSearchComponent>;
  let servicePointsServiceSpy: SpyObj<ServicePointInternalService>;

  beforeEach(async () => {
    servicePointsServiceSpy = jasmine.createSpyObj<ServicePointInternalService>(
      'servicePointsService',
      ['searchServicePointsWithRouteNetworkTrue']
    );
    servicePointsServiceSpy.searchServicePointsWithRouteNetworkTrue
      .withArgs({ value: 'be' })
      .and.returnValue(of());

    await TestBed.configureTestingModule({
      providers: [
        {
          provide: ServicePointInternalService,
          useValue: servicePointsServiceSpy,
        },
        translateServiceProvider,
      ],
      imports: [
        NgSelectModule,
        HttpClientTestingModule,
        KilometerMasterSearchComponent,
        SearchSelectComponent,
        AtlasFieldErrorComponent,
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
    expect(
      servicePointsServiceSpy.searchServicePointsWithRouteNetworkTrue
    ).toHaveBeenCalled();
  }));
});
