import { BaseDetailFormGroup } from '../../../../../core/components/base-detail/base-detail-form-group';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import moment from 'moment';
import { WhitespaceValidator } from '../../../../../core/validation/whitespace/whitespace-validator';
import { DateRangeValidator } from '../../../../../core/validation/date-range/date-range-validator';
import { PrmMeanOfTransportHelper } from '../../../util/prm-mean-of-transport-helper';
import { PrmMeanOfTransportValidator } from '../create-stop-point/prm-mean-of-transport-validator';
import {
  BooleanOptionalAttributeType,
  MeanOfTransport,
  ReadStopPointVersion,
  StandardAttributeType,
  StopPointVersion,
} from '../../../../../api';

export interface StopPointDetailFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  meansOfTransport: FormControl<Array<MeanOfTransport> | null | undefined>;
  freeText: FormControl<string | null | undefined>;
  address: FormControl<string | null | undefined>;
  zipCode: FormControl<string | null | undefined>;
  city: FormControl<string | null | undefined>;
  alternativeTransport: FormControl<StandardAttributeType | null | undefined>;
  shuttleService: FormControl<StandardAttributeType | null | undefined>;
  alternativeTransportCondition: FormControl<string | null | undefined>;
  assistanceAvailability: FormControl<StandardAttributeType | null | undefined>;
  assistanceCondition: FormControl<string | null | undefined>;
  assistanceService: FormControl<StandardAttributeType | null | undefined>;
  audioTicketMachine: FormControl<StandardAttributeType | null | undefined>;
  additionalInformation: FormControl<string | null | undefined>;
  dynamicAudioSystem: FormControl<StandardAttributeType | null | undefined>;
  dynamicOpticSystem: FormControl<StandardAttributeType | null | undefined>;
  infoTicketMachine: FormControl<string | null | undefined>;
  interoperable: FormControl<boolean | null | undefined>;
  url: FormControl<string | null | undefined>;
  visualInfo: FormControl<StandardAttributeType | null | undefined>;
  wheelchairTicketMachine: FormControl<
    StandardAttributeType | null | undefined
  >;
  assistanceRequestFulfilled: FormControl<
    BooleanOptionalAttributeType | null | undefined
  >;
  ticketMachine: FormControl<BooleanOptionalAttributeType | null | undefined>;
  number: FormControl<number | null | undefined>;
}

export interface ReducedStopPointDetailFormGroup extends BaseDetailFormGroup {
  number: FormControl<number | null | undefined>;
  sloid: FormControl<string | null | undefined>;
  meansOfTransport: FormControl<Array<MeanOfTransport> | null | undefined>;
  freeText: FormControl<string | null | undefined>;
}

export interface MeanOfTransportFormGroup {
  meansOfTransport: FormControl<Array<MeanOfTransport> | null | undefined>;
}

export class StopPointFormGroupBuilder {
  static buildFormGroup(version: ReadStopPointVersion): FormGroup {
    if (version.reduced) {
      return this.buildReducedFormGroup(version);
    }
    return this.buildCompleteFormGroup(version);
  }

