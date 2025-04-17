import { TableService } from './table.service';
import { TableFilterSingleSearch } from '../table-filter/config/table-filter-single-search';
import { TableFilterChip } from '../table-filter/config/table-filter-chip';
import { TableFilterBoolean } from '../table-filter/config/table-filter-boolean';
import { TableFilterConfig } from './table-filter-config';
import { TableFilterMultiSelect } from '../table-filter/config/table-filter-multiselect';
import { TableFilterSearchSelect } from '../table-filter/config/table-filter-search-select';
import { AtlasCharsetsValidator } from '../../validation/charsets/atlas-charsets-validator';
import { BusinessOrganisation, WorkflowStatus } from '../../../api';
import { TableFilterSearchType } from '../table-filter/config/table-filter-search-type';
import { FormControl, FormGroup } from '@angular/forms';
import { Pages } from '../../../pages/pages';

describe('TableService', () => {
  let service: TableService;

  beforeEach(() => {
    service = new TableService();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return sortString', () => {
    service.sortActive = 'test';
    service.sortDirection = 'desc';
    expect(service.sortString).toEqual('test,desc');
  });

  it('should return undefined (sortString)', () => {
    service.sortActive = '';
    service.sortDirection = 'asc';
    expect(service.sortString).toBeUndefined();

    service.sortActive = 'test';
    service.sortDirection = '';
    expect(service.sortString).toBeUndefined();

    service.sortActive = '';
    service.sortDirection = '';
    expect(service.sortString).toBeUndefined();
  });

  it('should test getOrphanFilters', () => {
    const sharedFilterConfig = {
      search: new TableFilterChip(
        0,
        'col-6',
        'SEPODI.SERVICE_POINTS.WORKFLOW.SEARCH'
      ),
      workflowIds: new TableFilterSingleSearch(
        1,
        'SEPODI.SERVICE_POINTS.WORKFLOW.ID',
        'col-3',
        AtlasCharsetsValidator.numeric
      ),
      workflowStatus: new TableFilterMultiSelect(
        'WORKFLOW.STATUS.',
        'WORKFLOW.STATUS_DETAIL',
        [
          WorkflowStatus.Added,
          WorkflowStatus.Hearing,
          WorkflowStatus.Approved,
          WorkflowStatus.Rejected,
          WorkflowStatus.Canceled,
        ],
        1,
        'col-3',
        [
          WorkflowStatus.Added,
          WorkflowStatus.Hearing,
          WorkflowStatus.Approved,
          WorkflowStatus.Rejected,
          WorkflowStatus.Canceled,
        ]
      ),
      sboid: new TableFilterSearchSelect<BusinessOrganisation>(
        TableFilterSearchType.BUSINESS_ORGANISATION,
        1,
        'col-3',
        new FormGroup({
          businessOrganisation: new FormControl(),
        })
      ),
      locality: new TableFilterSingleSearch(
        1,
        'SEPODI.GEOLOCATION.DISTRICT',
        'col-3 pb-5'
      ),
    };

    const tableFilterConfigIntern = {
      ...sharedFilterConfig,
    };

    const tableFilterConfigIntern2 = {
      ...sharedFilterConfig,
      filterByNoDecision: new TableFilterBoolean(
        0,
        'col-6 container-right-position',
        'SEPODI.SERVICE_POINTS.WORKFLOW.SLIDE'
      ),
    };

    const newFilterConfig = new TableFilterConfig(
      tableFilterConfigIntern,
      Pages.SERVICE_POINT_WORKFLOWS
    );
    const oldFilterConfig = new TableFilterConfig(
      tableFilterConfigIntern2,
      Pages.SERVICE_POINT_WORKFLOWS
    );

    const orphanFilters: string[] = service.getOrphanFilters(
      newFilterConfig,
      oldFilterConfig
    );
    expect(orphanFilters).toEqual(['filterByNoDecision']);
  });
});
