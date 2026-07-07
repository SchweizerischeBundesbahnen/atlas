import { Component, computed, inject, OnInit, signal } from '@angular/core';
import {
  ApplicationType,
  BusinessOrganisation,
  TransportCompany,
  TransportCompanyBoRelation,
  TransportCompanyStatus,
} from '../../../../api';
import { ReactiveFormsModule } from '@angular/forms';
import { TableColumn } from '../../../../core/components/table/table-column';
import { ActivatedRoute } from '@angular/router';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { ScrollToTopDirective } from '../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { RelationComponent } from '../../../../core/components/relation/relation.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { BackButtonDirective } from '../../../../core/components/button/back-button/back-button.directive';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { disabled, form, required, validateTree } from '@angular/forms/signals';
import { TextFieldSfComponent } from '../../../../core/form-components/text-field-sf/text-field-sf.component';
import { AtlasButtonType } from '../../../../core/components/button/atlas-button.type';
import { AtlasFormCommentSfComponent } from '../../../../core/form-components/comment-sf/atlas-form-comment-sf.component';
import { TransportCompanyDetailFacade } from './transport-company-detail.facade';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import moment, { Moment } from 'moment';
import { DialogData } from '../../../../core/components/dialog/dialog.data';
import { NotificationService } from '../../../../core/notification/notification.service';
import { required as requiredValue } from '../../../../core/util/values';
import { BoSelectSfComponent } from '../../../../core/form-components/bo-select-sf/bo-select-sf.component';
import { DateRangeSfComponent } from '../../../../core/form-components/date-range-sf/date-range-sf.component';

type TransportCompanyFormModel = {
  id: number;
  status: TransportCompanyStatus | null;
  number: string;
  abbreviation: string;
  description: string;
  enterpriseId: string;
  businessRegisterName: string;
  businessRegisterNumber: string;
  comment: string;
};

type TransportCompanyRelationFormModel = {
  businessOrganisation: BusinessOrganisation | null;
  validFrom: Moment | null;
  validTo: Moment | null;
};

export type TransportCompanyRelationFormModelValidated = {
  transportCompanyId: number;
  businessOrganisation: BusinessOrganisation;
  validFrom: Moment;
  validTo: Moment;
};

@Component({
  templateUrl: './transport-company-detail.component.html',
  styleUrls: ['./transport-company-detail.component.scss'],
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    ReactiveFormsModule,
    RelationComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    BackButtonDirective,
    TranslatePipe,
    TextFieldSfComponent,
    AtlasFormCommentSfComponent,
    BoSelectSfComponent,
    DateRangeSfComponent,
  ],
  providers: [TransportCompanyDetailFacade],
})
// todo: test dirty on interface
export class TransportCompanyDetailComponent implements OnInit, DetailFormComponent {
  protected readonly facade = inject(TransportCompanyDetailFacade);

