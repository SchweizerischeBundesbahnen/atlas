import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { BulkImportLogEntry, BulkImportResult, BulkImportService } from '../../../api';

@Component({
  selector: 'bulk-import-log',
  templateUrl: './bulk-import-log.component.html',
  styleUrl: 'bulk-import-log.component.scss',
})
export class BulkImportLogComponent implements OnInit {
  data$?: Observable<{ importResult?: BulkImportResultTemplate; id: number | null }>;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly bulkImportService: BulkImportService,
  ) {}

  ngOnInit() {
    this.data$ = this.route.params.pipe(
      map((params) => {
        if (!params.id) return null;
        if (Number(params.id) || Number(params.id) === 0) {
          return params.id;
        } else {
          return null;
        }
      }),
      switchMap((id: number | null) => {
        if (id === null) {
          return of({ id });
        } else {
          return this.getLogs(id).pipe(
            map((importResult: BulkImportResult) => ({
              importResult: {
                ...importResult,
                logEntries: importResult.logEntries?.map((entry) => ({
                  ...entry,
                  expanded: false,
                })),
              },
              id,
            })),
          );
        }
      }),
    );
  }

  private getLogs(id: number): Observable<BulkImportResult> {
    console.log('request logs with bulk import id:', id);
    return this.bulkImportService.getBulkImportResults(id);
  }
}

interface BulkImportLogEntryTemplate extends BulkImportLogEntry {
  expanded: boolean;
}

interface BulkImportResultTemplate extends BulkImportResult {
  logEntries?: Array<BulkImportLogEntryTemplate>;
}
