import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchServicePointComponent } from './search-service-point.component';
import { AppTestingModule } from '../../../app.testing.module';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { BERN_WYLEREGG } from '../service-point-test-data';
import { ServicePointSearchResult, ServicePointsService } from '../../../api';
import { SearchSelectComponent } from '../../../core/form-components/search-select/search-select.component';
import SpyObj = jasmine.SpyObj;

describe('SearchServicePointComponent', () => {
  let component: SearchServicePointComponent;
  let fixture: ComponentFixture<SearchServicePointComponent>;
  let servicePointsServiceSpy: SpyObj<ServicePointsService>;
  let router: Router;

  const activatedRouteMock = { data: of({ servicePoint: [BERN_WYLEREGG] }) };

  beforeEach(() => {
    servicePointsServiceSpy = jasmine.createSpyObj<ServicePointsService>(['searchServicePoints']);

    TestBed.configureTestingModule({
      declarations: [SearchServicePointComponent, SearchSelectComponent],
      imports: [AppTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: ServicePointsService, useValue: servicePointsServiceSpy },
      ],
    });
    fixture = TestBed.createComponent(SearchServicePointComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should search servicePoint', () => {
    //when
    component.searchServicePoint('bern');
    //then
    expect(servicePointsServiceSpy.searchServicePoints).toHaveBeenCalledOnceWith({ value: 'bern' });
  });

  it('should not search servicePoint', () => {
    //when
    component.searchServicePoint(' ');
    //then
    expect(servicePointsServiceSpy.searchServicePoints).not.toHaveBeenCalledOnceWith({
      value: 'bern',
    });
  });

  it('should navigate to servicePoint details', () => {
    //given
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    const searchResult: ServicePointSearchResult = { number: 8507000, designationOfficial: 'Bern' };
    //when
    component.navigateToServicePoint(searchResult);
    //then
    expect(router.navigate).toHaveBeenCalled();
  });
});
