import {BaseDetailFormGroup} from "../../../../core/components/base-detail/base-detail-form-group";
import {MeanOfTransport, ReadStopPointVersion, StandardAttributeType} from "../../../../api";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import moment from 'moment';

export interface StopPointDetailFormGroup extends BaseDetailFormGroup {
  sloid: FormControl<string | null | undefined>;
  meansOfTransport: FormControl<Array<MeanOfTransport> | null | undefined>;
  freeText: FormControl<string | null | undefined>;
  address: FormControl<string | null | undefined>;
  zipCode: FormControl<string | null | undefined>;
  city: FormControl<string | null | undefined>;
  alternativeTransport: FormControl<StandardAttributeType | null | undefined>;
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
  wheelchairTicketMachine: FormControl<StandardAttributeType | null | undefined>;
  assistanceRequestFulfilled: FormControl<StandardAttributeType | null | undefined>;
  ticketMachine: FormControl<StandardAttributeType | null | undefined>;
  number: FormControl<number | null | undefined>;
}

export class StopPointFormGroupBuilder {

  static buildFormGroup(version: ReadStopPointVersion): FormGroup {
    const formGroup = new FormGroup<StopPointDetailFormGroup>(
      {
        number: new FormControl(version.number.numberShort),
        sloid: new FormControl(version.sloid),
        meansOfTransport: new FormControl(version.meansOfTransport),
        freeText: new FormControl(version.freeText),
        address: new FormControl(version.address),
        zipCode: new FormControl(version.zipCode),
        city: new FormControl(version.city),
        alternativeTransport: new FormControl(version.alternativeTransport),
        alternativeTransportCondition: new FormControl(version.alternativeTransportCondition),
        assistanceAvailability: new FormControl(version.assistanceAvailability),
        assistanceCondition: new FormControl(version.assistanceCondition),
        assistanceService: new FormControl(version.assistanceService),
        audioTicketMachine: new FormControl(version.audioTicketMachine),
        additionalInformation: new FormControl(version.additionalInformation),
        dynamicAudioSystem: new FormControl(version.dynamicAudioSystem),
        dynamicOpticSystem: new FormControl(version.dynamicOpticSystem),
        infoTicketMachine: new FormControl(version.infoTicketMachine),
        interoperable: new FormControl(version.interoperable),
        url: new FormControl(version.url),
        visualInfo: new FormControl(version.visualInfo),
        wheelchairTicketMachine: new FormControl(version.wheelchairTicketMachine),
        assistanceRequestFulfilled: new FormControl(version.assistanceRequestFulfilled),
        ticketMachine: new FormControl(version.ticketMachine),
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
      }
    )
    return formGroup;
  }
}
