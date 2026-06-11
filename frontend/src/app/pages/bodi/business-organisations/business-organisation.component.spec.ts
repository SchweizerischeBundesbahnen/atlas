import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { BusinessOrganisationComponent } from './business-organisation.component';
import { ContainerBusinessOrganisation } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent, translateServiceProvider } from '../../../app.testing.mocks';
import { DEFAULT_STATUS_SELECTION } from '../../../core/constants/status.choices';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { TableComponent } from '../../../core/components/table/table.component';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const businessOrganisation: ContainerBusinessOrganisation = {
  objects: [
    {
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
    },
  ],
  totalCount: 1,
};

describe('BusinessOrganisationComponent', () => {
  let component: BusinessOrganisationComponent;
  let fixture: ComponentFixture<BusinessOrganisationComponent>;

  let businessOrganisationService: Mocked<Pick<BusinessOrganisationService, 'getAllBusinessOrganisations'>>;

  beforeEach(() => {
    businessOrganisationService = {
      getAllBusinessOrganisations: vi.fn(),
    };
    businessOrganisationService.getAllBusinessOrganisations.mockReturnValue(of(businessOrganisation));

    TestBed.configureTestingModule({
      imports: [BusinessOrganisationComponent],
      providers: [
        translateServiceProvider,
        TranslatePipe,
        RouterOutlet,
        {
          provide: BusinessOrganisationService,
          useValue: businessOrganisationService,
        },
        { provide: ActivatedRoute, useValue: { paramMap: new Subject() } },
      ],
    }).overrideComponent(BusinessOrganisationComponent, {
      remove: { imports: [TableComponent] },
      add: { imports: [MockTableComponent] },
    });

    fixture = TestBed.createComponent(BusinessOrganisationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should getOverview', () => {
    component.getOverview({
      page: 0,
      size: 10,
    });

    expect(businessOrganisationService.getAllBusinessOrganisations).toHaveBeenCalledExactlyOnceWith(
      [],
      undefined,
      undefined,
      DEFAULT_STATUS_SELECTION,
      0,
      10,
      ['descriptionDe,asc']
    );

    expect(component.totalCount$).toEqual(1);
    expect(component.businessOrganisations.length).toEqual(1);
    expect(component.businessOrganisations[0].sboid).toEqual('sboid');
  });
});
