import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ParkingLotDetailComponent} from './parking-lot-detail.component';
import {AuthService} from '../../../../../core/auth/auth.service';
import {of} from 'rxjs';
import {DialogService} from '../../../../../core/components/dialog/dialog.service';
import {BERN_WYLEREGG} from '../../../../../../test/data/service-point';
import {MockAtlasButtonComponent, MockAtlasFieldErrorComponent,} from '../../../../../app.testing.mocks';
import {DisplayDatePipe} from '../../../../../core/pipe/display-date.pipe';
import {TextFieldComponent} from '../../../../../core/form-components/text-field/text-field.component';
import {AtlasLabelFieldComponent} from '../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import {AtlasSpacerComponent} from '../../../../../core/components/spacer/atlas-spacer.component';
import {InfoIconComponent} from '../../../../../core/form-components/info-icon/info-icon.component';
import {SelectComponent} from '../../../../../core/form-components/select/select.component';
import {CommentComponent} from '../../../../../core/form-components/comment/comment.component';
import {DateRangeTextComponent} from '../../../../../core/versioning/date-range-text/date-range-text.component';
import {SwitchVersionComponent} from '../../../../../core/components/switch-version/switch-version.component';
import {DateRangeComponent} from '../../../../../core/form-components/date-range/date-range.component';
import {DateIconComponent} from '../../../../../core/form-components/date-icon/date-icon.component';
import {AppTestingModule} from '../../../../../app.testing.module';
import {ActivatedRoute} from '@angular/router';
import {NotificationService} from '../../../../../core/notification/notification.service';
import {BooleanOptionalAttributeType, PersonWithReducedMobilityService, ReadParkingLotVersion,} from '../../../../../api';
import {TranslatePipe} from '@ngx-translate/core';
import {SplitServicePointNumberPipe} from '../../../../../core/search-service-point/split-service-point-number.pipe';
import moment from 'moment';
import {UserDetailInfoComponent} from '../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import {SloidComponent} from '../../../../../core/form-components/sloid/sloid.component';
import {AtlasSlideToggleComponent} from '../../../../../core/form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import {ParkingLotFormComponent} from "./form/parking-lot-form/parking-lot-form.component";
import SpyObj = jasmine.SpyObj;

const parkingLot: ReadParkingLotVersion[] = [
  {
    creationDate: '2024-01-22T13:52:30.598026',
    creator: 'e524381',
    editionDate: '2024-01-22T13:52:30.598026',
    editor: 'e524381',
    id: 1000,
    sloid: 'ch:1:sloid:12345:1',
    validFrom: new Date('2000-01-01'),
    validTo: new Date('2000-12-31'),
    etagVersion: 0,
    parentServicePointSloid: 'ch:1:sloid:7000',
    designation: 'designation',
    additionalInformation: 'additional',
    placesAvailable: BooleanOptionalAttributeType.ToBeCompleted,
    prmPlacesAvailable: BooleanOptionalAttributeType.ToBeCompleted,
    number: {
      number: 8507000,
      numberShort: 7000,
      uicCountryCode: 85,
      checkDigit: 3,
    },
  },
];

const authService: Partial<AuthService> = {
  hasPermissionsToWrite(): boolean {
    return true;
  },
};

describe('ParkingLotDetailComponent', () => {
  let component: ParkingLotDetailComponent;
  let fixture: ComponentFixture<ParkingLotDetailComponent>;

  const personWithReducedMobilityService = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['createParkingLot', 'updateParkingLot'],
  );
  personWithReducedMobilityService.createParkingLot.and.returnValue(of(parkingLot[0]));
  personWithReducedMobilityService.updateParkingLot.and.returnValue(of(parkingLot));

  const notificationService = jasmine.createSpyObj('notificationService', ['success']);
  const dialogService: SpyObj<DialogService> = jasmine.createSpyObj('dialogService', ['confirm']);
  dialogService.confirm.and.returnValue(of(true));

  const activatedRouteMock = {
    snapshot: {
      data: {
        servicePoint: [BERN_WYLEREGG],
        parkingLot: [],
      },
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        ParkingLotDetailComponent,
        SloidComponent,
        AtlasSlideToggleComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        ParkingLotFormComponent,
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
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: NotificationService, useValue: notificationService },
        { provide: PersonWithReducedMobilityService, useValue: personWithReducedMobilityService },
        { provide: DialogService, useValue: dialogService },
        TranslatePipe,
        SplitServicePointNumberPipe,
      ],
    });
  });

  describe('new parking lot', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(ParkingLotDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(component.isNew).toBeTrue();
      expect(component.selectedVersion).toBeUndefined();

      expect(component.form.enabled).toBeTrue();
    });

    it('should create on save', () => {
      component.form.controls.designation.setValue('Haupteingang A');
      component.form.controls.validFrom.setValue(moment('31.10.2000', 'dd.MM.yyyy'));
      component.form.controls.validTo.setValue(moment('31.10.2099', 'dd.MM.yyyy'));

      component.save();

      expect(personWithReducedMobilityService.createParkingLot).toHaveBeenCalled();
      expect(notificationService.success).toHaveBeenCalled();
    });
  });

  describe('edit parking lot', () => {
    beforeEach(() => {
      TestBed.overrideProvider(ActivatedRoute, {
        useValue: {
          snapshot: {
            data: {
              servicePoint: [BERN_WYLEREGG],
              parkingLot: parkingLot,
            },
          },
        },
      });
      fixture = TestBed.createComponent(ParkingLotDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(component.isNew).toBeFalse();
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

      component.form.controls.designation.markAsDirty();

      expect(component.form.dirty).toBeTrue();
      expect(component.isFormDirty()).toBeTrue();

      component.toggleEdit();
      expect(component.form.enabled).toBeFalse();
    });

    it('should update', () => {
      component.toggleEdit();

      component.form.controls.designation.setValue('new designation');
      component.form.controls.designation.markAsDirty();

      component.save();
      expect(personWithReducedMobilityService.updateParkingLot).toHaveBeenCalled();
      expect(notificationService.success).toHaveBeenCalled();
    });
  });
});
