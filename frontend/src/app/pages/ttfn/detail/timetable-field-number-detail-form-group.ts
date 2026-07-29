import { MeanOfTransport, TimetableFieldNumberVersion } from '../../../api';
import { TtfnMeanOfTransport } from '../../../api/model/ttfnMeanOfTransport';
import { BaseDetailFormModel } from '../../../core/model/base-detail-form-group';
import moment from 'moment';

export const DESCRIPTION_MAX_LENGTH = 70;
export const NUMBER_MAX_LENGTH = 7;

export interface TimetableFieldNumberDetailFormModel extends BaseDetailFormModel {
  businessOrganisation: string | null;
  number: string;
  descriptionOutwardLine1: string;
  descriptionOutwardLine2: string;
  descriptionOutwardLine3: string;
  descriptionReturnLine1: string;
  descriptionReturnLine2: string;
  descriptionReturnLine3: string;
  meanOfTransport: MeanOfTransport[];
}

export class TimetableFieldNumberDetailForm {
  public static readonly emptyFormValue: TimetableFieldNumberDetailFormModel = {
    businessOrganisation: null,
    number: '',
    descriptionOutwardLine1: '',
    descriptionOutwardLine2: '',
    descriptionOutwardLine3: '',
    descriptionReturnLine1: '',
    descriptionReturnLine2: '',
    descriptionReturnLine3: '',
    meanOfTransport: [],
    validFrom: null,
    validTo: null,
    etagVersion: null,
    creationDate: null,
    editionDate: null,
    editor: null,
    creator: null,
  };

  public static toFormModel(version: TimetableFieldNumberVersion): TimetableFieldNumberDetailFormModel {
    return {
      businessOrganisation: version.businessOrganisation ?? '',
      number: version.number ?? '',
      descriptionOutwardLine1: version.descriptionOutwardLine1 ?? '',
      descriptionOutwardLine2: version.descriptionOutwardLine2 ?? '',
      descriptionOutwardLine3: version.descriptionOutwardLine3 ?? '',
      descriptionReturnLine1: version.descriptionReturnLine1 ?? '',
      descriptionReturnLine2: version.descriptionReturnLine2 ?? '',
      descriptionReturnLine3: version.descriptionReturnLine3 ?? '',
      meanOfTransport: version.meanOfTransport ? [version.meanOfTransport as MeanOfTransport] : [],
      validFrom: version.validFrom ? moment(version.validFrom) : null,
      validTo: version.validTo ? moment(version.validTo) : null,
      etagVersion: version.etagVersion ?? null,
      creationDate: version.creationDate ?? null,
      editionDate: version.editionDate ?? null,
      editor: version.editor ?? null,
      creator: version.creator ?? null,
    };
  }

  public static toApiModel(model: TimetableFieldNumberDetailFormModel): TimetableFieldNumberVersion {
    return {
      businessOrganisation: model.businessOrganisation!,
      number: model.number,
      descriptionOutwardLine1: model.descriptionOutwardLine1,
      descriptionOutwardLine2: model.descriptionOutwardLine2 || undefined,
      descriptionOutwardLine3: model.descriptionOutwardLine3 || undefined,
      descriptionReturnLine1: model.descriptionReturnLine1 || undefined,
      descriptionReturnLine2: model.descriptionReturnLine2 || undefined,
      descriptionReturnLine3: model.descriptionReturnLine3 || undefined,
      meanOfTransport: model.meanOfTransport[0] as TtfnMeanOfTransport,
      validFrom: model.validFrom!.toDate(),
      validTo: model.validTo!.toDate(),
      etagVersion: model.etagVersion ?? undefined,
    };
  }
}
