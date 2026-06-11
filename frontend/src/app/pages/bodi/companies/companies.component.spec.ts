import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import { CompaniesComponent } from './companies.component';
import { ContainerCompany } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent, translateServiceProvider } from '../../../app.testing.mocks';
import { TableComponent } from '../../../core/components/table/table.component';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { CompanyService } from '../../../api/service/bodi/company.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const company: ContainerCompany = {
  objects: [
    {
      uicCode: '1',
      name: 'Geilste Company',
    },
  ],
  totalCount: 1,
};

describe('CompaniesComponent', () => {
  let component: CompaniesComponent;
  let fixture: ComponentFixture<CompaniesComponent>;
  let companyService: Mocked<Pick<CompanyService, 'getCompanies'>>;

  beforeEach(() => {
    companyService = {
      getCompanies: vi.fn(),
    };
    companyService.getCompanies.mockReturnValue(of(company));

     TestBed.configureTestingModule({
       imports: [CompaniesComponent],
       providers: [
         translateServiceProvider,
         TranslatePipe,
         RouterOutlet,
         { provide: CompanyService, useValue: companyService },
         { provide: ActivatedRoute, useValue: { paramMap: new Subject() } },
       ],
     }).overrideComponent(CompaniesComponent, {
       remove: { imports: [TableComponent] },
       add: { imports: [MockTableComponent] },
     });

    fixture = TestBed.createComponent(CompaniesComponent);
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

    expect(companyService.getCompanies).toHaveBeenCalledExactlyOnceWith([], 0, 10, ['uicCode,asc']);
    expect(component['companies'].length).toEqual(1);
    expect(component['companies'][0].uicCode).toEqual('1');
    expect(component['totalCount']).toEqual(1);
  });
});
