import {BoSelectionDisplayPipe} from './bo-selection-display.pipe';
import {BusinessOrganisation} from '../../../api';

describe('BoSelectionDisplayPipe', () => {

  let boSelectionDisplayPipe: BoSelectionDisplayPipe;

  const boLanguageServiveSpy = jasmine.createSpyObj('BoLanguageService', {
    getCurrentLanguageAbbreviation: 'organisationNumber',
    getCurrentLanguageDescription: 'organisationNumber',
  });

  beforeEach(() => {
    boSelectionDisplayPipe = new BoSelectionDisplayPipe(boLanguageServiveSpy);
  });

  it('create an instance', () => {
    expect(boSelectionDisplayPipe).toBeTruthy();
  });

  it('should transform given bo to text', () => {
    expect(
      boSelectionDisplayPipe.transform({
        sboid: 'sboid',
        organisationNumber: 123,
      } as BusinessOrganisation)
    ).toBe('123 - 123 - 123 - sboid');
  });

  it('should transform undefined to text', () => {
    expect(
      boSelectionDisplayPipe.transform()
    ).toBe('--');
  });
});
