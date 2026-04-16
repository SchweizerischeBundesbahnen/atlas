import { beforeEach, describe, expect, it, type MockedObject, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NavigateServicePointComponent, NOTFOUND_LABEL } from './navigate-service-point.component';
import { Router, RouterModule } from '@angular/router';
import { LocationService } from '../../api/service/location/location.service';
import { of } from 'rxjs';
import { inputBinding } from '@angular/core';
import { ServicePointSearch } from '../search-service-point/service-point-search';
import { translateServiceProvider } from '../../app.testing.mocks';

describe('NavigateServicePoint', () => {
  type LocationServiceMock = MockedObject<Pick<LocationService, 'getSloidLocationModel'>>;
  let component: NavigateServicePointComponent;
  let fixture: ComponentFixture<NavigateServicePointComponent>;
  let locationService: LocationServiceMock;
  let router: Router;

  beforeEach(() => {
    // Mocking: stub only the used method of LocationService
    locationService = {
      getSloidLocationModel: vi.fn().mockName('locationService.getSloidLocationModel'),
    };

    // Config: wire TestBed with the mocked service and component
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [translateServiceProvider, { provide: LocationService, useValue: locationService }],
    });

    // Arrangement: obtain the fixture/component/router instances
    type ComponentProp = keyof NavigateServicePointComponent;
    const searchTypeInputName: ComponentProp = 'searchType';
    fixture = TestBed.createComponent(NavigateServicePointComponent, {
      bindings: [inputBinding(searchTypeInputName, () => ServicePointSearch.PRM)],
    });
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should find and navigate PRM', () => {
    //given
    locationService.getSloidLocationModel.mockReturnValue(
      of([{ sloid: 'ch:1:sloid:12:0:1:1', sloidType: 'REFERENCE_POINT' }])
    );
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    //when
    component.doSearch('ch:1:sloid:12:0:1');
    //then
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not find and no navigate PRM', () => {
    //given
    locationService.getSloidLocationModel.mockReturnValue(of([]));
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    //when
    component.doSearch('ch:1:sloid:12:0:1:1');
    //then
    expect(router.navigate).not.toHaveBeenCalled();
    expect(component.resultMsg).toBe(NOTFOUND_LABEL);
  });
});
