import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivatedRoute, Router } from '@angular/router';
import { AppTestingModule } from '../../../app.testing.module';
import { DisplayDatePipe } from '../../../core/pipe/display-date.pipe';
import { of } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { ActivatedRouteMockType, MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { DateRangeTextComponent } from '../../../core/versioning/date-range-text/date-range-text.component';
import { SplitServicePointNumberPipe } from '../../../core/search-service-point/split-service-point-number.pipe';
import { TextFieldComponent } from '../../../core/form-components/text-field/text-field.component';
import { SelectComponent } from '../../../core/form-components/select/select.component';
import { AtlasLabelFieldComponent } from '../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { SwitchVersionComponent } from '../../../core/components/switch-version/switch-version.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasFieldErrorComponent } from '../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { AtlasSpacerComponent } from '../../../core/components/spacer/atlas-spacer.component';
import { GeographyComponent } from '../geography/geography.component';
import { DecimalNumberPipe } from '../../../core/pipe/decimal-number.pipe';
import { AtlasSlideToggleComponent } from '../../../core/form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { InfoIconComponent } from '../../../core/form-components/info-icon/info-icon.component';
import { RemoveCharsDirective } from '../../../core/form-components/text-field/remove-chars.directive';
import { SloidComponent } from '../../../core/form-components/sloid/sloid.component';
import { LoadingPointsService, ServicePointsService } from '../../../api';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import moment from 'moment/moment';
import { LoadingPointsDetailComponent } from './loading-points-detail.component';
import { LOADING_POINT } from '../../../../test/data/loading-point';
import { BERN_WYLEREGG } from '../../../../test/data/service-point';
import { UserDetailInfoComponent } from '../../../core/components/base-detail/user-edit-info/user-detail-info.component';

const authService: Partial<AuthService> = {};

describe('LoadingPointsDetailComponent', () => {
  let component: LoadingPointsDetailComponent;
  let fixture: ComponentFixture<LoadingPointsDetailComponent>;
  let router: Router;

  const servicePointService = jasmine.createSpyObj(['getServicePointVersions']);
  servicePointService.getServicePointVersions.and.returnValue(of([BERN_WYLEREGG]));
  const loadingPointService = jasmine.createSpyObj('loadingPointService', [
    'createLoadingPoint',
    'updateLoadingPoint',
  ]);
  loadingPointService.createLoadingPoint.and.returnValue(of(LOADING_POINT[0]));
  loadingPointService.updateLoadingPoint.and.returnValue(of(LOADING_POINT));

  const dialogService = jasmine.createSpyObj('dialogService', ['confirm']);
  dialogService.confirm.and.returnValue(of(true));

  describe('for existing Version', () => {
    beforeEach(() => {
      const activatedRouteMock = {
        data: of({ loadingPoint: LOADING_POINT }),
        snapshot: { params: { servicePointNumber: 8504414 } },
      };
      setupTestBed(activatedRouteMock);
      fixture = TestBed.createComponent(LoadingPointsDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      router = TestBed.inject(Router);
    });

    it('should display validity', () => {
      expect(component.selectedVersion).toBeTruthy();

      expect(component.maxValidity.validFrom).toEqual(new Date('2023-11-01'));
      expect(component.maxValidity.validTo).toEqual(new Date('2099-11-07'));
    });

    it('should init selected servicepoint', () => {
      expect(component.servicePointName).toBeTruthy();
      expect(component.servicePoint).toBeTruthy();
      expect(component.servicePointBusinessOrganisations).toBeTruthy();

      expect(servicePointService.getServicePointVersions).toHaveBeenCalled();
    });

    it('should go back to servicepoint', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      component.backToServicePoint();

      expect(router.navigate).toHaveBeenCalledWith([
        'service-point-directory',
        'service-points',
        8504414,
        'loading-points',
      ]);
    });

    it('should toggle form correctly', () => {
      expect(component.form.enabled).toBeFalse();

      component.toggleEdit();
      expect(component.form.enabled).toBeTrue();

      component.toggleEdit();
      expect(component.form.enabled).toBeFalse();
    });

    it('should update via service', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      component.toggleEdit();
      component.save();

      expect(loadingPointService.updateLoadingPoint).toHaveBeenCalled();
    });
  });

  describe('for new Version', () => {
    beforeEach(() => {
      const activatedRouteMock = {
        data: of({ loadingPoint: [] }),
        snapshot: { params: { servicePointNumber: 8504414 } },
      };
      setupTestBed(activatedRouteMock);
      fixture = TestBed.createComponent(LoadingPointsDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      router = TestBed.inject(Router);
    });

    it('should not display current version', () => {
      expect(component.selectedVersion).toBeFalsy();
    });

    it('should save version', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

      component.form.controls.number.setValue(5);
      component.form.controls.designation.setValue('456');

      component.form.controls.validFrom.setValue(moment(new Date(2000 - 10 - 1)));
      component.form.controls.validTo.setValue(moment(new Date(2099 - 10 - 1)));
      component.save();

      expect(loadingPointService.createLoadingPoint).toHaveBeenCalled();
    });
  });

  function setupTestBed(activatedRoute: ActivatedRouteMockType) {
    TestBed.configureTestingModule({
      declarations: [
        LoadingPointsDetailComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        MockAtlasButtonComponent,
        DateRangeTextComponent,
        TextFieldComponent,
        SelectComponent,
        AtlasLabelFieldComponent,
        SwitchVersionComponent,
        AtlasFieldErrorComponent,
        AtlasSpacerComponent,
        AtlasSlideToggleComponent,
        GeographyComponent,
        DecimalNumberPipe,
        InfoIconComponent,
        RemoveCharsDirective,
        SloidComponent,
        UserDetailInfoComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: ServicePointsService, useValue: servicePointService },
        { provide: LoadingPointsService, useValue: loadingPointService },
        { provide: DialogService, useValue: dialogService },
        SplitServicePointNumberPipe,
        TranslatePipe,
      ],
    }).compileComponents();
  }
});