  private readonly permissionService = inject(PermissionService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly translateService = inject(TranslateService);
  private readonly dialogService = inject(DialogService);
  private readonly notificationService = inject(NotificationService);

  private readonly transportCompanyFormModel = signal<TransportCompanyFormModel>({
    id: 0,
    status: null,
    number: '',
    abbreviation: '',
    description: '',
    enterpriseId: '',
    businessRegisterName: '',
    businessRegisterNumber: '',
    comment: '',
  });
  protected readonly transportCompanyForm = form(this.transportCompanyFormModel, (schemaPath) => {
    disabled(schemaPath);
  });

  private readonly emptyFormValue = {
    businessOrganisation: null,
    validFrom: null,
    validTo: null,
  };
  private readonly transportCompanyRelationFormModel = signal<TransportCompanyRelationFormModel>({
    ...this.emptyFormValue,
  });
  protected readonly transportCompanyRelationForm = form(this.transportCompanyRelationFormModel, (schemaPath) => {
    required(schemaPath);
    validateTree(schemaPath, (ctx) => {
      const validFrom = ctx.valueOf(schemaPath.validFrom);
      const validTo = ctx.valueOf(schemaPath.validTo);
      if (validFrom !== null && validTo !== null && validFrom.isAfter(validTo)) {
        return [
          {
            kind: 'dateRange',
            message: 'ValidFrom must be before validTo', // todo: translate
            fieldTree: ctx.fieldTree.validFrom,
          },
          {
            kind: 'dateRange',
            message: 'ValidTo must be after validFrom', // todo: translate
            fieldTree: ctx.fieldTree.validTo,
          },
        ];
      }
      return null;
    });
  });
  dirty = computed(() => this.transportCompanyRelationForm().dirty());

  protected readonly editPermissions = this.permissionService.hasPermissionsToCreate(ApplicationType.Bodi);
  protected readonly AtlasButtonType = AtlasButtonType;
  protected readonly transportCompanyRelationTableColumns = computed<TableColumn<TransportCompanyBoRelation>[]>(() => {
    const currentLang = this.translateService.currentLang() ?? 'de';
    const abbreviationKey = `abbreviation${currentLang[0].toUpperCase()}${currentLang[1]}`;
    const descriptionKey = `description${currentLang[0].toUpperCase()}${currentLang[1]}`;
    return [
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.SBOID',
        valuePath: 'businessOrganisation.sboid',
        columnDef: 'sboid',
      },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
        valuePath: 'businessOrganisation.organisationNumber',
        columnDef: 'organisationNumber',
      },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
        valuePath: `businessOrganisation.${abbreviationKey}`,
        columnDef: 'abbreviation',
      },
      {
        headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
        valuePath: `businessOrganisation.${descriptionKey}`,
        columnDef: 'description',
      },
      {
        headerTitle: 'COMMON.VALID_FROM',
        value: 'validFrom',
        valuePath: 'validFrom',
        columnDef: 'validFrom',
        formatAsDate: true,
      },
      {
        headerTitle: 'COMMON.VALID_TO',
        value: 'validTo',
        valuePath: 'validTo',
        columnDef: 'validTo',
        formatAsDate: true,
      },
    ];
  });

  ngOnInit() {
    const transportCompany: TransportCompany = this.activatedRoute.snapshot.data.transportCompanyDetail[0];
    this.transportCompanyFormModel.set({
      id: transportCompany.id ?? 0,
      status: transportCompany.transportCompanyStatus ?? null,
      number: transportCompany.number ?? '',
      abbreviation: transportCompany.abbreviation ?? '',
      description: transportCompany.description ?? '',
      enterpriseId: transportCompany.enterpriseId ?? '',
      businessRegisterName: transportCompany.businessRegisterName ?? '',
      businessRegisterNumber: transportCompany.businessRegisterNumber ?? '',
      comment: transportCompany.comment ?? '',
    });
    const relations: TransportCompanyBoRelation[] = this.activatedRoute.snapshot.data.transportCompanyDetail[1];
    this.facade.init(relations, transportCompany.id!);
  }

  saveRelation() {
    this.transportCompanyRelationForm().markAsTouched();
    if (this.transportCompanyRelationForm().invalid()) {
      return;
    }
    const validatedForm: TransportCompanyRelationFormModelValidated = {
      transportCompanyId: this.transportCompanyForm.id().value(),
      businessOrganisation: this.transportCompanyRelationForm.businessOrganisation().value()!,
      validFrom: this.transportCompanyRelationForm.validFrom().value()!,
      validTo: this.transportCompanyRelationForm.validTo().value()!,
    };
    this.facade.save(validatedForm).subscribe({
      next: () => {
        this.transportCompanyRelationForm().reset({ ...this.emptyFormValue });
        const successMsg = this.facade.isRelationSelected()
          ? 'RELATION.UPDATE_SUCCESS_MSG'
          : 'RELATION.ADD_SUCCESS_MSG';
        this.notificationService.success(successMsg);
      },
    });
  }

  deleteRelation() {
    this.facade.deleteRelation().subscribe({
      next: () => {
        this.notificationService.success('RELATION.DELETE_SUCCESS_MSG');
      },
    });
  }

  updateRelation() {
    const relation = requiredValue(this.facade.selectedRelation(), 'No relation selected');
    this.transportCompanyRelationFormModel.set({
      businessOrganisation: relation.businessOrganisation ?? null,
      validFrom: moment(relation.validFrom),
      validTo: moment(relation.validTo),
    });
  }

  leaveEditModeWithDialog() {
    if (!this.transportCompanyRelationForm().dirty()) {
      this.cancelEdit();
      return;
    }

    this.dialogService
      .openDialogDataWithConfirmationResult({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      } satisfies DialogData)
      .subscribe((result) => {
        if (result) {
          this.cancelEdit();
        }
      });
  }

  private cancelEdit() {
    this.facade.leaveEditMode();
    this.transportCompanyRelationForm().reset({ ...this.emptyFormValue });
  }
}