  private static buildCompleteFormGroup(version: ReadStopPointVersion) {
    return new FormGroup<StopPointDetailFormGroup>(
      {
        number: new FormControl(version.number.number),
        sloid: new FormControl(version.sloid),
        meansOfTransport: new FormControl(version.meansOfTransport, [
          Validators.required,
          PrmMeanOfTransportValidator.isReducedOrComplete,
        ]),
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
        alternativeTransport: new FormControl(version.alternativeTransport, [
          Validators.required,
        ]),
        shuttleService: new FormControl(version.shuttleService, [
          Validators.required,
        ]),
        alternativeTransportCondition: new FormControl(
          version.alternativeTransportCondition,
          [
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            Validators.maxLength(2000),
          ]
        ),
        assistanceAvailability: new FormControl(
          version.assistanceAvailability,
          [Validators.required]
        ),
        assistanceCondition: new FormControl(version.assistanceCondition, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        assistanceService: new FormControl(version.assistanceService, [
          Validators.required,
        ]),
        audioTicketMachine: new FormControl(version.audioTicketMachine, [
          Validators.required,
        ]),
        additionalInformation: new FormControl(version.additionalInformation, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        dynamicAudioSystem: new FormControl(version.dynamicAudioSystem, [
          Validators.required,
        ]),
        dynamicOpticSystem: new FormControl(version.dynamicOpticSystem, [
          Validators.required,
        ]),
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
        wheelchairTicketMachine: new FormControl(
          version.wheelchairTicketMachine,
          [Validators.required]
        ),
        assistanceRequestFulfilled: new FormControl(
          version.assistanceRequestFulfilled,
          [Validators.required]
        ),
        ticketMachine: new FormControl(version.ticketMachine, [
          Validators.required,
        ]),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required]
        ),
        validTo: new FormControl(
          version.validTo ? moment(version.validTo) : version.validTo,
          [Validators.required]
        ),
        etagVersion: new FormControl(version.etagVersion),
        creationDate: new FormControl(version.creationDate),
        editionDate: new FormControl(version.editionDate),
        editor: new FormControl(version.editor),
        creator: new FormControl(version.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }

  private static buildReducedFormGroup(version: ReadStopPointVersion) {
    return new FormGroup<ReducedStopPointDetailFormGroup>(
      {
        number: new FormControl(version.number.number),
        sloid: new FormControl(version.sloid),
        meansOfTransport: new FormControl(version.meansOfTransport, [
          Validators.required,
          PrmMeanOfTransportValidator.isReducedOrComplete,
        ]),
        freeText: new FormControl(version.freeText, [
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          Validators.maxLength(2000),
        ]),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required]
        ),
        validTo: new FormControl(
          version.validTo ? moment(version.validTo) : version.validTo,
          [Validators.required]
        ),
        etagVersion: new FormControl(version.etagVersion),
        creationDate: new FormControl(version.creationDate),
        editionDate: new FormControl(version.editionDate),
        editor: new FormControl(version.editor),
        creator: new FormControl(version.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }

  static buildEmptyWithReducedValidationFormGroup(): FormGroup {
    return new FormGroup<StopPointDetailFormGroup>(
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
        shuttleService: new FormControl(null),
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
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }

  static getWritableStopPoint(
    form: FormGroup<StopPointDetailFormGroup>
  ): StopPointVersion {
    const value = form.value;
    const isReduced = PrmMeanOfTransportHelper.isReduced(
      value.meansOfTransport!
    );
    if (isReduced) {
      return this.getWritableReducedStopPoint(form);
    }
    return this.getWritableCompleteStopPoint(form);
  }

  private static getWritableCompleteStopPoint(
    form: FormGroup<StopPointDetailFormGroup>
  ) {
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
      shuttleService: value.shuttleService!,
      alternativeTransportCondition: value.alternativeTransportCondition!,
      assistanceAvailability: value.assistanceAvailability!,
      assistanceCondition: value.assistanceCondition!,
      assistanceRequestFulfilled: value.assistanceRequestFulfilled!,
      assistanceService: value.assistanceService!,
      audioTicketMachine: value.audioTicketMachine!,
      dynamicAudioSystem: value.dynamicAudioSystem!,
      dynamicOpticSystem: value.dynamicOpticSystem!,
      infoTicketMachine: value.infoTicketMachine!,
      interoperable: value.interoperable ?? false,
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

  private static getWritableReducedStopPoint(
    form: FormGroup<StopPointDetailFormGroup>
  ) {
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
        [Validators.required, PrmMeanOfTransportValidator.isReducedOrComplete]
      ),
    });
  }

  static addCompleteRecordingValidation(
    form: FormGroup<StopPointDetailFormGroup>
  ) {
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
    form.controls.shuttleService.addValidators([Validators.required]);
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
    form.controls.assistanceRequestFulfilled.addValidators([
      Validators.required,
    ]);
    form.controls.ticketMachine.addValidators([Validators.required]);
  }

  static removeCompleteRecordingValidation(
    form: FormGroup<StopPointDetailFormGroup>
  ) {
    const completeRecordingValidation = [
      form.controls.address,
      form.controls.zipCode,
      form.controls.city,
      form.controls.alternativeTransport,
      form.controls.shuttleService,
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
    completeRecordingValidation.forEach((control) => {
      if (control) {
        control.clearValidators();
      }
    });
  }

  static populateDropdownsForCompleteWithDefaultValue(
    form: FormGroup<StopPointDetailFormGroup>
  ) {
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
      form.controls.alternativeTransport,
      form.controls.shuttleService,
    ];
    dropdownControlsToPopulateWithDefaultValue.forEach((control) => {
      control.setValue(StandardAttributeType.ToBeCompleted);
    });
  }
}
