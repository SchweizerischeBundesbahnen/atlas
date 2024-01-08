import { FormControl, FormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { PrmMeanOfTransportHelper } from '../../util/prm-mean-of-transport-helper';
import { PrmMeanOfTransportValidator } from '../../tabs/stop-point/create-stop-point/prm-mean-of-transport-validator';
import { BaseDetailFormGroup } from '../../../../core/components/base-detail/base-detail-form-group';
import {
  BasicAttributeType,
  BoardingDeviceAttributeType,
  BooleanOptionalAttributeType,
  CreateStopPointVersion,
  InfoOpportunityAttributeType,
  ReadPlatformVersion,
  StandardAttributeType,
  VehicleAccessAttributeType,
} from '../../../../api';
import { MeanOfTransportFormGroup } from '../../tabs/stop-point/form/stop-point-detail-form-group';

export interface PlatformFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  additionalInformation: FormControl<string | null | undefined>;
}

export interface ReducedPlatformFormGroup extends BaseDetailFormGroup {
  height: FormControl<number | null | undefined>;
  inclinationLongitudinal: FormControl<number | null | undefined>;
  infoOpportunities: FormControl<Array<InfoOpportunityAttributeType> | null | undefined>;
  partialElevation: FormControl<boolean | null | undefined>;
  tactileSystem: FormControl<BooleanOptionalAttributeType | null | undefined>;
  vehicleAccess: FormControl<VehicleAccessAttributeType | null | undefined>;
  wheelchairAreaLength: FormControl<number | null | undefined>;
  wheelchairAreaWidth: FormControl<number | null | undefined>;
}

