import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ApplicationType, TransportCompany, TransportCompanyBoRelation, TransportCompanyStatus } from '../../../../api';
import { ReactiveFormsModule } from '@angular/forms';
import { TableColumn } from '../../../../core/components/table/table-column';
import { ActivatedRoute } from '@angular/router';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { ScrollToTopDirective } from '../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { RelationComponent } from '../../../../core/components/relation/relation.component';
import {
  BusinessOrganisationSelectComponent
} from '../../../../core/form-components/bo-select/business-organisation-select.component';
import { DateRangeComponent } from '../../../../core/form-components/date-range/date-range.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { BackButtonDirective } from '../../../../core/components/button/back-button/back-button.directive';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { disabled, form } from '@angular/forms/signals';
import { TextFieldSfComponent } from '../../../../core/form-components/text-field-sf/text-field-sf.component';
import { AtlasButtonType } from '../../../../core/components/button/atlas-button.type';
import { AtlasFormCommentSfComponent } from '../../../../core/form-components/comment-sf/atlas-form-comment-sf.component';
import { TransportCompanyDetailFacade } from './transport-company-detail.facade';

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

@Component({
  templateUrl: './transport-company-detail.component.html',
  styleUrls: ['./transport-company-detail.component.scss'],
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    ReactiveFormsModule,
    RelationComponent,
    BusinessOrganisationSelectComponent,
    DateRangeComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    BackButtonDirective,
    TranslatePipe,
    TextFieldSfComponent,
    AtlasFormCommentSfComponent,
  ],
})
// todo: define dirty on interface
export class TransportCompanyDetailComponent implements OnInit, DetailFormComponent {
  protected readonly facade = inject(TransportCompanyDetailFacade);
  private readonly permissionService = inject(PermissionService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly translateService = inject(TranslateService);

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

  // todo: decide where to put forms (component or facade)
  ngOnInit() {
    const transportCompany: TransportCompany = this.activatedRoute.snapshot.data.transportCompanyDetail[0];
    this.transportCompanyRelations = this.activatedRoute.snapshot.data.transportCompanyDetail[1];

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
  }
}
