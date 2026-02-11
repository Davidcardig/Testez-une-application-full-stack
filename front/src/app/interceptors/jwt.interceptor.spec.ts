import { TestBed } from '@angular/core/testing';
import { HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { expect } from '@jest/globals';
import { of } from 'rxjs';

import { JwtInterceptor } from './jwt.interceptor';
import { SessionService } from '../services/session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('JwtInterceptor', () => {
  let interceptor: JwtInterceptor;
  let sessionService: SessionService;
  let httpHandler: HttpHandler;
  let httpRequest: HttpRequest<any>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JwtInterceptor, SessionService]
    });
    interceptor = TestBed.inject(JwtInterceptor);
    sessionService = TestBed.inject(SessionService);
    httpHandler = {
      handle: jest.fn(() => of({} as HttpEvent<any>))
    } as any;
    httpRequest = new HttpRequest('GET', '/api/test');
  });

  it('devrait être créé', () => {
    expect(interceptor).toBeTruthy();
  });

  it('devrait ajouter le header Authorization quand l\'utilisateur est connecté', () => {
    const mockSession: SessionInformation = {
      token: 'test-token',
      type: 'Bearer',
      id: 1,
      username: 'testuser',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    };
    sessionService.logIn(mockSession);

    interceptor.intercept(httpRequest, httpHandler);

    const modifiedRequest = (httpHandler.handle as jest.Mock).mock.calls[0][0];
    expect(modifiedRequest.headers.get('Authorization')).toBe('Bearer test-token');
  });

  it('devrait ne pas ajouter le header Authorization quand l\'utilisateur n\'est pas connecté', () => {
    sessionService.logOut();

    interceptor.intercept(httpRequest, httpHandler);

    expect(httpHandler.handle).toHaveBeenCalledWith(httpRequest);
  });

  it('devrait retourner le résultat de next.handle()', () => {
    const mockResponse = of({} as HttpEvent<any>);
    httpHandler.handle = jest.fn().mockReturnValue(mockResponse);

    const result = interceptor.intercept(httpRequest, httpHandler);

    expect(result).toBe(mockResponse);
  });
});
