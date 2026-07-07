import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { mock } from 'vitest-mock-extended';
import { BoSelectionDisplayPipe } from './bo-selection-display.pipe';
import { BusinessOrganisation } from '../../api';
import { BusinessOrganisationLanguageService } from '../../pages/bodi/business-organisations/shared/business-organisation-language.service';

describe('BoSelectionDisplayPipe', () => {
  let boSelectionDisplayPipe: BoSelectionDisplayPipe;

  const boLanguageServiceMock = mock<BusinessOrganisationLanguageService>();

  beforeEach(() => {
    boLanguageServiceMock.getCurrentLanguageAbbreviation.mockReturnValue('abbreviationDe');
    boLanguageServiceMock.getCurrentLanguageDescription.mockReturnValue('descriptionDe');

    TestBed.configureTestingModule({
      providers: [
        BoSelectionDisplayPipe,
        { provide: BusinessOrganisationLanguageService, useValue: boLanguageServiceMock },
      ],
    });

    boSelectionDisplayPipe = TestBed.inject(BoSelectionDisplayPipe);
  });

  it('create an instance', () => {
    expect(boSelectionDisplayPipe).toBeTruthy();
  });

  it('should transform given bo to text', () => {
    expect(
      boSelectionDisplayPipe.transform({
        sboid: 'sboid',
        organisationNumber: 123,
        abbreviationDe: 'ABB',
        descriptionDe: 'Description',
      } as BusinessOrganisation)
    ).toBe('123 - ABB - Description - sboid');
  });

  it('should transform undefined to text', () => {
    expect(boSelectionDisplayPipe.transform()).toBe('--');
  });
});
