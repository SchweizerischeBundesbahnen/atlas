import { BusinessOrganisationVersion, BusinessType } from '../../../../api';
import { BaseDetailFormModel } from '../../../../core/model/base-detail-form-group';
import moment from 'moment';

export interface BusinessOrganisationDetailFormModel extends BaseDetailFormModel {
  descriptionDe: string;
  descriptionFr: string;
  descriptionIt: string;
  descriptionEn: string;
  abbreviationDe: string;
  abbreviationFr: string;
  abbreviationIt: string;
  abbreviationEn: string;
  organisationNumber: string;
  contactEnterpriseEmail: string;
  businessTypes: BusinessType[];
}

export class BusinessOrganisationDetailForm {
  public static readonly emptyFormValue: BusinessOrganisationDetailFormModel = {
    descriptionDe: '',
    descriptionFr: '',
    descriptionIt: '',
    descriptionEn: '',
    abbreviationDe: '',
    abbreviationFr: '',
    abbreviationIt: '',
    abbreviationEn: '',
    organisationNumber: '',
    contactEnterpriseEmail: '',
    businessTypes: [],
    validFrom: null,
    validTo: null,
    etagVersion: null,
    creationDate: null,
    editionDate: null,
    editor: null,
    creator: null,
  };

  public static toFormModel(version: BusinessOrganisationVersion): BusinessOrganisationDetailFormModel {
    return {
      descriptionDe: version.descriptionDe ?? '',
      descriptionFr: version.descriptionFr ?? '',
      descriptionIt: version.descriptionIt ?? '',
      descriptionEn: version.descriptionEn ?? '',
      abbreviationDe: version.abbreviationDe ?? '',
      abbreviationFr: version.abbreviationFr ?? '',
      abbreviationIt: version.abbreviationIt ?? '',
      abbreviationEn: version.abbreviationEn ?? '',
      organisationNumber: version.organisationNumber.toString(),
      contactEnterpriseEmail: version.contactEnterpriseEmail ?? '',
      businessTypes: Array.from(version.businessTypes ?? []),
      validFrom: version.validFrom ? moment(version.validFrom) : null,
      validTo: version.validTo ? moment(version.validTo) : null,
      etagVersion: version.etagVersion ?? null,
      creationDate: version.creationDate ?? null,
      editionDate: version.editionDate ?? null,
      editor: version.editor ?? null,
      creator: version.creator ?? null,
    };
  }

  public static toApiModel(model: BusinessOrganisationDetailFormModel): BusinessOrganisationVersion {
    return {
      descriptionDe: model.descriptionDe,
      descriptionFr: model.descriptionFr,
      descriptionIt: model.descriptionIt,
      descriptionEn: model.descriptionEn,
      abbreviationDe: model.abbreviationDe,
      abbreviationFr: model.abbreviationFr,
      abbreviationIt: model.abbreviationIt,
      abbreviationEn: model.abbreviationEn,
      organisationNumber: Number(model.organisationNumber),
      contactEnterpriseEmail: model.contactEnterpriseEmail,
      businessTypes: model.businessTypes as unknown as Set<BusinessType>,
      validFrom: model.validFrom!.toDate(),
      validTo: model.validTo!.toDate(),
      etagVersion: model.etagVersion ?? undefined,
    };
  }
}
