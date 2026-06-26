import { beforeEach, describe, expect, it, MockInstance, vi } from 'vitest';
import { AtlasApiService } from './atlas-api.service';
import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../core/auth/user/user.service';
import { BulkImportRequest } from '../model/bulkImportRequest';
import { ImportType } from '../model/importType';
import { BusinessObjectType } from '../model/businessObjectType';
import { ApplicationType } from '../model/applicationType';

type HttpClientMock = Pick<{ [P in keyof HttpClient]: MockInstance }, 'get' | 'put' | 'post' | 'delete'>;

describe('AtlasApiService', () => {
  let service: AtlasApiService;

  let httpClient: HttpClientMock;

  beforeEach(() => {
    httpClient = {
      get: vi.fn(),
      put: vi.fn(),
      post: vi.fn(),
      delete: vi.fn(),
    };

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
    expect(() => service.validateParams({ prop1: '123', prop2: undefined })).toThrowError(
      "Required parameter 'prop2' is null or undefined."
    );
    expect(() => service.validateParams({ prop1: '123', prop2: null })).toThrowError(
      "Required parameter 'prop2' is null or undefined."
    );
  });

  it('should not throw on validateParams', () => {
    expect(() => service.validateParams({ prop1: '123', prop2: 'test' })).not.toThrow();
  });

  it('should create HttpParams with array', () => {
    const httpParams = service.paramsOf({
      array: ['uno', 'due'],
      string: '123',
    });
    expect(httpParams.getAll('array')).toEqual(['uno', 'due']);
    expect(httpParams.get('string')).toEqual('123');
  });

  it('should create HttpParams with set', () => {
    const httpParams = service.paramsOf({
      set: new Set(['uno', 'due']),
      string: '123',
    });
    expect(httpParams.getAll('set')).toEqual(['uno', 'due']);
    expect(httpParams.get('string')).toEqual('123');
  });

  it('should get', () => {
    service.get('/path', new HttpParams().append('param', 'uno'));

    const callArgs = httpClient.get.mock.calls[0];
    expect(callArgs[0]).toEqual('http://localhost:8888/path');
    expect(callArgs[1].headers.get('Accept')).toEqual('*/*');
    expect(callArgs[1].params.get('param')).toEqual('uno');
    expect(callArgs[1].responseType).toEqual('json');
  });

  it('should getBlob', () => {
    service.getBlob('/path', new HttpParams().append('param', 'uno'));

    const callArgs = httpClient.get.mock.calls[0];
    expect(callArgs[0]).toEqual('http://localhost:8888/path');
    expect(callArgs[1].headers.get('Accept')).toEqual('*/*');
    expect(callArgs[1].params.get('param')).toEqual('uno');
    expect(callArgs[1].responseType).toEqual('blob');
  });

  it('should put', () => {
    service.put('/path', { value: 'test' });

    const callArgs = httpClient.put.mock.calls[0];
    expect(callArgs[0]).toEqual('http://localhost:8888/path');
    expect(callArgs[1]).toEqual({ value: 'test' });
    expect(callArgs[2].headers.get('Content-Type')).toEqual('application/json');
    expect(callArgs[2].responseType).toEqual('json');
  });

  it('should post', () => {
    service.post('/path', { value: 'test' });

    const callArgs = httpClient.post.mock.calls[0];
    expect(callArgs[0]).toEqual('http://localhost:8888/path');
    expect(callArgs[1]).toEqual({ value: 'test' });
    expect(callArgs[2].headers.get('Content-Type')).toEqual('application/json');
    expect(callArgs[2].responseType).toEqual('json');
  });

  it('should delete', () => {
    service.delete('/path');

    const callArgs = httpClient.delete.mock.calls[0];
    expect(callArgs[0]).toEqual('http://localhost:8888/path');
  });

  it('should createFormData with blob', async () => {
    const file = new File(['test'], 'test.txt');
    const data = service.createFormData({ file, object: { attribute: 1 } });
    expect(data.get('file')).toBe(file);

    const blob = data.get('object') as File;
    expect(JSON.parse(await blob.text())).toEqual({ attribute: 1 });
  });

  it('should createFormData with array', async () => {
    const array = ['a', 'b'];
    const data = service.createFormData({ array });

    expect(data.getAll('array')).toEqual(array);
  });

  it('should createFormData without blob', async () => {
    const bulkImportRequest: BulkImportRequest = {
      importType: ImportType.Terminate,
      objectType: BusinessObjectType.ServicePoint,
      applicationType: ApplicationType.Sepodi,
    };

    const data = service.createFormData({ bulkImportRequest });
    const values = data.getAll('bulkImportRequest') as Blob[];
    const blob = values[0];
    const text = await blob.text();

    expect(JSON.parse(text)).toEqual(bulkImportRequest);
  });
});
