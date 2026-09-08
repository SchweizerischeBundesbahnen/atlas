import { Injectable, inject } from '@angular/core';
import { TimetableHearingStatementV2 } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root',
})
export class OpenStatementInMailService {
  private readonly translatePipe = inject(TranslatePipe);

  openAsMail(statement: TimetableHearingStatementV2) {
    this.openStatementInMailClient(statement);
  }

  openStatementInMailClient(statement: TimetableHearingStatementV2) {
    const a = document.createElement('a');
    a.href = this.buildMailToLink(statement);
    a.click();
  }

  buildMailToLink(statement: TimetableHearingStatementV2) {
    const statementInfo = this.buildStatementInfo(statement);
    const stopPointInfo = this.buildStopPointInfo(statement);
    const ttfnInfo = this.buildTtfnInfo(statement);

    const subject = this.buildSubject(ttfnInfo, statement.id);
    const body = `${ttfnInfo}${stopPointInfo}${statementInfo}`;
    return `mailto:?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
  }

  private buildStatementInfo(statement: TimetableHearingStatementV2) {
    return this.translatePipe.transform('TTH.STATEMENT.STATEMENT') + ': ' + statement?.statement;
  }

  private buildStopPointInfo(statement: TimetableHearingStatementV2) {
    const stopPointLabel = this.translatePipe.transform('TTH.STATEMENT.STOP_POINT');
    return statement?.stopPlace ? `${stopPointLabel}: ${statement?.stopPlace}\r\r` : '';
  }

  private buildTtfnInfo(statement: TimetableHearingStatementV2) {
    const ttfnLabel = this.translatePipe.transform('TTH.STATEMENT.TTFN');
    return statement?.timetableFieldNumber
      ? `${ttfnLabel}: ${statement.timetableFieldNumber} ${statement.timetableFieldDescription}\r\r`
      : '';
  }

  private buildSubject(ttfnInfo: string | undefined, id: number | undefined) {
    const requestLabel = this.translatePipe.transform('TTH.STATEMENT.REQUEST');
    return `${requestLabel} ${id} ${ttfnInfo}`;
  }
}
