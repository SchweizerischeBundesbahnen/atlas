import { Component, inject, input, OnInit, output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { SearchSelectComponent } from '../../../../../core/form-components/search-select/search-select.component';
import { TthDossier } from '../../../../../api/model/tthDossier';
import { DossierInternalService } from '../../../../../api/service/workflow/dossier-internal.service';
import { map } from 'rxjs/operators';
import { DossierSelectFormatPipe } from './dossier-select-format.pipe';
import { HearingStatus, SwissCanton } from '../../../../../api';
import { DossierStatus } from '../../../../../api/model/dossierStatus';

@Component({
  selector: 'atlas-dossier-select',
  templateUrl: './dossier-select.component.html',
  imports: [SearchSelectComponent, ReactiveFormsModule, DossierSelectFormatPipe],
})
export class DossierSelectComponent implements OnInit {
  private readonly dossierInternalService = inject(DossierInternalService);

  readonly form = input.required<FormGroup>();

  readonly controlName = input<string>('dossier');
  readonly canton = input<SwissCanton>();
  readonly year = input<number>();
  readonly statusRestriction = input<DossierStatus[]>();
  readonly bindValue = input<string>('');

  readonly selectionChange = output<TthDossier>();
  searchResults$: Observable<TthDossier[]> = of([]);

  ngOnInit() {
    const initialValue = this.form().controls[this.controlName()]?.value;
    this.search(initialValue);
  }

  search(searchQuery: string): void {
    if (!searchQuery) {
      return;
    }
    this.searchResults$ = this.dossierInternalService
      .getOverview(this.year(), HearingStatus.Active, this.canton(), undefined, [searchQuery], this.statusRestriction())
      .pipe(map((response) => response.objects ?? []));
  }
}
