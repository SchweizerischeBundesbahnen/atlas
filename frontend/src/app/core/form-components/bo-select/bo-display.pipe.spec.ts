import { BusinessOrganisationVersion } from '../../../api';
import { BoDisplayPipe } from './bo-display.pipe';
import { firstValueFrom, of } from 'rxjs';
import { beforeEach, describe, expect, it } from 'vitest';
import { BoSelectionDisplayPipe } from './bo-selection-display.pipe';
import { mock } from 'vitest-mock-extended';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';

const version: BusinessOrganisationVersion = {
  id: 1234,
  organisationNumber: 1234,
  sboid: 'sboid',
  descriptionDe: 'asdf',
  descriptionFr: 'asdf',
  descriptionIt: 'asdf',
  descriptionEn: 'asdf',
  abbreviationDe: 'asdf',
  abbreviationFr: 'asdf',
  abbreviationIt: 'asdf',
  abbreviationEn: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
};

describe('BoDisplayPipe', () => {
  let boDisplayPipe: BoDisplayPipe;

  const boSelectionDisplayPipe = mock<BoSelectionDisplayPipe>();
  boSelectionDisplayPipe.transform.mockReturnValue('123 - 123 - 123 - sboid');

  const businessOrganisationsService = mock<BusinessOrganisationService>();
  businessOrganisationsService.getVersions.mockReturnValue(of([version]));

  beforeEach(() => {
    boDisplayPipe = new BoDisplayPipe(boSelectionDisplayPipe, businessOrganisationsService);
  });

  it('create an instance', () => {
    expect(boDisplayPipe).toBeTruthy();
  });

  it('should transform given sboid', async () => {
    const result = await firstValueFrom(boDisplayPipe.transform('sboid'));
    expect(result).toBe('123 - 123 - 123 - sboid');

    expect(boSelectionDisplayPipe.transform).toHaveBeenCalled();
    expect(businessOrganisationsService.getVersions).toHaveBeenCalled();
  });
});
