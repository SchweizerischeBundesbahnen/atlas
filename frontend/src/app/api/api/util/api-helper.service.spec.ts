import {TestBed} from '@angular/core/testing';

import {ApiHelperService} from './api-helper.service';
import {AppTestingModule} from "../../../app.testing.module";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {UserService} from "../../../core/auth/user/user.service";
import {environment} from "../../../../environments/environment";

describe('ApiHelperService', () => {
  let service: ApiHelperService;
  let mockUserService: {
    currentUser: any;
    readonly loggedIn: boolean;
  };

  beforeEach(() => {
    mockUserService = {
      currentUser: null,
      get loggedIn() { return !!this.currentUser; }
    };

    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: ApiHelperService },
        { provide: UserService, useValue: mockUserService }
      ]
    });
    service = TestBed.inject(ApiHelperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('DEFAULT_HTTP_HEADERS should contain Accept: */*', () => {
    const header = service.DEFAULT_HTTP_HEADERS;
    expect(header.get('Accept')).toBe('*/*');
  });

  it('validateParams should throw error when null/undefined', () => {
    expect(() => service.validateParams({ attribute: null }))
      .toThrowError(`Required parameter 'attribute' is null or undefined.`);
  });

  it('should createFormData', async () => {
    const file = new File(['test'], 'test.txt');
    const data = service.createFormData({ file, object: { attribute: 1 } });
    expect(data.get('file')).toBe(file);

    const blob = data.get('object') as Blob;
    const text = await new Response(blob).text();
    expect(JSON.parse(text)).toEqual({ attribute: 1 });
  });

  it('should return atlasApiUrl if loggedIn', () => {
    mockUserService.currentUser = { id: 1 };
    expect(service.getBasePath()).toBe(environment.atlasApiUrl);
  });

  it('should return atlasApiUrl if not loggedIn', () => {
    mockUserService.currentUser = null;
    expect(service.getBasePath()).toBe(environment.atlasUnauthApiUrl);
  });

  it('handleError should throw error with Message', (done) => {
    service.handleError().subscribe({
      next: () => fail('should error'),
      error: e => {
        expect(e).toBe('Unexpected Error occured!');
        done();
      }
    });
  });

});
