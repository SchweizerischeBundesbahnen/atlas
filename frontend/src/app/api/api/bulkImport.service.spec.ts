import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";
import {BulkImportService} from "./bulkImport.service";
import {AppTestingModule} from "../../app.testing.module";
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {ApplicationType} from "../model/applicationType";
import {BusinessObjectType} from "../model/businessObjectType";
import {ImportType} from "../model/importType";

describe('BulkImportService', () => {
  let service: BulkImportService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        HttpClientTestingModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        { provide: BulkImportService },
      ],
    });

    service = TestBed.inject(BulkImportService);
    httpTestingController = TestBed.inject(HttpTestingController);

  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call downloadTemplate with the correct URL', () => {
    const applicationType = ApplicationType.Sepodi;
    const objectType = BusinessObjectType.ServicePoint;
    const importType = ImportType.Create;
    service.downloadTemplate(applicationType, objectType, importType)
      .subscribe(response => {
        expect(response).toBeTruthy();
      });

    const req = httpTestingController.expectOne(request =>
      request.method === 'GET' &&
      request.url.includes('/bulk-import-service/v1/import/bulk/template/')
    );

    expect(req.request.responseType).toEqual('blob');
  });

  it('should throw an error in getBulkImportResults if id is null', () => {
    expect(() => service.getBulkImportResults(null as any))
      .toThrowError(`Required parameter 'id' is null or undefined.`);
  });

});
