import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ScrollToTopDirective } from '../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { LinkIconComponent } from '../../../../core/form-components/link-icon/link-icon.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { BackButtonDirective } from '../../../../core/components/button/back-button/back-button.directive';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasTextFieldComponent } from '@atlas/form';
import { disabled, form } from '@angular/forms/signals';
import { Company } from '../../../../api';

type CompanyFormModel = {
  uicCode: string;
  countryCodeIso: string;
  shortName: string;
  name: string;
  url: string;
};

@Component({
  templateUrl: './company-detail.component.html',
  styleUrls: ['./company-detail.component.scss'],
  imports: [
    ScrollToTopDirective,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    LinkIconComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    BackButtonDirective,
    TranslatePipe,
    AtlasTextFieldComponent,
  ],
})
export class CompanyDetailComponent implements OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly companyFormModel = signal<CompanyFormModel>({
    uicCode: '',
    countryCodeIso: '',
    shortName: '',
    name: '',
    url: '',
  });
  protected readonly companyForm = form(this.companyFormModel, (schemaPath) => {
    disabled(schemaPath);
  });

  ngOnInit() {
    const company: Company = this.activatedRoute.snapshot.data.companyDetail;
    if (company) {
      this.companyFormModel.set({
        uicCode: company.uicCode ?? '',
        countryCodeIso: company.countryCodeIso ?? '',
        shortName: company.shortName ?? '',
        name: company.name ?? '',
        url: company.url ?? '',
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
