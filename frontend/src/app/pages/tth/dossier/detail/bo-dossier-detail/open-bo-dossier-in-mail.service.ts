import { Injectable } from '@angular/core';
import { SwissCanton } from '../../../../../api';
import { Cantons } from '../../../../../core/cantons/Cantons';
import { DossierMailData, OpenDossierInMailService } from '../open-dossier-in-mail.service';

export interface BoDossierMailData extends DossierMailData {
  id: number;
  topic: string;
  cantonQuestion: string;
  swissCanton: SwissCanton;
}

@Injectable({
  providedIn: 'root',
})
export class OpenBoDossierInMailService extends OpenDossierInMailService {
  openDossierInMail(data: BoDossierMailData) {
    const tth = this.translatePipe.transform('PAGES.TTH.TITLE_MENU');
    const subject = `${tth} - Dossier ${data.id} - "${data.topic}"`;

    this.buildStatementInfo(data).subscribe((statementInfo) => {
      const dossierInfo = this.buildInfoWithLabel('TTH.DOSSIER.ID_AND_TOPIC', `${data.id} - "${data.topic}"`);
      const cantonInfo = this.buildInfoWithLabel(
        this.translatePipe.transform('TTH.DOSSIER.INQUIRY_FROM_THE_CANTON') +
          ' ' +
          this.getCantonLabel(data.swissCanton),
        data.cantonQuestion
      );
      const body = `${dossierInfo}${cantonInfo}${statementInfo}`;

      window.open(`mailto:?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`, '_self');
    });
  }

  private getCantonLabel(swissCanton: SwissCanton): string {
    return this.translatePipe.transform('TTH.CANTON.' + Cantons.fromSwissCanton(swissCanton)?.short);
  }
}
