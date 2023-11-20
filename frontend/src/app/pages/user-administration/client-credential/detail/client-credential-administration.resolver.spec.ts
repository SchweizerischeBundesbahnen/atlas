import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { Observable, of } from 'rxjs';
import { ClientCredential } from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { UserService } from '../../service/user.service';
import {
  ClientCredentialAdministrationResolver,
  clientCredentialResolver,
} from './client-credential-administration.resolver';

const clientCredential: ClientCredential = {
  clientCredentialId: '23456789',
};

const routerStateSnapshot = jasmine.createSpyObj('RouterStateSnapshot', ['']);

describe('ClientCredentialAdministrationResolver', () => {
  const userService = jasmine.createSpyObj('userService', ['getClientCredential']);
  userService.getClientCredential.and.returnValue(of(clientCredential));

  let resolver: ClientCredentialAdministrationResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        ClientCredentialAdministrationResolver,
        { provide: UserService, useValue: userService },
      ],
    });
    resolver = TestBed.inject(ClientCredentialAdministrationResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get client credential from service to display', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ clientId: '23456789' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      clientCredentialResolver(mockRoute, routerStateSnapshot),
    ) as Observable<ClientCredential>;

    result.subscribe((snapshot) => {
      expect(snapshot.clientCredentialId).toBe('23456789');
    });
    expect(userService.getClientCredential).toHaveBeenCalled();
  });
});
