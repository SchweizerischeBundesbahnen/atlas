import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BulkImportLogComponent } from './bulk-import-log.component';
import { ActivatedRoute } from '@angular/router';
import { BulkImportResult, BulkImportService } from '../../../api';
import { Observable, of } from 'rxjs';
import { Pipe, PipeTransform } from '@angular/core';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import Spy = jasmine.Spy;
import { MockMatPaginatorComponent } from '../../../app.testing.mocks';

@Pipe({
  name: 'userDisplayName',
  standalone: true,
})
class UserDisplayNamePipeMock implements PipeTransform {
  transform(): Observable<undefined> {
    return of();
  }
}

fdescribe('BulkImportLogComponent', () => {
  let component: BulkImportLogComponent;
  let fixture: ComponentFixture<BulkImportLogComponent>;

  beforeEach(async () => {
    const bulkImportServiceSpy = jasmine.createSpyObj<BulkImportService>(['getBulkImportResults']);

    (bulkImportServiceSpy.getBulkImportResults as Spy<(id: number) => Observable<BulkImportResult>>)
      .withArgs(10)
      .and.returnValue(
        of({
          importType: 'UPDATE',
          businessObjectType: 'SERVICE_POINT',
          creator: 'atlas',
          creationDate: '2024-09-24T12:31:01.87023',
          inNameOf: 'atlas2',
          nbOfSuccess: 1,
          nbOfInfo: 1,
          nbOfError: 1,
          logEntries: [
            {
              lineNumber: 2,
              status: 'INFO',
              errors: [
                {
                  errorMessage: 'error',
                  displayInfo: {
                    code: 'TRANSLATIONS.TEST',
                    parameters: [
                      {
                        key: 'field',
                        value: 'test',
                      },
                    ],
                  },
                },
              ],
            },
            {
              lineNumber: 3,
              status: 'DATA_EXECUTION_ERROR',
              errors: [],
            },
            {
              lineNumber: 4,
              status: 'DATA_VALIDATION_ERROR',
              errors: [],
            },
          ],
        }),
      );

    await TestBed.configureTestingModule({
      imports: [
        UserDisplayNamePipeMock,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      declarations: [BulkImportLogComponent, MockMatPaginatorComponent],
      providers: [
        { provide: BulkImportService, useValue: bulkImportServiceSpy },
        { provide: ActivatedRoute, useValue: { params: of({ id: 10 }) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BulkImportLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and init', (done) => {
    expect(component).toBeTruthy();
    component.data$?.subscribe((data) => {
      expect(data.id).toEqual(10);
      expect(data.importResult).toEqual({
        importType: 'UPDATE',
        businessObjectType: 'SERVICE_POINT',
        creator: 'atlas',
        creationDate: '2024-09-24T12:31:01.87023',
        inNameOf: 'atlas2',
        nbOfSuccess: 1,
        nbOfInfo: 1,
        nbOfError: 1,
        logEntries: [
          {
            expanded: false,
            lineNumber: 2,
            status: 'INFO',
            errors: [
              {
                errorMessage: 'error',
                displayInfo: {
                  code: 'TRANSLATIONS.TEST',
                  parameters: [
                    {
                      key: 'field',
                      value: 'test',
                    },
                  ],
                },
              },
            ],
          },
          {
            expanded: false,
            lineNumber: 3,
            status: 'DATA_EXECUTION_ERROR',
            errors: [],
          },
          {
            expanded: false,
            lineNumber: 4,
            status: 'DATA_VALIDATION_ERROR',
            errors: [],
          },
        ],
      });
      done();
    });
  });

  // todo
  it('should change page', () => {});
});
