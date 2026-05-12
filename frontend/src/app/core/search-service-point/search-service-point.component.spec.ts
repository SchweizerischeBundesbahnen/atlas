import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SearchServicePointComponent } from './search-service-point.component';
import { AppTestingModule } from '../../app.testing.module';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom, of, skip } from 'rxjs';
import { ServicePointSearchResult } from '../../api';
import { SearchSelectComponent } from '../form-components/search-select/search-select.component';
import { TranslatePipe } from '@ngx-translate/core';
import { ServicePointSearch } from './service-point-search';
import { BERN_WYLEREGG } from '../../../test/data/service-point';
import { ServicePointInternalService } from '../../api/service/sepodi/service-point-internal.service';
import { inputBinding } from '@angular/core';

type ServicePointInternalServiceMock = Mocked<Pick<ServicePointInternalService, 'searchServicePoints'>>;

describe('SearchServicePointComponent', () => {
  let component: SearchServicePointComponent;
  let fixture: ComponentFixture<SearchServicePointComponent>;

  let servicePointInternalService: ServicePointInternalServiceMock;
  let router: Router;

  const activatedRouteMock = {
    data: of({ servicePoint: [BERN_WYLEREGG] }),
  } as Partial<ActivatedRoute>;

  beforeEach(() => {
    // Mocking
    servicePointInternalService = {
      searchServicePoints: vi.fn().mockReturnValue(of([{ sloid: 'ch:1:sloid:100' }])),
    };

    // Configuration
    TestBed.configureTestingModule({
      imports: [AppTestingModule, SearchServicePointComponent, SearchSelectComponent],
      providers: [
        TranslatePipe,
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        {
          provide: ServicePointInternalService,
          useValue: servicePointInternalService,
        },
      ],
    });

    // Arrangement
    router = TestBed.inject(Router);
    const searchTypeInputName: keyof SearchServicePointComponent = 'searchType';
    fixture = TestBed.createComponent(SearchServicePointComponent, {
      bindings: [inputBinding(searchTypeInputName, () => ServicePointSearch.SePoDi)],
    });
    component = fixture.componentInstance;

    component._DEBOUNCE_TIME = 0;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to servicePoint details', () => {
    //given
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    const searchResult: ServicePointSearchResult = {
      number: 8507000,
      designationOfficial: 'Bern',
    };
    //when
    component.navigateTo(searchResult);
    //then
    expect(router.navigate).toHaveBeenCalledOnce();
  });

  it('should load result', async () => {
    const promise = firstValueFrom(component.servicePointSearchResult$.pipe(skip(1)));
    component.searchInput$.next('be');
    await promise;

    expect(component.searchValue).toEqual('be');
    expect(servicePointInternalService.searchServicePoints).toHaveBeenCalledWith({ value: 'be' });
  });

  it('should not load result when search input length is smaller than 2', async () => {
    const promise = firstValueFrom(component.servicePointSearchResult$.pipe(skip(1)));
    component.searchInput$.next('b');
    await promise;

    expect(component.searchValue).toEqual('b');
    expect(servicePointInternalService.searchServicePoints).not.toHaveBeenCalled();
  });

  it('should init search value', () => {
    //when
    component.initSearchValue('be ');
    //then
    expect(component.searchValue).toEqual('be');
  });

  it('should get placeholder label when searchInput < 2', async () => {
    const promise = firstValueFrom(component.servicePointSearchResult$.pipe(skip(1)));
    component.searchInput$.next('b');
    await promise;

    expect(component.minThermLongText).toEqual('COMMON.TYPE_TO_SEARCH_SHORT');
    expect(component.notFoundText).toEqual('COMMON.TYPE_TO_SEARCH_SHORT');
  });

  it('should get placeholder label when searchInput >= 2', async () => {
    const promise = firstValueFrom(component.servicePointSearchResult$.pipe(skip(1)));
    component.searchInput$.next('be');
    await promise;

    expect(component.minThermLongText).toEqual('COMMON.NODATAFOUND');
    expect(component.notFoundText).toEqual('COMMON.NODATAFOUND');
  });
});
