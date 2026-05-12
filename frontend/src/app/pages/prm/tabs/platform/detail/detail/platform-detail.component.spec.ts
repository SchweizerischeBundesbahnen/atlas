import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { PlatformDetailComponent } from './platform-detail.component';
import { ReadPlatformVersion, VehicleAccessAttributeType } from '../../../../../../api';
import { of } from 'rxjs';
import { DialogService } from '../../../../../../core/components/dialog/dialog.service';
import { STOP_POINT, STOP_POINT_COMPLETE } from '../../../../util/stop-point-test-data';
import { BERN_WYLEREGG } from '../../../../../../../test/data/service-point';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../../../../test/data/traffic-point-element';
import {
  adminPermissionServiceMock,
  MockAtlasButtonComponent,
  MockAtlasFieldErrorComponent,
  MockNavigationSepodiPrmComponent,
} from '../../../../../../app.testing.mocks';
import { DisplayDatePipe } from '../../../../../../core/pipe/display-date.pipe';
import { PlatformReducedFormComponent } from '../form/platform-reduced-form/platform-reduced-form.component';
import { PlatformCompleteFormComponent } from '../form/platform-complete-form/platform-complete-form.component';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent, InfoIconComponent } from '@atlas/form';
import { AtlasSpacerComponent } from '../../../../../../core/components/spacer/atlas-spacer.component';
import { SelectComponent } from '../../../../../../core/form-components/select/select.component';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { DateRangeTextComponent } from '../../../../../../core/versioning/date-range-text/date-range-text.component';
import { SwitchVersionComponent } from '../../../../../../core/components/switch-version/switch-version.component';
import { DateRangeComponent } from '../../../../../../core/form-components/date-range/date-range.component';
import { DateIconComponent } from '../../../../../../core/form-components/date-icon/date-icon.component';
import { UserDetailInfoComponent } from '../../../../../../core/components/user-edit-info/user-detail-info.component';
import { DetailPageContainerComponent } from '../../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../../core/components/detail-page-content/detail-page-content.component';
import { DetailFooterComponent } from '../../../../../../core/components/detail-footer/detail-footer.component';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { NotificationService } from '../../../../../../core/notification/notification.service';
import { TranslatePipe } from '@ngx-translate/core';
import { SplitServicePointNumberPipe } from '../../../../../../core/search-service-point/split-service-point-number.pipe';
import moment from 'moment';
import { PermissionService } from '../../../../../../core/auth/permission/permission.service';
import { PlatformService } from '../../../../../../api/service/prm/platform/platform.service';
import {
  WheelchairAccessibilityInternalService
} from '../../../../../../api/service/prm/wheelchair-accessibility/wheelchair-accessibility-internal.service';

const reducedPlatform: ReadPlatformVersion[] = [
  {
    creationDate: '2024-01-11T10:08:28.446803',
    creator: 'u123456',
    editionDate: '2024-01-11T10:08:28.446803',
    editor: 'u123456',
    id: 1002,
    sloid: 'ch:1:sloid:7000:0:100000',
    validFrom: new Date('2024-01-01'),
    validTo: new Date('2024-01-03'),
    etagVersion: 8,
    parentServicePointSloid: 'ch:1:sloid:7000',
    boardingDevice: 'TO_BE_COMPLETED',
    adviceAccessInfo: undefined,
    additionalInformation: undefined,
    contrastingAreas: 'YES',
    dynamicAudio: 'TO_BE_COMPLETED',
    dynamicVisual: 'TO_BE_COMPLETED',
    height: undefined,
    inclination: undefined,
    inclinationLongitudinal: undefined,
    inclinationWidth: undefined,
    infoOpportunities: [],
    levelAccessWheelchair: 'TO_BE_COMPLETED',
    partialElevation: undefined,
    superelevation: undefined,
    tactileSystem: undefined,
    vehicleAccess: undefined,
    wheelchairAreaLength: undefined,
    wheelchairAreaWidth: undefined,
    number: {
      number: 8507000,
      checkDigit: 3,
      numberShort: 7000,
      uicCountryCode: 85,
    },
  },
  {
    creationDate: '2024-01-11T10:08:28.446803',
    creator: 'u123456',
    editionDate: '2024-01-11T10:08:28.446803',
    editor: 'u123456',
    id: 1003,
    sloid: 'ch:1:sloid:7000:0:100000',
    validFrom: new Date('2024-01-04'),
    validTo: new Date('2024-01-10'),
    etagVersion: 8,
    parentServicePointSloid: 'ch:1:sloid:7000',
    boardingDevice: 'LIFTS',
    adviceAccessInfo: undefined,
    additionalInformation: undefined,
    contrastingAreas: 'YES',
    dynamicAudio: 'TO_BE_COMPLETED',
    dynamicVisual: 'TO_BE_COMPLETED',
    height: undefined,
    inclination: undefined,
    inclinationLongitudinal: undefined,
    inclinationWidth: undefined,
    infoOpportunities: [],
    levelAccessWheelchair: 'TO_BE_COMPLETED',
    partialElevation: undefined,
    superelevation: undefined,
    tactileSystem: undefined,
    vehicleAccess: undefined,
    wheelchairAreaLength: undefined,
    wheelchairAreaWidth: undefined,
    number: {
      number: 8507000,
      checkDigit: 3,
      numberShort: 7000,
      uicCountryCode: 85,
    },
  },
];

