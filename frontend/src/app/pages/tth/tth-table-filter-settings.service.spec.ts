import { TthTableFilterSettingsService } from './tth-table-filter-settings.service';
import { describe, expect, it } from 'vitest';
import { TableFilterChip } from '../../core/components/table-filter/config/table-filter-chip';
import { TableFilterMultiSelect } from '../../core/components/table-filter/config/table-filter-multiselect';
import { TableFilterSearchSelect } from '../../core/components/table-filter/config/table-filter-search-select';

describe('TthTableFilterSettingsService', () => {
  describe('createSettings', () => {
    it('should create settings object', () => {
      const settings = TthTableFilterSettingsService.createSettings();
      expect(settings).toBeDefined();
    });

    it('should create chipSearch filter', () => {
      const settings = TthTableFilterSettingsService.createSettings();
      expect(settings.chipSearch).toBeInstanceOf(TableFilterChip);
    });

    it('should create multiSelectStatementStatus filter', () => {
      const settings = TthTableFilterSettingsService.createSettings();
      expect(settings.multiSelectStatementStatus).toBeInstanceOf(TableFilterMultiSelect);
    });

    it('should create searchSelectTU filter', () => {
      const settings = TthTableFilterSettingsService.createSettings();
      expect(settings.searchSelectTU).toBeInstanceOf(TableFilterSearchSelect);
    });

    it('should create searchSelectTTFN filter', () => {
      const settings = TthTableFilterSettingsService.createSettings();
      expect(settings.searchSelectTTFN).toBeInstanceOf(TableFilterSearchSelect);
    });

    it('should return all required filter properties', () => {
      const settings = TthTableFilterSettingsService.createSettings();
      expect(settings.chipSearch).toBeDefined();
      expect(settings.multiSelectStatementStatus).toBeDefined();
      expect(settings.searchSelectTU).toBeDefined();
      expect(settings.searchSelectTTFN).toBeDefined();
    });
  });

  describe('createDossierSettings', () => {
    it('should create dossier settings object', () => {
      const settings = TthTableFilterSettingsService.createDossierSettings();
      expect(settings).toBeDefined();
    });

    it('should create chipSearch filter', () => {
      const settings = TthTableFilterSettingsService.createDossierSettings();
      expect(settings.chipSearch).toBeInstanceOf(TableFilterChip);
    });

    it('should create multiSelectDossierStatus filter', () => {
      const settings = TthTableFilterSettingsService.createDossierSettings();
      expect(settings.multiSelectDossierStatus).toBeInstanceOf(TableFilterMultiSelect);
    });

    it('should return all required filter properties for dossier', () => {
      const settings = TthTableFilterSettingsService.createDossierSettings();

      expect(settings.chipSearch).toBeDefined();
      expect(settings.multiSelectDossierStatus).toBeDefined();
    });
  });
});
