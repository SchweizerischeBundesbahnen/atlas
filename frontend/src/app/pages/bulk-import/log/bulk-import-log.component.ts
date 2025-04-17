import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import {
  BulkImportLogEntry,
  BulkImportResult,
  BulkImportService,
} from '../../../api';
import {
  NgTemplateOutlet,
  NgClass,
  AsyncPipe,
  DatePipe,
} from '@angular/common';
import { MatPaginator } from '@angular/material/paginator';
import { LoadingSpinnerComponent } from '../../../core/components/loading-spinner/loading-spinner.component';
import { TranslatePipe } from '@ngx-translate/core';
import { UserDisplayNamePipe } from '../../../core/pipe/user-display-name.pipe';
import { ParamsForTranslationPipe } from '../../../core/pipe/params-for-translation.pipe';

@Component({
  selector: 'bulk-import-log',
  templateUrl: './bulk-import-log.component.html',
  styleUrl: 'bulk-import-log.component.scss',
  imports: [
    NgTemplateOutlet,
    NgClass,
    MatPaginator,
    LoadingSpinnerComponent,
    AsyncPipe,
    DatePipe,
    TranslatePipe,
    UserDisplayNamePipe,
    ParamsForTranslationPipe,
  ],
})
export class BulkImportLogComponent implements OnInit {
  data$?: Observable<{ importResult?: BulkImportResultTemplate; id: unknown }>;
  pagedLogEntries: Array<BulkImportLogEntryTemplate> = [];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly bulkImportService: BulkImportService
  ) {}

  ngOnInit() {
    this.data$ = this.route.params.pipe(
      switchMap((params) => {
        if (Number(params.id) || Number(params.id) === 0) {
          return this.getLogs(params.id).pipe(
            map((importResult: BulkImportResult) => ({
              importResult: {
                ...importResult,
                logEntries: importResult.logEntries?.map((entry) => ({
                  ...entry,
                  expanded: false,
                })),
              },
              id: params.id,
            })),
            tap(
              (result) =>
                (this.pagedLogEntries = this.pageChanged(
                  { pageIndex: 0, pageSize: 5 },
                  result.importResult.logEntries
                ))
            )
          );
        } else {
          return of({ id: params.id });
        }
      })
    );
  }

  pageChanged(
    e: { pageIndex: number; pageSize: number },
    array?: Array<BulkImportLogEntryTemplate>
  ): Array<BulkImportLogEntryTemplate> {
    if (!array || array.length === 0) {
      return [];
    }
    const start = e.pageIndex * e.pageSize;
    return array.slice(start, start + e.pageSize);
  }

  private getLogs(id: number): Observable<BulkImportResult> {
    return this.bulkImportService.getBulkImportResults(id);
  }
}

interface BulkImportLogEntryTemplate extends BulkImportLogEntry {
  expanded: boolean;
}

interface BulkImportResultTemplate extends BulkImportResult {
  logEntries?: Array<BulkImportLogEntryTemplate>;
}