export interface CompletePlatformFormGroup extends BaseDetailFormGroup {
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
  public static buildCompleteFormGroup(version: ReadPlatformVersion) {
    return new FormGroup<PlatformFormGroup>(
      {
        number: new FormControl(version.number.number),
        sloid: new FormControl(version.sloid),
        meansOfTransport: new FormControl(version.meansOfTransport),
        freeText: new FormControl(version.freeText, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        address: new FormControl(version.address, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        zipCode: new FormControl(version.zipCode, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(50),
        ]),
        city: new FormControl(version.city, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(75),
        ]),
        alternativeTransport: new FormControl(version.alternativeTransport, [Validators.required]),
        alternativeTransportCondition: new FormControl(version.alternativeTransportCondition, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        assistanceAvailability: new FormControl(version.assistanceAvailability, [
          Validators.required,
        ]),
        assistanceCondition: new FormControl(version.assistanceCondition, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        assistanceService: new FormControl(version.assistanceService, [Validators.required]),
        audioTicketMachine: new FormControl(version.audioTicketMachine, [Validators.required]),
        additionalInformation: new FormControl(version.additionalInformation, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        dynamicAudioSystem: new FormControl(version.dynamicAudioSystem, [Validators.required]),
        dynamicOpticSystem: new FormControl(version.dynamicOpticSystem, [Validators.required]),
        infoTicketMachine: new FormControl(version.infoTicketMachine, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        interoperable: new FormControl(version.interoperable),
        url: new FormControl(version.url, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(500),
        ]),
        visualInfo: new FormControl(version.visualInfo, [Validators.required]),
        wheelchairTicketMachine: new FormControl(version.wheelchairTicketMachine, [
          Validators.required,
        ]),
        assistanceRequestFulfilled: new FormControl(version.assistanceRequestFulfilled, [
          Validators.required,
        ]),
        ticketMachine: new FormControl(version.ticketMachine, [Validators.required]),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ),
        validTo: new FormControl(version.validTo ? moment(version.validTo) : version.validTo, [
          Validators.required,
        ]),
        etagVersion: new FormControl(version.etagVersion),
        creationDate: new FormControl(version.creationDate),
        editionDate: new FormControl(version.editionDate),
        editor: new FormControl(version.editor),
        creator: new FormControl(version.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
  }

  public static buildReducedFormGroup(version: ReadPlatformVersion) {
    return new FormGroup<ReducedPlatformFormGroup>(
      {
        number: new FormControl(version.number.number),
        sloid: new FormControl(version.sloid),
        meansOfTransport: new FormControl(version.meansOfTransport, [Validators.required]),
        freeText: new FormControl(version.freeText, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ),
        validTo: new FormControl(version.validTo ? moment(version.validTo) : version.validTo, [
          Validators.required,
        ]),
        etagVersion: new FormControl(version.etagVersion),
        creationDate: new FormControl(version.creationDate),
        editionDate: new FormControl(version.editionDate),
        editor: new FormControl(version.editor),
        creator: new FormControl(version.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
  }

  static buildEmptyWithReducedValidationFormGroup(): FormGroup {
    return new FormGroup<PlatformFormGroup>(
      {
        number: new FormControl(null),
        sloid: new FormControl(null),
        meansOfTransport: new FormControl(null, [Validators.required]),
        freeText: new FormControl(null, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        address: new FormControl(),
        zipCode: new FormControl(null),
        city: new FormControl(null),
        alternativeTransport: new FormControl(null),
        alternativeTransportCondition: new FormControl(null),
        assistanceAvailability: new FormControl(null),
        assistanceCondition: new FormControl(null),
        assistanceService: new FormControl(null),
        audioTicketMachine: new FormControl(null),
        additionalInformation: new FormControl(null),
        dynamicAudioSystem: new FormControl(null),
        dynamicOpticSystem: new FormControl(null),
        infoTicketMachine: new FormControl(null),
        interoperable: new FormControl(null),
        url: new FormControl(null),
        visualInfo: new FormControl(null),
        wheelchairTicketMachine: new FormControl(null),
        assistanceRequestFulfilled: new FormControl(null),
        ticketMachine: new FormControl(null),
        validFrom: new FormControl(null, [Validators.required]),
        validTo: new FormControl(null, [Validators.required]),
        etagVersion: new FormControl(),
        creationDate: new FormControl(),
        editionDate: new FormControl(),
        editor: new FormControl(),
        creator: new FormControl(),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
    );
  }

  static getWritableStopPoint(form: FormGroup<PlatformFormGroup>): CreateStopPointVersion {
    const value = form.value;
    const isReduced = PrmMeanOfTransportHelper.isReduced(value.meansOfTransport!);
    if (isReduced) {
      return this.getWritableReducedStopPoint(form);
    }
    return this.getWritableCompleteStopPoint(form);
  }

  private static getWritableCompleteStopPoint(form: FormGroup<PlatformFormGroup>) {
    const value = form.value;
    return {
      sloid: value.sloid!,
      freeText: value.freeText!,
      numberWithoutCheckDigit: value.number!,
      meansOfTransport: value.meansOfTransport!,
      city: value.city!,
      address: value.address!,
      zipCode: value.zipCode!,
      url: value.url!,
      additionalInformation: value.additionalInformation!,
      alternativeTransport: value.alternativeTransport!,
      alternativeTransportCondition: value.alternativeTransportCondition!,
      assistanceAvailability: value.assistanceAvailability!,
      assistanceCondition: value.assistanceCondition!,
      assistanceRequestFulfilled: value.assistanceRequestFulfilled!,
      assistanceService: value.assistanceService!,
      audioTicketMachine: value.audioTicketMachine!,
      dynamicAudioSystem: value.dynamicAudioSystem!,
      dynamicOpticSystem: value.dynamicOpticSystem!,
      infoTicketMachine: value.infoTicketMachine!,
      interoperable: value.interoperable!,
      ticketMachine: value.ticketMachine!,
      visualInfo: value.visualInfo!,
      wheelchairTicketMachine: value.wheelchairTicketMachine!,
      validFrom: value.validFrom!.toDate(),
      validTo: value.validTo!.toDate(),
      etagVersion: value.etagVersion!,
      creationDate: value.creationDate!,
      editionDate: value.editionDate!,
      editor: value.editor!,
      creator: value.creator!,
    };
  }

  private static getWritableReducedStopPoint(form: FormGroup<PlatformFormGroup>) {
    const value = form.value;
    return {
      sloid: value.sloid!,
      freeText: value.freeText!,
      numberWithoutCheckDigit: value.number!,
      meansOfTransport: value.meansOfTransport!,
      validFrom: value.validFrom!.toDate(),
      validTo: value.validTo!.toDate(),
      etagVersion: value.etagVersion!,
      creationDate: value.creationDate!,
      editionDate: value.editionDate!,
      editor: value.editor!,
      creator: value.creator!,
    };
  }

  static buildMeansOfTransportForm() {
    return new FormGroup<MeanOfTransportFormGroup>({
      meansOfTransport: new FormControl(
        [],
        [Validators.required, PrmMeanOfTransportValidator.isReducedOrComplete],
      ),
    });
  }

  static addCompleteRecordingValidation(form: FormGroup<PlatformFormGroup>) {
    form.controls.address.addValidators([
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
      Validators.maxLength(2000),
    ]);
    form.controls.zipCode.addValidators([
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
      Validators.maxLength(50),
    ]);
    form.controls.city.addValidators([
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
      Validators.maxLength(75),
    ]);
    form.controls.alternativeTransport.addValidators([Validators.required]);
    form.controls.alternativeTransportCondition.addValidators([
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
      Validators.maxLength(2000),
    ]);
    form.controls.assistanceAvailability.addValidators([Validators.required]);
    form.controls.assistanceCondition.addValidators([
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
      Validators.maxLength(2000),
    ]);
    form.controls.assistanceService.addValidators([Validators.required]);
    form.controls.audioTicketMachine.addValidators([Validators.required]);
    form.controls.additionalInformation.addValidators([
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
      Validators.maxLength(2000),
    ]);
    form.controls.dynamicAudioSystem.addValidators([Validators.required]);
    form.controls.dynamicOpticSystem.addValidators([Validators.required]);
    form.controls.infoTicketMachine.addValidators([
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
      Validators.maxLength(2000),
    ]);
    form.controls.url.addValidators([
      WhitespaceValidator.blankOrEmptySpaceSurrounding,
      Validators.maxLength(500),
    ]);
    form.controls.visualInfo.addValidators([Validators.required]);
    form.controls.wheelchairTicketMachine.addValidators([Validators.required]);
    form.controls.assistanceRequestFulfilled.addValidators([Validators.required]);
    form.controls.ticketMachine.addValidators([Validators.required]);
  }

  static removeCompleteRecordingValidation(form: FormGroup<PlatformFormGroup>) {
    const completeRecordingValidation = [
      form.controls.address,
      form.controls.zipCode,
      form.controls.city,
      form.controls.alternativeTransport,
      form.controls.alternativeTransportCondition,
      form.controls.assistanceAvailability,
      form.controls.assistanceCondition,
      form.controls.assistanceService,
      form.controls.audioTicketMachine,
      form.controls.additionalInformation,
      form.controls.dynamicAudioSystem,
      form.controls.dynamicOpticSystem,
      form.controls.infoTicketMachine,
      form.controls.url,
      form.controls.visualInfo,
      form.controls.wheelchairTicketMachine,
      form.controls.assistanceRequestFulfilled,
      form.controls.ticketMachine,
    ];
    completeRecordingValidation.forEach((control) => control.clearValidators());
  }

  static populateDropdownsForCompleteWithDefaultValue(form: FormGroup<PlatformFormGroup>) {
    const dropdownControlsToPopulateWithDefaultValue = [
      form.controls.assistanceAvailability,
      form.controls.assistanceAvailability,
      form.controls.assistanceService,
      form.controls.audioTicketMachine,
      form.controls.dynamicAudioSystem,
      form.controls.dynamicOpticSystem,
      form.controls.visualInfo,
      form.controls.wheelchairTicketMachine,
      form.controls.ticketMachine,
    ];
    dropdownControlsToPopulateWithDefaultValue.forEach((control) => {
      control.setValue(StandardAttributeType.ToBeCompleted);
    });
    form.controls.alternativeTransport.setValue(StandardAttributeType.No);
  }
}
