import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { TableColumn } from '../../../core/components/table/table-column';
import { ApplicationRole, BusinessOrganisation, BusinessOrganisationsService } from '../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { Observable, of, tap } from 'rxjs';
import { RelationComponent } from '../../../core/components/relation/relation.component';
import { UserPermissionManager } from '../user-permission-manager';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-user-administration-application-config',
  templateUrl: './user-administration-application-config.component.html',
  styleUrls: ['./user-administration-application-config.component.scss'],
})
export class UserAdministrationApplicationConfigComponent {
  @ViewChild(RelationComponent) relationComponent!: RelationComponent<BusinessOrganisation>;

  @Input() applicationConfig!: UserPermissionManager;
  @Input() application!: string;

  @Output() add = new EventEmitter<string>();

  currentRecords$: Observable<BusinessOrganisation[]> = of([]);
  selectedIndex = -1;

  constructor(private readonly boService: BusinessOrganisationsService) {}

  readonly businessOrganisationForm: FormGroup = new FormGroup({
    businessOrganisation: new FormControl<BusinessOrganisation | null>(null),
  });

  readonly tableColumnDef: TableColumn<BusinessOrganisation>[] = [
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.ORGANISATION_NUMBER',
      columnDef: 'organisationNumber',
      value: 'organisationNumber',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.SAID',
      columnDef: 'said',
      value: 'said',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.ABBREVIATION',
      columnDef: 'abbreviation',
      value: 'abbreviationDe',
    },
    {
      headerTitle: 'BODI.BUSINESS_ORGANISATION.DESCRIPTION',
      columnDef: 'description',
      value: 'descriptionDe',
    },
  ];

  getRoleOptions(): ApplicationRole[] {
    return Object.values(ApplicationRole);
  }

  addBusinessOrganisation(): void {
    const value = this.businessOrganisationForm.get('businessOrganisation')?.value;
    if (!value) {
      return;
    }
    this.applicationConfig.addSboidToPermission(this.application, value).pipe(
      map((addedEl) => {
        if (!addedEl) {
          return;
        }
        // TODO: push into observable / merge observables
        this.currentRecords$.push(addedEl);
        this.relationComponent.table.renderRows();
      })
    );
    // this.add.emit(value);
  }

  removeBusinessOrganisation(): void {
    const sboid = this.currentRecords[this.selectedIndex].sboid!;
    this.currentRecords.splice(this.selectedIndex, 1);
    this.relationComponent.table.renderRows();
    this.selectedIndex = -1;
    this.userPermission.sboids.splice(this.selectedIndex, 1);
  }
}
