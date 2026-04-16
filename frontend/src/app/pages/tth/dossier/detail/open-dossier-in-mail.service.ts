import { inject } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable } from 'rxjs';
import { TimetableHearingStatementInternalService } from '../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { map } from 'rxjs/operators';

export interface DossierMailData {
  statementIds: number[];
}

export abstract class OpenDossierInMailService {
  protected readonly translatePipe = inject(TranslatePipe);
  protected readonly timetableHearingStatementInternalService = inject(TimetableHearingStatementInternalService);

  buildStatementInfo(data: DossierMailData): Observable<string> {
    return forkJoin(data.statementIds.map((id) => this.timetableHearingStatementInternalService.getStatement(id))).pipe(
      map((statements) =>
        this.buildInfoWithLabel(
          'TTH.DOSSIER.STATEMENTS',
          statements
            .map((i) => {
              const anonymizedStatement = i.statementAnonymous ? i.statement : (i.anonymousStatement ?? '');
              return `[${i.id}]: ${anonymizedStatement}`;
            })
            .join('\n\n')
        )
      )
    );
  }

  protected buildInfoWithLabel(label: string, value: string | null | undefined): string {
    if (value) {
      return this.translatePipe.transform(label) + ':\n' + value + '\n\n';
    }
    return '';
  }
}
