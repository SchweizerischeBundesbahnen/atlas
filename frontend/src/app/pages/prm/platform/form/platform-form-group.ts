import { FormControl, FormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';
import {
  BasicAttributeType,
  BoardingDeviceAttributeType,
  BooleanOptionalAttributeType,
  InfoOpportunityAttributeType,
  ReadPlatformVersion,
  VehicleAccessAttributeType,
} from '../../../../api';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';

export interface PlatformFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  additionalInformation: FormControl<string | null | undefined>;
}

export interface ReducedPlatformFormGroup extends PlatformFormGroup {
  height: FormControl<number | null | undefined>;
  inclinationLongitudinal: FormControl<number | null | undefined>;
  infoOpportunities: FormControl<Array<InfoOpportunityAttributeType> | null | undefined>;
  partialElevation: FormControl<boolean | null | undefined>;
  tactileSystem: FormControl<BooleanOptionalAttributeType | null | undefined>;
  vehicleAccess: FormControl<VehicleAccessAttributeType | null | undefined>;
  wheelchairAreaLength: FormControl<number | null | undefined>;
  wheelchairAreaWidth: FormControl<number | null | undefined>;
}

export interface CompletePlatformFormGroup extends PlatformFormGroup {
  boardingDevice: FormControl<BoardingDeviceAttributeType | null | undefined>;
  adviceAccessInfo: FormControl<string | null | undefined>;
  contrastingAreas: FormControl<BooleanOptionalAttributeType | null | undefined>;
  dynamicAudio: FormControl<BasicAttributeType | null | undefined>;
  dynamicVisual: FormControl<BasicAttributeType | null | undefined>;
  inclination: FormControl<number | null | undefined>;
  inclinationWidth: FormControl<number | null | undefined>;
  levelAccessWheelchair: FormControl<BasicAttributeType | null | undefined>;
  superelevation: FormControl<number | null | undefined>;
}

export class PlatformFormGroupBuilder {
  public static buildCompleteFormGroup(version?: ReadPlatformVersion) {
    return new FormGroup<CompletePlatformFormGroup>(
      {
        sloid: new FormControl(version?.sloid),
        additionalInformation: new FormControl(version?.additionalInformation, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        boardingDevice: new FormControl(
          version?.boardingDevice ?? BoardingDeviceAttributeType.ToBeCompleted,
          [Validators.required],
        ),
        adviceAccessInfo: new FormControl(version?.adviceAccessInfo, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        contrastingAreas: new FormControl(
          version?.contrastingAreas ?? BooleanOptionalAttributeType.ToBeCompleted,
          [Validators.required],
        ),
        dynamicAudio: new FormControl(
          version?.dynamicAudio ?? BooleanOptionalAttributeType.ToBeCompleted,
          [Validators.required],
        ),
        dynamicVisual: new FormControl(
          version?.dynamicVisual ?? BooleanOptionalAttributeType.ToBeCompleted,
          [Validators.required],
        ),
        inclination: new FormControl(version?.inclination, [
          AtlasCharsetsValidator.decimalWithDigits(3),
        ]),
        inclinationWidth: new FormControl(version?.inclinationWidth, [
          AtlasCharsetsValidator.decimalWithDigits(3),
        ]),
        levelAccessWheelchair: new FormControl(
          version?.levelAccessWheelchair ?? BooleanOptionalAttributeType.ToBeCompleted,
          [Validators.required],
        ),
        superelevation: new FormControl(version?.superelevation, [
          AtlasCharsetsValidator.decimalWithDigits(3),
        ]),
        validFrom: new FormControl(version?.validFrom ? moment(version.validFrom) : null, [
          Validators.required,
        ]),
        validTo: new FormControl(version?.validTo ? moment(version.validTo) : null, [
          Validators.required,
        ]),
        etagVersion: new FormControl(version?.etagVersion),
        creationDate: new FormControl(version?.creationDate),
        editionDate: new FormControl(version?.editionDate),
        editor: new FormControl(version?.editor),
        creator: new FormControl(version?.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
  }

  public static buildReducedFormGroup(version?: ReadPlatformVersion) {
    return new FormGroup<ReducedPlatformFormGroup>(
      {
        sloid: new FormControl(version?.sloid),
        additionalInformation: new FormControl(version?.additionalInformation, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        height: new FormControl(version?.height, [AtlasCharsetsValidator.decimalWithDigits(3)]),
        inclinationLongitudinal: new FormControl(version?.inclinationLongitudinal, [
          AtlasCharsetsValidator.decimalWithDigits(3),
        ]),
        infoOpportunities: new FormControl(
          version?.infoOpportunities ?? [InfoOpportunityAttributeType.ToBeCompleted],
        ),
        partialElevation: new FormControl(version?.partialElevation),
        tactileSystem: new FormControl(
          version?.tactileSystem ?? BooleanOptionalAttributeType.ToBeCompleted,
          [Validators.required],
        ),
        vehicleAccess: new FormControl(
          version?.vehicleAccess ?? VehicleAccessAttributeType.ToBeCompleted,
          [Validators.required],
        ),
        wheelchairAreaLength: new FormControl(version?.wheelchairAreaLength, [
          AtlasCharsetsValidator.decimalWithDigits(3),
        ]),
        wheelchairAreaWidth: new FormControl(version?.wheelchairAreaWidth, [
          AtlasCharsetsValidator.decimalWithDigits(3),
        ]),
        validFrom: new FormControl(version?.validFrom ? moment(version.validFrom) : null, [
          Validators.required,
        ]),
        validTo: new FormControl(version?.validTo ? moment(version.validTo) : null, [
          Validators.required,
        ]),
        etagVersion: new FormControl(version?.etagVersion),
        creationDate: new FormControl(version?.creationDate),
        editionDate: new FormControl(version?.editionDate),
        editor: new FormControl(version?.editor),
        creator: new FormControl(version?.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
  }
}
