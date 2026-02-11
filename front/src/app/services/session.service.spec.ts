import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('devrait être créé', () => {
    expect(service).toBeTruthy();
  });

  describe('Tests unitaires', () => {
    const mockUser: SessionInformation = {
      token: 'test-token',
      type: 'Bearer',
      id: 1,
      username: 'testuser',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    };

    it('devrait avoir les valeurs initiales correctes', () => {
      expect(service.isLogged).toBe(false);
      expect(service.sessionInformation).toBeUndefined();
    });

    it('devrait gérer la connexion d\'un utilisateur', () => {
      service.logIn(mockUser);

      expect(service.sessionInformation).toEqual(mockUser);
      expect(service.isLogged).toBe(true);
    });

    it('devrait gérer la déconnexion d\'un utilisateur', () => {
      service.logIn(mockUser);
      service.logOut();

      expect(service.sessionInformation).toBeUndefined();
      expect(service.isLogged).toBe(false);
    });

    it('devrait émettre les changements via l\'observable $isLogged', (done) => {
      const emissions: boolean[] = [];

      service.$isLogged().subscribe((isLogged) => {
        emissions.push(isLogged);

        if (emissions.length === 3) {
          expect(emissions).toEqual([false, true, false]);
          done();
        }
      });

      service.logIn(mockUser);
      service.logOut();
    });
  });


  describe('Tests d\'intégration', () => {
    it('devrait gérer plusieurs sessions utilisateurs successives', () => {
      const user1: SessionInformation = {
        token: 'token-1',
        type: 'Bearer',
        id: 1,
        username: 'user1',
        firstName: 'User',
        lastName: 'One',
        admin: false
      };

      const user2: SessionInformation = {
        token: 'token-2',
        type: 'Bearer',
        id: 2,
        username: 'user2',
        firstName: 'User',
        lastName: 'Two',
        admin: true
      };

      service.logIn(user1);
      expect(service.sessionInformation).toEqual(user1);

      service.logIn(user2);
      expect(service.sessionInformation).toEqual(user2);
      expect(service.sessionInformation?.admin).toBe(true);
    });
  });
});
