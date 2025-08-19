import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from "../../atlas-api.service";
import {Observable} from "rxjs";
import {ContactPointVersion} from "../../../model/contactPointVersion";
import {ReadContactPointVersion} from "../../../model/readContactPointVersion";

@Injectable({
  providedIn: 'root'
})
export class ContactPointService {

  private readonly V1_CONTACT_POINTS = '/prm-directory/v1/contact-points';

  private readonly atlasApiService = inject(AtlasApiService);

  public createContactPoint(contactPointVersion: ContactPointVersion): Observable<ReadContactPointVersion> {
    this.atlasApiService.validateParams({contactPointVersion: contactPointVersion});
    return this.atlasApiService.post(`${this.V1_CONTACT_POINTS}`, contactPointVersion);
  }

  public updateContactPoint(id: number, contactPointVersion: ContactPointVersion): Observable<ReadContactPointVersion> {
    this.atlasApiService.validateParams({id, contactPointVersion: contactPointVersion});
    return this.atlasApiService.put(`${this.V1_CONTACT_POINTS}/${id}`, contactPointVersion);
  }

  public getContactPointVersions(sloid: String): Observable<Array<ReadContactPointVersion>> {
    this.atlasApiService.validateParams({sloid});
    return this.atlasApiService.get(`${this.V1_CONTACT_POINTS}/${sloid}`);
  }

}
