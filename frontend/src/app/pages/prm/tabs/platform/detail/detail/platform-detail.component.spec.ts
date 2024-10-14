import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PlatformDetailComponent} from './platform-detail.component';
import {PersonWithReducedMobilityService, ReadPlatformVersion, VehicleAccessAttributeType} from "../../../../../../api";
import {of} from "rxjs";
import {DialogService} from "../../../../../../core/components/dialog/dialog.service";
import {STOP_POINT, STOP_POINT_COMPLETE} from "../../../../util/stop-point-test-data.spec";
import {BERN_WYLEREGG} from "../../../../../../../test/data/service-point";
import {BERN_WYLEREGG_TRAFFIC_POINTS} from "../../../../../../../test/data/traffic-point-element";
import {
  adminPermissionServiceMock,
  MockAtlasButtonComponent,
  MockAtlasFieldErrorComponent
} from "../../../../../../app.testing.mocks";
import {DisplayDatePipe} from "../../../../../../core/pipe/display-date.pipe";
import {PlatformReducedFormComponent} from "../form/platform-reduced-form/platform-reduced-form.component";
import {PlatformCompleteFormComponent} from "../form/platform-complete-form/platform-complete-form.component";
import {TextFieldComponent} from "../../../../../../core/form-components/text-field/text-field.component";
import {AtlasLabelFieldComponent} from "../../../../../../core/form-components/atlas-label-field/atlas-label-field.component";
import {AtlasSpacerComponent} from "../../../../../../core/components/spacer/atlas-spacer.component";
import {InfoIconComponent} from "../../../../../../core/form-components/info-icon/info-icon.component";
import {SelectComponent} from "../../../../../../core/form-components/select/select.component";
import {CommentComponent} from "../../../../../../core/form-components/comment/comment.component";
import {DateRangeTextComponent} from "../../../../../../core/versioning/date-range-text/date-range-text.component";
import {SwitchVersionComponent} from "../../../../../../core/components/switch-version/switch-version.component";
import {DateRangeComponent} from "../../../../../../core/form-components/date-range/date-range.component";
import {DateIconComponent} from "../../../../../../core/form-components/date-icon/date-icon.component";
import {UserDetailInfoComponent} from "../../../../../../core/components/base-detail/user-edit-info/user-detail-info.component";
import {
  DetailPageContainerComponent
} from "../../../../../../core/components/detail-page-container/detail-page-container.component";
import {DetailPageContentComponent} from "../../../../../../core/components/detail-page-content/detail-page-content.component";
import {DetailFooterComponent} from "../../../../../../core/components/detail-footer/detail-footer.component";
import {AppTestingModule} from "../../../../../../app.testing.module";
import {ActivatedRoute, Router, RouterModule} from "@angular/router";
import {NotificationService} from "../../../../../../core/notification/notification.service";
import {TranslatePipe} from "@ngx-translate/core";
import {SplitServicePointNumberPipe} from "../../../../../../core/search-service-point/split-service-point-number.pipe";
import moment from "moment";
import {PermissionService} from "../../../../../../core/auth/permission/permission.service";
import SpyObj = jasmine.SpyObj;
import {Pages} from "../../../../../pages";

