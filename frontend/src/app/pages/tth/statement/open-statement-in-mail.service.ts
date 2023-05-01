import { Injectable } from '@angular/core';
import {
  TimetableFieldNumber,
  TimetableFieldNumbersService,
  TimetableHearingStatement,
} from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root',
})
export class OpenStatementInMailService {
  constructor(
    private readonly translatePipe: TranslatePipe,
    private readonly timetableFieldNumbersService: TimetableFieldNumbersService
  ) {}

  openAsMail(statement: TimetableHearingStatement, ttfnValidOn: Date | undefined) {
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

  private openStatementInMailClient(
    statement: TimetableHearingStatement,
    resolvedTtfn: TimetableFieldNumber | undefined
  ) {
    const a = document.createElement('a');

    const statementInfo = this.buildStatementInfo(statement);
    const stopPointInfo = this.buildStopPointInfo(statement);
    const ttfnInfo = this.buildTtfnInfo(resolvedTtfn);

    const subject = this.buildSubject(ttfnInfo);
    const body = `${ttfnInfo}${stopPointInfo}${statementInfo}`;

    const rawLink = `mailto:?${subject}body=${body}`;
    a.href = encodeURI(rawLink);
    a.click();
  }

  private buildStatementInfo(statement: TimetableHearingStatement) {
    return this.translatePipe.transform('TTH.STATEMENT.STATEMENT') + ': ' + statement?.statement;
  }

  private buildStopPointInfo(statement: TimetableHearingStatement) {
    const stopPointLabel = this.translatePipe.transform('TTH.STATEMENT.STOP_POINT');
    return statement?.stopPlace ? `${stopPointLabel}: ${statement?.stopPlace}\r\r` : '';
  }

  private buildTtfnInfo(resolvedTtfn: TimetableFieldNumber | undefined) {
    return resolvedTtfn
      ? this.translatePipe.transform('TTH.STATEMENT.TTFN', {
          number: resolvedTtfn.number,
          description: resolvedTtfn.description,
        }) + '\r\r'
      : '';
  }

  private buildSubject(ttfnInfo: string | undefined) {
    return ttfnInfo
      ? 'subject=' + this.translatePipe.transform('TTH.STATEMENT.REQUEST') + ttfnInfo + '&'
      : '';
  }
}
