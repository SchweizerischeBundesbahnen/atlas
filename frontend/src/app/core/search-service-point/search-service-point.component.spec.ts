import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { SearchServicePointComponent } from './search-service-point.component';
import { AppTestingModule } from '../../app.testing.module';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { BERN_WYLEREGG } from '../../pages/sepodi/service-point-test-data';
import { ServicePointSearchResult, ServicePointsService } from '../../api';
import { SearchSelectComponent } from '../form-components/search-select/search-select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { ServicePointSearch } from './service-point-search';
import SpyObj = jasmine.SpyObj;

describe('SearchServicePointComponent', () => {
  let component: SearchServicePointComponent;
  let fixture: ComponentFixture<SearchServicePointComponent>;
  let servicePointsServiceSpy: SpyObj<ServicePointsService>;
  let router: Router;

  const activatedRouteMock = { data: of({ servicePoint: [BERN_WYLEREGG] }) };

  beforeEach(() => {
    servicePointsServiceSpy = jasmine.createSpyObj<ServicePointsService>('servicePointsService', [
      'searchServicePoints',
    ]);
    servicePointsServiceSpy.searchServicePoints.withArgs({ value: 'be' }).and.returnValue(of());

    TestBed.configureTestingModule({
      declarations: [SearchServicePointComponent, SearchSelectComponent],
      imports: [AppTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: ServicePointsService, useValue: servicePointsServiceSpy },
        { provide: TranslatePipe },
      ],
    });
    fixture = TestBed.createComponent(SearchServicePointComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.searchType = ServicePointSearch.SePoDi;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to servicePoint details', () => {
    //given
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    const searchResult: ServicePointSearchResult = { number: 8507000, designationOfficial: 'Bern' };
    //when
    component.navigateTo(searchResult);
    //then
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should load result', fakeAsync(() => {
    //when
    fixture.componentInstance.searchInput$.next('be');
    fixture.detectChanges();
    tick(1000);
    //then
    expect(component.searchValue).toEqual('be');
    expect(servicePointsServiceSpy.searchServicePoints).toHaveBeenCalled();
  }));

  it('should not load result when search input length is smaller than 2', fakeAsync(() => {
    //when
    fixture.componentInstance.searchInput$.next('b');
    fixture.detectChanges();
    tick(1000);
    //then
    expect(component.searchValue).toEqual('b');
    expect(servicePointsServiceSpy.searchServicePoints).not.toHaveBeenCalled();
  }));

  it('should init search value', () => {
    //when
    component.initSearchValue('be ');
    //then
    expect(component.searchValue).toEqual('be');
  });

  it('should get placeholder label when searchInput < 2', fakeAsync(() => {
    //when
    fixture.componentInstance.searchInput$.next('b');
    fixture.detectChanges();
    tick(1000);
    //then
    expect(component.minThermLongText).toEqual('COMMON.TYPE_TO_SEARCH_SHORT');
    expect(component.notFoundText).toEqual('COMMON.TYPE_TO_SEARCH_SHORT');
  }));

  it('should get placeholder label when searchInput >= 2', fakeAsync(() => {
    //when
    fixture.componentInstance.searchInput$.next('be');
    fixture.detectChanges();
    tick(1000);
    //then
    expect(component.minThermLongText).toEqual('COMMON.NODATAFOUND');
    expect(component.notFoundText).toEqual('COMMON.NODATAFOUND');
  }));
});
