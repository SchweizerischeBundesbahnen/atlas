import {AtlasApiService} from './atlasApi.service';
import {TestBed} from '@angular/core/testing';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {UserService} from '../../core/auth/user/user.service';
import SpyObj = jasmine.SpyObj;

describe('AtlasApiService', () => {
  let service: AtlasApiService;

  let httpClient: SpyObj<HttpClient>;

  beforeEach(() => {
    httpClient = jasmine.createSpyObj(['get', 'put', 'post', 'delete']);
    TestBed.configureTestingModule({
      providers: [
        AtlasApiService,
        { provide: HttpClient, useValue: httpClient },
        { provide: UserService, useValue: { loggedIn: true } },
      ],
    });
    service = TestBed.inject(AtlasApiService);
  });

  it('should throw on validateParams', () => {
    expect(() => service.validateParams({ prop1: '123', prop2: undefined }))
      .toThrowError('Required parameter \'prop2\' is null or undefined.');
    expect(() => service.validateParams({ prop1: '123', prop2: null }))
      .toThrowError('Required parameter \'prop2\' is null or undefined.');
  });

  it('should not throw on validateParams', () => {
    expect(() => service.validateParams({ prop1: '123', prop2: 'test' })).not.toThrow();
  });

  it('should create HttpParams', () => {
    const httpParams = service.paramsOf({
      array: ['uno', 'due'],
      string: '123',
    });
    expect(httpParams.getAll('array')).toEqual(['uno', 'due']);
    expect(httpParams.get('string')).toEqual('123');
  });

  it('should get', () => {
    service.get('/path', new HttpParams().append('param', 'uno'));

    const callArgs = httpClient.get.calls.argsFor(0);
    expect(callArgs[0]).toEqual('http://localhost:8888/path');
    expect((callArgs[1]?.headers as HttpHeaders).get('Accept')).toEqual('*/*');
    expect((callArgs[1]?.params as HttpParams).get('param')).toEqual('uno');
    expect(callArgs[1]?.responseType).toEqual('json');
  });

  it('should getBlob', () => {
    service.getBlob('/path', new HttpParams().append('param', 'uno'));

    const callArgs = httpClient.get.calls.argsFor(0);
    expect(callArgs[0]).toEqual('http://localhost:8888/path');
    expect((callArgs[1]?.headers as HttpHeaders).get('Accept')).toEqual('*/*');
    expect((callArgs[1]?.params as HttpParams).get('param')).toEqual('uno');
    expect(callArgs[1]?.responseType as any).toEqual('blob');
  });

  it('should put', () => {
    service.put('/path', { value: 'test' });

    const callArgs = httpClient.put.calls.argsFor(0);
    expect(callArgs[0]).toEqual('http://localhost:8888/path');
    expect(callArgs[1]).toEqual({ value: 'test' });
    expect((callArgs[2]?.headers as HttpHeaders).get('Content-Type')).toEqual('application/json');
    expect(callArgs[2]?.responseType).toEqual('json');
  });

  it('should post', () => {
    service.post('/path', { value: 'test' });

    const callArgs = httpClient.post.calls.argsFor(0);
    expect(callArgs[0]).toEqual('http://localhost:8888/path');
    expect(callArgs[1]).toEqual({ value: 'test' });
    expect((callArgs[2]?.headers as HttpHeaders).get('Content-Type')).toEqual('application/json');
    expect(callArgs[2]?.responseType).toEqual('json');
  });

  it('should delete', () => {
    service.delete('/path');

    const callArgs = httpClient.delete.calls.argsFor(0);
    expect(callArgs[0]).toEqual('http://localhost:8888/path');
  });

  it('should createFormData', async () => {
    const file = new File(['test'], 'test.txt');
    const data = service.createFormData({ file, object: { attribute: 1 } });
    expect(data.get('file')).toBe(file);

    const blob = data.get('object') as Blob;
    const text = await new Response(blob).text();
    expect(JSON.parse(text)).toEqual({ attribute: 1 });
  });
});
