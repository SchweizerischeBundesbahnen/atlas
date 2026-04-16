import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import { SearchSelectComponent } from '../../../../../core/form-components/search-select/search-select.component';
import { AtlasFieldErrorComponent } from '../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { FormControl, FormGroup } from '@angular/forms';
import { KilometerMasterSearchComponent } from './kilometer-master-search.component';
import { of } from 'rxjs';
import { translateServiceProvider } from '../../../../../app.testing.mocks';
import { ServicePointInternalService } from '../../../../../api/service/sepodi/service-point-internal.service';

describe('KilometerMasterSearchComponent', () => {
  let component: KilometerMasterSearchComponent;
  let fixture: ComponentFixture<KilometerMasterSearchComponent>;
  let servicePointsServiceSpy: Mocked<Pick<ServicePointInternalService, 'searchServicePointsWithRouteNetworkTrue'>>;

  beforeEach(async () => {
    servicePointsServiceSpy = {
      searchServicePointsWithRouteNetworkTrue: vi.fn(),
    };
    servicePointsServiceSpy.searchServicePointsWithRouteNetworkTrue.mockReturnValue(of());

    await TestBed.configureTestingModule({
      providers: [
        {
          provide: ServicePointInternalService,
          useValue: servicePointsServiceSpy,
        },
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
      imports: [NgSelectModule, KilometerMasterSearchComponent, SearchSelectComponent, AtlasFieldErrorComponent],
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

  it('should call searchServicePoints method', async () => {
    //when
    component.searchServicePoint('be');
    //then
    expect(servicePointsServiceSpy.searchServicePointsWithRouteNetworkTrue).toHaveBeenCalled();
  });
});
