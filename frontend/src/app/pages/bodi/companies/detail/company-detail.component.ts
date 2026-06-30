import { Component, inject, OnInit, signal } from '@angular/core';
import { Company } from '../../../../api';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CompanyFormGroup } from './company-form-group';
import { ActivatedRoute } from '@angular/router';
import { ScrollToTopDirective } from '../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { LinkIconComponent } from '../../../../core/form-components/link-icon/link-icon.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { BackButtonDirective } from '../../../../core/components/button/back-button/back-button.directive';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { TextFieldSfComponent } from '../../../../core/form-components/text-field-sf/text-field-sf.component';
import { form, required } from '@angular/forms/signals';

type CompanyFormModel = {
  uicCode: string;
};

@Component({
  templateUrl: './company-detail.component.html',
  styleUrls: ['./company-detail.component.scss'],
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    ReactiveFormsModule,
    TextFieldComponent,
    LinkIconComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    BackButtonDirective,
    TranslatePipe,
    TextFieldSfComponent,
  ],
})
export class CompanyDetailComponent implements OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly translateService = inject(TranslateService);

  company!: Company;
  form!: FormGroup<CompanyFormGroup>;

  companyFormModel = signal<CompanyFormModel>({
    uicCode: '',
  });
  companyForm = form(this.companyFormModel, (schemaPath) => {
    required(schemaPath.uicCode, {
      message: () => this.translateService.instant('VALIDATION.MAXLENGTH', { length: 5 }),
    });
  });

  ngOnInit() {
    this.company = this.activatedRoute.snapshot.data.companyDetail;
    if (this.company) {
      this.form = new FormGroup<CompanyFormGroup>({
        uicCode: new FormControl({
          value: this.company.uicCode,
          disabled: true,
        }),
        countryCodeIso: new FormControl({
          value: this.company.countryCodeIso,
          disabled: true,
        }),
        shortName: new FormControl({
          value: this.company.shortName,
          disabled: true,
        }),
        name: new FormControl({ value: this.company.name, disabled: true }),
        url: new FormControl({ value: this.company.url, disabled: true }),
      });

      this.companyFormModel.set({
        uicCode: this.company.uicCode ?? 'n/A',
      });
    }
  }

  prependHttp(url: string | null | undefined) {
    if (!url) {
      return url;
    }
    const trimmedUrl = url.trim();
    if (trimmedUrl.startsWith('http')) {
      return trimmedUrl;
    }
    return 'https://' + trimmedUrl;
  }
}
