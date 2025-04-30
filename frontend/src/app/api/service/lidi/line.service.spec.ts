import { LineService } from './line.service';
import { TestBed } from '@angular/core/testing';
import { UserService } from '../../../core/auth/user/user.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { LineVersionV2 } from '../../model/lineVersionV2';
import { UpdateLineVersionV2 } from '../../model/updateLineVersionV2';
import SpyObj = jasmine.SpyObj;

describe('LineService', () => {

  let service: LineService;

  let userService: SpyObj<UserService>;
  let httpClient: SpyObj<HttpClient>;

  beforeEach(() => {
    userService = jasmine.createSpyObj<UserService>({
      get loggedIn(): boolean {
        return true;
      },
    });

    httpClient = jasmine.createSpyObj<HttpClient>(['get', 'put', 'post']);

    TestBed.configureTestingModule({
      providers: [LineService,
        { provide: UserService, useValue: userService },
        { provide: HttpClient, useValue: httpClient },
      ],
    });

    service = TestBed.inject(LineService);
  });

  it('should getLines', () => {
    const params = new HttpParams()
      .append('swissLineNumber', '123')
      .append('statusRestrictions', 'REVOKED')
      .append('statusRestrictions', 'DRAFT')
      .append('validOn', '2025-01-01');

    service.getLines('123', undefined, ['REVOKED', 'DRAFT'], undefined, undefined, undefined, new Date(2025, 0, 1));

    const callArgs = httpClient.get.calls.argsFor(0);
    expect(callArgs[0]).toEqual('http://localhost:8888/line-directory/v1/lines'); // url
    expect(callArgs[1]?.responseType).toEqual('json');
    expect(callArgs[1]?.params).toEqual(params);
    expect((callArgs[1]?.headers as HttpHeaders).get('Accept')).toEqual('*/*');
  });

  it('should getLine', () => {
    const validateSpy = spyOn(Object.getPrototypeOf(service), 'validateParams').and.callThrough();

    service.getLine('123');

    const callArgs = httpClient.get.calls.argsFor(0);
    expect(callArgs[0]).toEqual('http://localhost:8888/line-directory/v1/lines/123'); // url
    expect(callArgs[1]?.responseType).toEqual('json');
    expect(callArgs[1]?.params).toEqual(undefined);
    expect((callArgs[1]?.headers as HttpHeaders).get('Accept')).toEqual('*/*');

    expect(validateSpy).toHaveBeenCalledOnceWith({ slnid: '123' });
  });

  it('should getLineVersions', () => {
    const validateSpy = spyOn(Object.getPrototypeOf(service), 'validateParams').and.callThrough();

    service.getLineVersions('123');

    const callArgs = httpClient.get.calls.argsFor(0);
    expect(callArgs[0]).toEqual('http://localhost:8888/line-directory/v1/lines/versions/123'); // url
    expect(callArgs[1]?.responseType).toEqual('json');
    expect(callArgs[1]?.params).toEqual(undefined);
    expect((callArgs[1]?.headers as HttpHeaders).get('Accept')).toEqual('*/*');

    expect(validateSpy).toHaveBeenCalledOnceWith({ slnid: '123' });
  });

  it('should getLineVersionsV2', () => {
    const validateSpy = spyOn(Object.getPrototypeOf(service), 'validateParams').and.callThrough();

    service.getLineVersionsV2('123');

    const callArgs = httpClient.get.calls.argsFor(0);
    expect(callArgs[0]).toEqual('http://localhost:8888/line-directory/v2/lines/versions/123'); // url
    expect(callArgs[1]?.responseType).toEqual('json');
    expect(callArgs[1]?.params).toEqual(undefined);
    expect((callArgs[1]?.headers as HttpHeaders).get('Accept')).toEqual('*/*');

    expect(validateSpy).toHaveBeenCalledOnceWith({ slnid: '123' });
  });

  it('should createLineVersionV2', () => {
    const validateSpy = spyOn(Object.getPrototypeOf(service), 'validateParams').and.callThrough();

    service.createLineVersionV2({} as LineVersionV2);

    const callArgs = httpClient.post.calls.argsFor(0);
    expect(callArgs[0]).toEqual('http://localhost:8888/line-directory/v2/lines/versions'); // url
    expect(callArgs[1]).toEqual({});
    expect(callArgs[2]?.responseType).toEqual('json');
    expect((callArgs[2]?.headers as HttpHeaders).get('Content-Type')).toEqual('application/json');

    expect(validateSpy).toHaveBeenCalledOnceWith({ lineVersionV2: {} });
  });

  it('should updateLineVersion', () => {
    const validateSpy = spyOn(Object.getPrototypeOf(service), 'validateParams').and.callThrough();

    service.updateLineVersion(1, {} as UpdateLineVersionV2);

    const callArgs = httpClient.put.calls.argsFor(0);
    expect(callArgs[0]).toEqual('http://localhost:8888/line-directory/v2/lines/versions/1'); // url
    expect(callArgs[1]).toEqual({});
    expect(callArgs[2]?.responseType).toEqual('json');
    expect((callArgs[2]?.headers as HttpHeaders).get('Content-Type')).toEqual('application/json');

    expect(validateSpy).toHaveBeenCalledOnceWith({ id: 1, updateLineVersionV2: {} });
  });
});
