import { Injectable } from '@angular/core';
import {
  TimetableFieldNumber,
  TimetableHearingStatementV2,
} from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { TimetableFieldNumberInternalService } from '../../../api/service/lidi/timetable-field-number-internal.service';

@Injectable({
  providedIn: 'root',
})
export class OpenStatementInMailService {
  constructor(
    private readonly translatePipe: TranslatePipe,
    private readonly timetableFieldNumbersService: TimetableFieldNumberInternalService
  ) {}

  openAsMail(
    statement: TimetableHearingStatementV2,
    ttfnValidOn: Date | undefined
  ) {
    if (statement?.ttfnid) {
      this.timetableFieldNumbersService
        .getOverview(
          [statement!.ttfnid!],
          undefined,
          undefined,
          ttfnValidOn,
          undefined,
          undefined,
          undefined,
          ['ttfnid,ASC']
        )
        .subscribe((result) => {
          const resolvedTtfn = result.objects![0];
          this.openStatementInMailClient(statement, resolvedTtfn);
        });
    } else {
      this.openStatementInMailClient(statement, undefined);
    }
  }

  openStatementInMailClient(
    statement: TimetableHearingStatementV2,
    resolvedTtfn: TimetableFieldNumber | undefined
  ) {
    const a = document.createElement('a');
    a.href = this.buildMailToLink(statement, resolvedTtfn);
    a.click();
  }

  buildMailToLink(
    statement: TimetableHearingStatementV2,
    resolvedTtfn: TimetableFieldNumber | undefined
  ) {
    const statementInfo = this.buildStatementInfo(statement);
    const stopPointInfo = this.buildStopPointInfo(statement);
    const ttfnInfo = this.buildTtfnInfo(resolvedTtfn);

    const subject = this.buildSubject(ttfnInfo, statement.id);
    const body = `${ttfnInfo}${stopPointInfo}${statementInfo}`;
    return `mailto:?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
  }

  private buildStatementInfo(statement: TimetableHearingStatementV2) {
    return (
      this.translatePipe.transform('TTH.STATEMENT.STATEMENT') +
      ': ' +
      statement?.statement
    );
  }

  private buildStopPointInfo(statement: TimetableHearingStatementV2) {
    const stopPointLabel = this.translatePipe.transform(
      'TTH.STATEMENT.STOP_POINT'
    );
    return statement?.stopPlace
      ? `${stopPointLabel}: ${statement?.stopPlace}\r\r`
      : '';
  }

  private buildTtfnInfo(resolvedTtfn: TimetableFieldNumber | undefined) {
    const ttfnLabel = this.translatePipe.transform('TTH.STATEMENT.TTFN');
    return resolvedTtfn
      ? `${ttfnLabel}: ${resolvedTtfn.number} ${resolvedTtfn.description}\r\r`
      : '';
  }

  private buildSubject(ttfnInfo: string | undefined, id: number | undefined) {
    const requestLabel = this.translatePipe.transform('TTH.STATEMENT.REQUEST');
    return ttfnInfo ? `${requestLabel} ${id} ${ttfnInfo}` : '';
  }
}
