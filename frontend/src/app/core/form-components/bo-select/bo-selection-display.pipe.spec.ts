import { BoSelectionDisplayPipe } from './bo-selection-display.pipe';
import { BusinessOrganisation } from '../../../api';

describe('BoSelectionDisplayPipe', () => {
  it('create an instance', () => {
    const boLanguageServiveSpy = jasmine.createSpyObj('BoLanguageService', {
      getCurrentLanguageAbbreviation: 'organisationNumber',
      getCurrentLanguageDescription: 'organisationNumber',
    });
    const pipe = new BoSelectionDisplayPipe(boLanguageServiveSpy);
    expect(pipe).toBeTruthy();
    expect(
      pipe.transform({
        said: 'said',
        organisationNumber: 123,
      } as BusinessOrganisation)
    ).toBe('123 - 123 - 123 - said');
  });
});