const reducedPlatform: ReadPlatformVersion[] = [
  {
    creationDate: '2024-01-11T10:08:28.446803',
    creator: 'e524381',
    editionDate: '2024-01-11T10:08:28.446803',
    editor: 'e524381',
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
    creator: 'e524381',
    editionDate: '2024-01-11T10:08:28.446803',
    editor: 'e524381',
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

  const personWithReducedMobilityService = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['createPlatform', 'updatePlatform'],
  );
  personWithReducedMobilityService.createPlatform.and.returnValue(of(reducedPlatform[0]));
  personWithReducedMobilityService.updatePlatform.and.returnValue(of(reducedPlatform));
  let routerSpy: SpyObj<Router>;

  const notificationService = jasmine.createSpyObj('notificationService', ['success']);
  const dialogService: SpyObj<DialogService> = jasmine.createSpyObj('dialogService', ['confirm']);
  dialogService.confirm.and.returnValue(of(true));

  const activatedRouteMock = {
    snapshot: {
      parent: {
        data: {
          stopPoint: [STOP_POINT],
          servicePoint: [BERN_WYLEREGG],
          platform: [],
          trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]],
        },
      },
    }
  };

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj(['navigate']);
    routerSpy.navigate.and.returnValue(Promise.resolve(true));

    TestBed.configureTestingModule({
      declarations: [
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
      ],
      imports: [
        AppTestingModule,
        RouterModule.forRoot([{
         path: ':sloid', redirectTo: ''
        }]),
      ],
      providers: [
        {provide: PermissionService, useValue: adminPermissionServiceMock},
        {provide: ActivatedRoute, useValue: activatedRouteMock},
        {provide: NotificationService, useValue: notificationService},
        {provide: PersonWithReducedMobilityService, useValue: personWithReducedMobilityService},
        {provide: DialogService, useValue: dialogService},
        {provide: Router, useValue: routerSpy},
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

      expect(component.isNew).toBeTrue();
      expect(component.reduced).toBeTrue();
      expect(component.selectedVersion).toBeUndefined();

      expect(component.form.enabled).toBeTrue();
    });

    it('should create on save', () => {
      component.form.controls.validFrom.setValue(moment('31.10.2000', 'dd.MM.yyyy'));
      component.form.controls.validTo.setValue(moment('31.10.2099', 'dd.MM.yyyy'));

      component.save();

      expect(personWithReducedMobilityService.createPlatform).toHaveBeenCalled();
      expect(notificationService.success).toHaveBeenCalled();
    });
  });

  describe('edit reduced platform', () => {
    beforeEach(() => {
      TestBed.overrideProvider(ActivatedRoute, {
        useValue: {
          snapshot: {
            parent: {
              data: {
                stopPoint: [STOP_POINT],
                servicePoint: [BERN_WYLEREGG],
                platform: reducedPlatform,
                trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]],
              },
            },
          },
        },
      });
      fixture = TestBed.createComponent(PlatformDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(component.isNew).toBeFalse();
      expect(component.reduced).toBeTrue();
      expect(component.selectedVersion).toBeDefined();

      expect(component.form.enabled).toBeFalse();
      expect(component.showVersionSwitch).toBeTrue();

      component.switchVersion(0);
      expect(component.selectedVersionIndex).toBe(0);
    });

    it('should toggle form', () => {
      expect(component.form.enabled).toBeFalse();

      component.toggleEdit();
      expect(component.form.enabled).toBeTrue();
      expect(component.form.dirty).toBeFalse();

      component.reducedForm.controls.vehicleAccess.setValue(
        VehicleAccessAttributeType.PlatformAccessWithAssistanceWhenNotified,
      );
      component.reducedForm.controls.vehicleAccess.markAsDirty();
      component.reducedForm.markAsDirty();

      expect(component.form.dirty).toBeTrue();

      component.toggleEdit();
      expect(component.form.enabled).toBeFalse();
    });

    it('should update', () => {
      component.toggleEdit();

      component.reducedForm.controls.vehicleAccess.setValue(
        VehicleAccessAttributeType.PlatformAccessWithAssistanceWhenNotified,
      );
      component.reducedForm.controls.vehicleAccess.markAsDirty();

      component.save();
      expect(personWithReducedMobilityService.updatePlatform).toHaveBeenCalled();
      expect(notificationService.success).toHaveBeenCalled();
    });
  });

  describe('create complete platform', () => {
    beforeEach(() => {
      TestBed.overrideProvider(ActivatedRoute, {
        useValue: {
          snapshot: {
            parent: {
              data: {
                stopPoint: [STOP_POINT_COMPLETE],
                servicePoint: [BERN_WYLEREGG],
                platform: [],
                trafficPoint: [BERN_WYLEREGG_TRAFFIC_POINTS[0]],
              },
            },
          },
        },
      });
      fixture = TestBed.createComponent(PlatformDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(component.isNew).toBeTrue();
      expect(component.reduced).toBeFalse();

      expect(component.form.enabled).toBeTrue();
    });

    it('should create complete platform', () => {
      component.form.controls.validFrom.setValue(moment('31.10.2000', 'dd.MM.yyyy'));
      component.form.controls.validTo.setValue(moment('31.10.2099', 'dd.MM.yyyy'));

      component.save();
      expect(personWithReducedMobilityService.createPlatform).toHaveBeenCalled();
      expect(notificationService.success).toHaveBeenCalled();
    });

    it('should navigate to the correct traffic point element URL', () => {
      component.selectedVersion = reducedPlatform[0];

      component.navigateToTrafficPointElement();

      expect(routerSpy.navigate).toHaveBeenCalledWith([
        Pages.SEPODI.path,
        Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path,
        reducedPlatform[0].sloid
      ]);
    });
  });


  describe('navigate back to stoppoint create if stoppoint does not exist', () => {
    beforeEach(() => {
      TestBed.overrideProvider(ActivatedRoute, {
        useValue: {
          snapshot: {
            parent: {
              data: {
                stopPoint: [],
                servicePoint: [BERN_WYLEREGG],
                platform: [],
                trafficPoint: [],
              },
            },
          },
        },
      });
      fixture = TestBed.createComponent(PlatformDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });


    it('navigate to stoppoint create if stoppoint does not exist', () => {
      component.ngOnInit();

      expect(routerSpy.navigate).toHaveBeenCalledWith([
        Pages.PRM.path,
        Pages.STOP_POINTS.path,
        component.servicePoint.sloid,
        Pages.PRM_STOP_POINT_TAB.path
      ]);
    });
  });
});
