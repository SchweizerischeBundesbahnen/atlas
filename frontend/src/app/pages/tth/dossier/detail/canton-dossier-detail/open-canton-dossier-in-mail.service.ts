import { Injectable } from '@angular/core';
import { DossierMailData, OpenDossierInMailService } from '../open-dossier-in-mail.service';

export interface CantonDossierMailData extends DossierMailData {
  topic: string;
  statementIds: number[];
  question: string | null | undefined;
  answer: string | null | undefined;
  internalComment: string | null | undefined;
  publicComment: string | null | undefined;
}

@Injectable({
  providedIn: 'root',
})
export class OpenCantonDossierInMailService extends OpenDossierInMailService {
  openDossierInMailClient(dossierMailData: CantonDossierMailData) {
    this.buildStatementInfo(dossierMailData).subscribe((statementInfo) => {
      const topicInfo = this.buildInfoWithLabel('TTH.DOSSIER.TOPIC', dossierMailData.topic);
      const questionInfo = this.buildInfoWithLabel('TTH.DOSSIER.BO_QUESTION', dossierMailData.question);
      const answerInfo = this.buildInfoWithLabel('TTH.DOSSIER.BO_ANSWER', dossierMailData.answer);
      const internalCommentInfo = this.buildInfoWithLabel(
        'TTH.DOSSIER.INTERNAL_COMMENT',
        dossierMailData.internalComment
      );
      const publicCommentInfo = this.buildInfoWithLabel('TTH.DOSSIER.PUBLIC_COMMENT', dossierMailData.publicComment);

      const subject = `Dossier "${dossierMailData.topic}"`;
      const body = `${topicInfo}${questionInfo}${answerInfo}${internalCommentInfo}${publicCommentInfo}${statementInfo}`;
      const link = `mailto:?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
      window.open(link, '_self');
    });
  }
}
