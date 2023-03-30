import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { CompaniesComponent } from './companies.component';
import { CompaniesService, ContainerCompany } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { MockTableComponent } from '../../../app.testing.mocks';

const company: ContainerCompany = {
  objects: [
    {
      uicCode: 1,
      name: 'Geilste Company',
    },
  ],
  totalCount: 1,
};

describe('CompaniesComponent', () => {
  let component: CompaniesComponent;
  let fixture: ComponentFixture<CompaniesComponent>;

  // With Spy
  const companiesService = jasmine.createSpyObj('companiesService', ['getCompanies']);
  companiesService.getCompanies.and.returnValue(of(company));

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CompaniesComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [{ provide: CompaniesService, useValue: companiesService }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(CompaniesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
