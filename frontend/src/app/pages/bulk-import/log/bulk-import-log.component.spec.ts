import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BulkImportLogComponent } from './bulk-import-log.component';
import { ActivatedRoute } from '@angular/router';
import { BulkImportResult } from '../../../api';
import { firstValueFrom, Observable, of } from 'rxjs';
import { Pipe, PipeTransform } from '@angular/core';
import { MockMatPaginatorComponent, translateServiceProvider } from '../../../app.testing.mocks';
import { By } from '@angular/platform-browser';
import { UserDisplayNamePipe } from '../../../core/pipe/user-display-name.pipe';
import { BulkImportService } from '../../../api/service/bulk/bulk-import.service';
import { beforeEach, describe, expect, it, type Mocked, MockInstance, vi } from 'vitest';

@Pipe({
  name: 'userDisplayName',
  standalone: true,
})
class UserDisplayNamePipeMock implements PipeTransform {
  transform(): Observable<undefined> {
    return of();
  }
}

describe('BulkImportLogComponent', () => {
  let component: BulkImportLogComponent;
  let fixture: ComponentFixture<BulkImportLogComponent>;
  let bulkImportService: Mocked<Pick<BulkImportService, 'getBulkImportResults'>>;
  let pageChangedFnSpy: MockInstance<typeof BulkImportLogComponent.prototype.pageChanged>;

  beforeEach(() => {
    bulkImportService = {
      getBulkImportResults: vi.fn(),
    };
    bulkImportService.getBulkImportResults.mockReturnValue(of(importResult));

    TestBed.configureTestingModule({
      imports: [BulkImportLogComponent],
      providers: [
        translateServiceProvider,
        { provide: BulkImportService, useValue: bulkImportService },
        { provide: ActivatedRoute, useValue: { params: of({ id: 10 }) } },
      ],
    }).overrideComponent(BulkImportLogComponent, {
      remove: { imports: [UserDisplayNamePipe] },
      add: { imports: [UserDisplayNamePipeMock] },
    });

    fixture = TestBed.createComponent(BulkImportLogComponent);
    component = fixture.componentInstance;
    pageChangedFnSpy = vi.spyOn(component, 'pageChanged');
  });

  it('should create and init', async () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(pageChangedFnSpy).toHaveBeenCalledTimes(1);
    const data = await firstValueFrom(component.data$!);
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
                parameters: [{ key: 'field', value: 'test' }],
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
  });

  it('should change page', () => {
    pageChangedFnSpy.mockRestore();
    pageChangedFnSpy = vi.spyOn(component, 'pageChanged');
    fixture.detectChanges();
    const paginator: MockMatPaginatorComponent = fixture.debugElement.query(By.css('mat-paginator')).componentInstance;
    paginator.page.emit({ pageIndex: 1, pageSize: 2 });
    expect(component.pagedLogEntries).toEqual([
      {
        expanded: false,
        lineNumber: 4,
        status: 'DATA_VALIDATION_ERROR',
        errors: [],
      },
    ]);
  });
});

const importResult: BulkImportResult = {
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
            parameters: [{ key: 'field', value: 'test' }],
          },
        },
      ],
    },
    { lineNumber: 3, status: 'DATA_EXECUTION_ERROR', errors: [] },
    { lineNumber: 4, status: 'DATA_VALIDATION_ERROR', errors: [] },
  ],
};
