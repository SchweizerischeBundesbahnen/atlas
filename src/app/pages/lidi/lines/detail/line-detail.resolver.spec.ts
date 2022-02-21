import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap, RouterModule } from '@angular/router';
import { of } from 'rxjs';
import { LinesService, LineVersion, Status } from '../../../../api';
import { LineDetailResolver } from './line-detail.resolver';
import PaymentTypeEnum = LineVersion.PaymentTypeEnum;
import TypeEnum = LineVersion.TypeEnum;
import { CoreModule } from '../../../../core/module/core.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';

const version: LineVersion = {
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: Status.Active,
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  paymentType: PaymentTypeEnum.None,
  swissLineNumber: 'L1',
  type: TypeEnum.Orderly,
  colorBackCmyk: '',
  colorBackRgb: '',
  colorFontCmyk: '',
  colorFontRgb: '',
};

describe('LineDetailResolver', () => {
  const linesServiceSpy = jasmine.createSpyObj('linesService', ['getLineVersions']);
  linesServiceSpy.getLineVersions.and.returnValue(of([version]));

  let resolver: LineDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        CoreModule,
        RouterModule.forRoot([]),
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [LineDetailResolver, { provide: LinesService, useValue: linesServiceSpy }],
    });
    resolver = TestBed.inject(LineDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', () => {
    const mockRoute = { paramMap: convertToParamMap({ id: '1234' }) } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].id).toBe(1234);
      expect(versions[0].status).toBe(Status.Active);
      expect(versions[0].slnid).toBe('slnid');
    });
  });
});
