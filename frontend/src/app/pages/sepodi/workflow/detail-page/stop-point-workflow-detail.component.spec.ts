import {ComponentFixture, TestBed} from '@angular/core/testing';
import {StopPointWorkflowDetailComponent} from "./stop-point-workflow-detail.component";
import {AppTestingModule} from "../../../../app.testing.module";
import {FormModule} from "../../../../core/module/form.module";
import {ActivatedRoute} from "@angular/router";
import {BERN_WYLEREGG} from "../../../../../test/data/service-point";
import {Country, MeanOfTransport, ReadServicePointVersion, ReadStopPointWorkflow, Status} from "../../../../api";
import {StopPointWorkflowDetailFormComponent} from "../detail-form/stop-point-workflow-detail-form.component";
import {StringListComponent} from "../../../../core/form-components/string-list/string-list.component";
import {MockAtlasButtonComponent} from "../../../../app.testing.mocks";
import {DisplayDatePipe} from "../../../../core/pipe/display-date.pipe";
import {SplitServicePointNumberPipe} from "../../../../core/search-service-point/split-service-point-number.pipe";
import {TranslatePipe} from "@ngx-translate/core";
import {DetailPageContentComponent} from "../../../../core/components/detail-page-content/detail-page-content.component";
import {DetailPageContainerComponent} from "../../../../core/components/detail-page-container/detail-page-container.component";
import {DetailFooterComponent} from "../../../../core/components/detail-footer/detail-footer.component";
import {AtlasSpacerComponent} from "../../../../core/components/spacer/atlas-spacer.component";
import {StopPointWorkflowDetailData} from "./stop-point-workflow-detail-resolver.service";
import {UserDetailInfoComponent} from "../../../../core/components/base-detail/user-edit-info/user-detail-info.component";

const workflow: ReadStopPointWorkflow = {
  versionId: 1000,
  sloid: 'ch:1:sloid:8000',
  workflowComment: "No comment"
};
const workflowData: StopPointWorkflowDetailData = {
  workflow: workflow,
  servicePoint: [BERN_WYLEREGG]
}

const activatedRoute = {
  snapshot: {
    data: {
      workflow: workflowData,
    },
  },
};

describe('StopPointWorkflowDetailComponent', () => {
  let component: StopPointWorkflowDetailComponent;
  let fixture: ComponentFixture<StopPointWorkflowDetailComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [
        StopPointWorkflowDetailComponent,
        StopPointWorkflowDetailFormComponent,
        StringListComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        DetailPageContentComponent,
        DetailPageContainerComponent,
        DetailFooterComponent,
        AtlasSpacerComponent,
        UserDetailInfoComponent,
      ],
      imports: [AppTestingModule, FormModule],
      providers: [
        {provide: ActivatedRoute, useValue: activatedRoute},
        {provide: TranslatePipe}
      ]
    }).compileComponents().then();

    fixture = TestBed.createComponent(StopPointWorkflowDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate old designation if version before was validated', () => {
    const servicePoint: ReadServicePointVersion[] = [
      {
        sloid: 'ch:1:sloid:89008',
        designationOfficial: 'Bern, Wyleregg 1',
        businessOrganisation: 'ch:1:sboid:100626',
        meansOfTransport: [MeanOfTransport.Bus],
        status: Status.Validated,
        validFrom: new Date('2014-12-14'),
        validTo: new Date('2021-03-31'),
        number: {number: 8589008, checkDigit: 7, uicCountryCode: 85, numberShort: 89008},
        country: Country.Switzerland,
        stopPoint: true,
      },
      {
        sloid: 'ch:1:sloid:89008',
        designationOfficial: 'Bern, Wyleregg 2',
        businessOrganisation: 'ch:1:sboid:100626',
        meansOfTransport: [MeanOfTransport.Bus],
        status: Status.Draft,
        validFrom: new Date('2021-04-01'),
        validTo: new Date('2021-06-31'),
        number: {number: 8589008, checkDigit: 7, uicCountryCode: 85, numberShort: 89008},
        country: Country.Switzerland,
        stopPoint: true,
      }
    ];

    const result = component.getOldDesignation(servicePoint, 1);
    expect(result).toBe('Bern, Wyleregg 1')
  });

  it('should calculate old designation if version before was not stoppoint', () => {
    const servicePoint: ReadServicePointVersion[] = [
      {
        sloid: 'ch:1:sloid:89008',
        designationOfficial: 'Bern, Wyleregg 1',
        businessOrganisation: 'ch:1:sboid:100626',
        meansOfTransport: [],
        status: Status.Validated,
        validFrom: new Date('2014-12-14'),
        validTo: new Date('2021-03-31'),
        number: {number: 8589008, checkDigit: 7, uicCountryCode: 85, numberShort: 89008},
        country: Country.Switzerland,
        stopPoint: false,
      },
      {
        sloid: 'ch:1:sloid:89008',
        designationOfficial: 'Bern, Wyleregg 2',
        businessOrganisation: 'ch:1:sboid:100626',
        meansOfTransport: [MeanOfTransport.Bus],
        status: Status.Draft,
        validFrom: new Date('2021-04-01'),
        validTo: new Date('2021-06-31'),
        number: {number: 8589008, checkDigit: 7, uicCountryCode: 85, numberShort: 89008},
        country: Country.Switzerland,
        stopPoint: true,
      }
    ];

    const result = component.getOldDesignation(servicePoint, 1);
    expect(result).toBe('-')
  });

});
