import {
  SwissCanton,
  TimetableHearingStatementDocument,
  TimetableHearingStatementSenderV2,
  TimetableHearingStatementV2,
} from '../../../api';
import { Cantons } from '../../../core/cantons/Cantons';
import { TableColumn } from '../../../core/components/table/table-column';

export abstract class StatementTableHandler {
  protected readonly idColumn: TableColumn<TimetableHearingStatementV2> = {
    headerTitle: 'ID',
    value: 'id',
  };
  protected readonly lastNameColumn: TableColumn<TimetableHearingStatementV2> =
    {
      headerTitle: 'TTH.TIMETABLE_FIELD_LASTNAME',
      value: 'statementSender',
      callback: this.mapToLastname,
    };
  protected readonly transportCompanyColumn: TableColumn<TimetableHearingStatementV2> =
    {
      headerTitle: 'BODI.TRANSPORT_COMPANIES.TRANSPORT_COMPANY_ABBREVIATION',
      value: 'responsibleTransportCompaniesDisplay',
    };
  protected readonly fieldNumberColumn: TableColumn<TimetableHearingStatementV2> =
    {
      headerTitle: 'TTH.TIMETABLE_FIELD_NUMBER',
      value: 'timetableFieldNumber',
      disabled: true,
    };
  protected readonly fieldNumberDescriptionColumn: TableColumn<TimetableHearingStatementV2> =
    {
      headerTitle: 'TTH.TIMETABLE_FIELD_NUMBER_DESCRIPTION',
      value: 'timetableFieldDescription',
      disabled: true,
    };
  protected readonly topicColumn: TableColumn<TimetableHearingStatementV2> = {
    headerTitle: 'TTH.DOSSIER.TOPIC',
    value: 'topic',
  };
  protected readonly dataProtectionColumn: TableColumn<TimetableHearingStatementV2> =
    {
      headerTitle: 'TTH.STATEMENT.DATA_PROTECTION',
      value: 'dataProtectionChecked',
      icon: {
        iconCallback: this.computeDataProtectionIcon,
        callback: () => false,
      },
    };
  protected readonly documentsColumn: TableColumn<TimetableHearingStatementV2> =
    {
      headerTitle: 'TTH.TIMETABLE_FIELD_DOCUMENT',
      value: 'documents',
      icon: {
        icon: 'bi bi-paperclip',
        callback: this.isDocumentExisting,
      },
    };

  protected readonly defaultStatementColumns: TableColumn<TimetableHearingStatementV2>[] =
    [
      this.idColumn,
      this.lastNameColumn,
      this.transportCompanyColumn,
      this.fieldNumberColumn,
      this.fieldNumberDescriptionColumn,
      this.topicColumn,
      this.dataProtectionColumn,
      this.documentsColumn,
    ];

  mapToShortCanton(canton: SwissCanton) {
    return Cantons.fromSwissCanton(canton)?.short;
  }

  computeDataProtectionIcon(dataProtectionChecked: boolean): string {
    const iconClass = dataProtectionChecked
      ? 'bi-check color-success'
      : 'bi-x color-error';
    return `bi ${iconClass}`;
  }

  isDocumentExisting(
    documents: Array<TimetableHearingStatementDocument>
  ): boolean {
    return documents.length > 0;
  }

  mapToLastname(statementSender: TimetableHearingStatementSenderV2) {
    return statementSender.lastName;
  }
}