describe('PlatformDetailComponent', () => {
  let component: PlatformDetailComponent;
  let fixture: ComponentFixture<PlatformDetailComponent>;
  let platformService: Mocked<Pick<PlatformService, 'createPlatform' | 'updatePlatform'>>;
  let routerSpy: Mocked<Pick<Router, 'navigate'>>;
  let notificationService: Mocked<Pick<NotificationService, 'success'>>;
  let dialogService: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

  const activatedRouteMock = {
    parent: {
      data: of({
        stopPoint: [STOP_POINT],
        servicePoint: [BERN_WYLEREGG],
        platform: [],
        trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]],
      }),
    },
  };

  beforeEach(() => {
    Element.prototype.scrollIntoView = vi.fn();

    platformService = {
      createPlatform: vi.fn(),
      updatePlatform: vi.fn(),
    };
    platformService.createPlatform.mockReturnValue(of(reducedPlatform[0]));
    platformService.updatePlatform.mockReturnValue(of(reducedPlatform[0]));

    routerSpy = {
      navigate: vi.fn(),
    };
    routerSpy.navigate.mockResolvedValue(true);

    notificationService = {
      success: vi.fn(),
    };

    dialogService = {
      openDialogDataWithConfirmationResult: vi.fn(),
    };
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        RouterModule.forRoot([
          {
            path: ':sloid',
            redirectTo: '',
          },
        ]),
        PlatformDetailComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        PlatformReducedFormComponent,
        PlatformCompleteFormComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        MockAtlasFieldErrorComponent,
        AtlasSpacerComponent,
        InfoIconComponent,
        SelectComponent,
        CommentComponent,
        DateRangeTextComponent,
        SwitchVersionComponent,
        DateRangeComponent,
        DateIconComponent,
        UserDetailInfoComponent,
        DetailPageContainerComponent,
        DetailPageContentComponent,
        DetailFooterComponent,
        MockNavigationSepodiPrmComponent,
      ],
      providers: [
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: NotificationService, useValue: notificationService },
        {
          provide: PlatformService,
          useValue: platformService,
        },
        { provide: DialogService, useValue: dialogService },
        { provide: Router, useValue: routerSpy },
        {
          provide: WheelchairAccessibilityInternalService,
          useValue: { getPlatformAccessibilityToday: () => of({}) },
        },
        TranslatePipe,
        SplitServicePointNumberPipe,
      ],
    });
  });

  describe('new reduced platform', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(PlatformDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(component.isNew).toBe(true);
      expect(component.reduced).toBe(true);
      expect(component.selectedVersion).toBeUndefined();

      expect(component.form.enabled).toBe(true);
    });

    it('should create on save', () => {
      component.form.controls.validFrom.setValue(moment('31.10.2000', 'dd.MM.yyyy'));
      component.form.controls.validTo.setValue(moment('31.10.2099', 'dd.MM.yyyy'));

      component.save();

      expect(platformService.createPlatform).toHaveBeenCalled();
      expect(notificationService.success).toHaveBeenCalled();
    });
  });

  describe('edit reduced platform', () => {
    beforeEach(() => {
      TestBed.overrideProvider(ActivatedRoute, {
        useValue: {
          parent: {
            data: of({
              stopPoint: [STOP_POINT],
              servicePoint: [BERN_WYLEREGG],
              platform: reducedPlatform,
              trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]],
            }),
          },
        },
      });
      fixture = TestBed.createComponent(PlatformDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(component.isNew).toBe(false);
      expect(component.reduced).toBe(true);
      expect(component.selectedVersion).toBeDefined();

      expect(component.form.enabled).toBe(false);
      expect(component.showVersionSwitch).toBe(true);

      component.switchVersion(0);
      expect(component.selectedVersionIndex).toBe(0);
    });

    it('should toggle form', () => {
      expect(component.form.enabled).toBe(false);

      component.toggleEdit();
      expect(component.form.enabled).toBe(true);
      expect(component.form.dirty).toBe(false);

      component.reducedForm.controls.vehicleAccess.setValue(
        VehicleAccessAttributeType.PlatformAccessWithAssistanceWhenNotified
      );
      component.reducedForm.controls.vehicleAccess.markAsDirty();
      component.reducedForm.markAsDirty();

      expect(component.form.dirty).toBe(true);

      component.toggleEdit();
      expect(component.form.enabled).toBe(false);
    });

    it('should update', () => {
      component.toggleEdit();

      component.reducedForm.controls.vehicleAccess.setValue(
        VehicleAccessAttributeType.PlatformAccessWithAssistanceWhenNotified
      );
      component.reducedForm.controls.vehicleAccess.markAsDirty();

      component.save();
      expect(platformService.updatePlatform).toHaveBeenCalled();
      expect(notificationService.success).toHaveBeenCalled();
    });
  });

  describe('create complete platform', () => {
    beforeEach(() => {
      TestBed.overrideProvider(ActivatedRoute, {
        useValue: {
          parent: {
            data: of({
              stopPoint: [STOP_POINT_COMPLETE],
              servicePoint: [BERN_WYLEREGG],
              platform: [],
              trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]],
            }),
          },
        },
      });
      fixture = TestBed.createComponent(PlatformDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(component.isNew).toBe(true);
      expect(component.reduced).toBe(false);

      expect(component.form.enabled).toBe(true);
    });

    it('should create complete platform', () => {
      component.form.controls.validFrom.setValue(moment('31.10.2000', 'dd.MM.yyyy'));
      component.form.controls.validTo.setValue(moment('31.10.2099', 'dd.MM.yyyy'));

      component.save();
      expect(platformService.createPlatform).toHaveBeenCalled();
      expect(notificationService.success).toHaveBeenCalled();
    });
  });
});
