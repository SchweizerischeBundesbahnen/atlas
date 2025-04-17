import { BusinessOrganisationVersion } from '../../../api';
import { BoDisplayPipe } from './bo-display.pipe';
import { of } from 'rxjs';

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

  const boSelectionDisplayPipe = jasmine.createSpyObj(
    'BoSelectionDisplayPipe',
    {
      transform: '123 - 123 - 123 - sboid',
    }
  );

  const businessOrganisationsService = jasmine.createSpyObj(
    'BusinessOrganisationsService',
    {
      getVersions: of([version]),
    }
  );

  beforeEach(() => {
    boDisplayPipe = new BoDisplayPipe(
      boSelectionDisplayPipe,
      businessOrganisationsService
    );
  });

  it('create an instance', () => {
    expect(boDisplayPipe).toBeTruthy();
  });

  it('should transform given sboid', (doneCallback) => {
    boDisplayPipe.transform('sboid').subscribe((result) => {
      expect(result).toBe('123 - 123 - 123 - sboid');

      expect(boSelectionDisplayPipe.transform).toHaveBeenCalled();
      expect(businessOrganisationsService.getVersions).toHaveBeenCalled();
      doneCallback();
    });
  });
});
