import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { BulkImportService } from '../../../api';

@Component({
  selector: 'bulk-import-log',
  templateUrl: './bulk-import-log.component.html',
})
export class BulkImportLogComponent implements OnInit {
  bulkImportId$?: Observable<{ id: number } | null>;
  logs$?: Observable<string[]>;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly bulkImportService: BulkImportService,
  ) {}

  ngOnInit() {
    this.bulkImportId$ = this.route.params.pipe(
      map((params) => {
        if (!params.id) return null;
        if (Number(params.id) || Number(params.id) === 0) {
          this.logs$ = getLogs(params.id);
          return { id: params.id };
        } else {
          return null;
        }
      }),
    );
  }

  private getLogs(id: number): Observable<string[]> {
    console.log('request logs with bulk import id:', id);
    this.bulkImportService.getBulkImportResults(id);
    return of(['test1', 'test2']);
  }
}
