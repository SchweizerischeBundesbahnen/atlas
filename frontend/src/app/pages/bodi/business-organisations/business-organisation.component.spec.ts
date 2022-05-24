import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { TableComponent } from '../../../core/components/table/table.component';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { BusinessOrganisationComponent } from './business-organisation.component';
import { ContainerLine, LinesService, LineType } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { Component, Input, TemplateRef } from '@angular/core';

const versionContainer: ContainerLine = {
  objects: [
    {
      slnid: 'slnid',
      description: 'asdf',
      status: 'ACTIVE',
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
      businessOrganisation: 'SBB',
      swissLineNumber: 'L1',
      lineType: LineType.Orderly,
    },
  ],
  totalCount: 1,
};

@Component({
  selector: 'app-table-search',
  template: '<p>Mock Product Editor Component</p>',
})
class MockAppTableSearchComponent {
  @Input() additionalFieldTemplate!: TemplateRef<any>;
}

describe('LinesComponent', () => {
  let component: BusinessOrganisationComponent;
  let fixture: ComponentFixture<BusinessOrganisationComponent>;

  // With Spy
  const linesService = jasmine.createSpyObj('linesService', ['getLines']);
  linesService.getLines.and.returnValue(of(versionContainer));
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        BusinessOrganisationComponent,
        TableComponent,
        LoadingSpinnerComponent,
        MockAppTableSearchComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: LinesService, useValue: linesService }, TranslatePipe],
    }).compileComponents();

    fixture = TestBed.createComponent(BusinessOrganisationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(linesService.getLines).toHaveBeenCalled();

    expect(component.lineVersions.length).toBe(1);
    expect(component.totalCount$).toBe(1);
  });
});
