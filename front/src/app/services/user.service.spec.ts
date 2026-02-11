import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';
import { User } from '../interfaces/user.interface';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('devrait être créé', () => {
    expect(service).toBeTruthy();
  });

  describe('Tests unitaires', () => {
    describe('getById()', () => {
      it('devrait retourner un seul User par id', () => {
        const mockUser: User = {
          id: 1,
          email: 'test@example.com',
          lastName: 'Dupont',
          firstName: 'Jean',
          admin: false,
          password: 'hashed_password',
          createdAt: new Date('2024-01-01'),
          updatedAt: new Date('2024-01-02')
        };

        service.getById('1').subscribe((user) => {
          expect(user).toEqual(mockUser);
          expect(user.id).toBe(1);
          expect(user.email).toBe('test@example.com');
          expect(user.admin).toBe(false);
        });

        const req = httpMock.expectOne('api/user/1');
        expect(req.request.method).toBe('GET');
        req.flush(mockUser);
      });

      it('should return an admin user', () => {
        const mockAdminUser: User = {
          id: 2,
          email: 'admin@example.com',
          lastName: 'Admin',
          firstName: 'Super',
          admin: true,
          password: 'hashed_password',
          createdAt: new Date('2024-01-01')
        };

        service.getById('2').subscribe((user) => {
          expect(user.admin).toBe(true);
          expect(user.email).toBe('admin@example.com');
        });

        const req = httpMock.expectOne('api/user/2');
        req.flush(mockAdminUser);
      });

      it('should call the correct API endpoint with provided id', () => {
        const userId = '42';
        const mockUser: User = {
          id: 42,
          email: 'user42@example.com',
          lastName: 'Test',
          firstName: 'User',
          admin: false,
          password: 'password',
          createdAt: new Date()
        };

        service.getById(userId).subscribe();

        const req = httpMock.expectOne(`api/user/${userId}`);
        expect(req.request.url).toBe(`api/user/${userId}`);
        req.flush(mockUser);
      });

      it('devrait gérer l\'erreur HTTP 404 quand l\'utilisateur n\'est pas trouvé', () => {
        service.getById('999').subscribe(
          () => fail('should have failed with 404 error'),
          (error) => {
            expect(error.status).toBe(404);
            expect(error.statusText).toBe('Not Found');
          }
        );

        const req = httpMock.expectOne('api/user/999');
        req.flush('User not found', { status: 404, statusText: 'Not Found' });
      });

      it('devrait gérer l\'erreur HTTP 500', () => {
        service.getById('1').subscribe(
          () => fail('should have failed with server error'),
          (error) => {
            expect(error.status).toBe(500);
            expect(error.statusText).toBe('Internal Server Error');
          }
        );

        const req = httpMock.expectOne('api/user/1');
        req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
      });

      it('devrait gérer un utilisateur sans le champ updatedAt', () => {
        const mockUser: User = {
          id: 1,
          email: 'test@example.com',
          lastName: 'Dupont',
          firstName: 'Jean',
          admin: false,
          password: 'password',
          createdAt: new Date('2024-01-01')
        };

        service.getById('1').subscribe((user) => {
          expect(user.updatedAt).toBeUndefined();
        });

        const req = httpMock.expectOne('api/user/1');
        req.flush(mockUser);
      });
    });

    describe('delete()', () => {
      it('devrait supprimer un utilisateur par id', () => {
        const userId = '1';

        service.delete(userId).subscribe((response) => {
          expect(response).toBeDefined();
        });

        const req = httpMock.expectOne(`api/user/${userId}`);
        expect(req.request.method).toBe('DELETE');
        req.flush({ message: 'User deleted successfully' });
      });

      it('devrait appeler le bon endpoint API avec l\'id fourni', () => {
        const userId = '42';

        service.delete(userId).subscribe();

        const req = httpMock.expectOne(`api/user/${userId}`);
        expect(req.request.url).toBe(`api/user/${userId}`);
        expect(req.request.method).toBe('DELETE');
        req.flush({});
      });

      it('devrait gérer une suppression réussie avec une réponse vide', () => {
        service.delete('1').subscribe((response) => {
          expect(response).toEqual({});
        });

        const req = httpMock.expectOne('api/user/1');
        req.flush({});
      });

      it('devrait gérer l\'erreur HTTP 404 lors de la tentative de suppression d\'un utilisateur inexistant', () => {
        service.delete('999').subscribe(
          () => fail('should have failed with 404 error'),
          (error) => {
            expect(error.status).toBe(404);
            expect(error.statusText).toBe('Not Found');
          }
        );

        const req = httpMock.expectOne('api/user/999');
        req.flush('User not found', { status: 404, statusText: 'Not Found' });
      });

      it('devrait gérer l\'erreur HTTP 403 quand non autorisé à supprimer', () => {
        service.delete('1').subscribe(
          () => fail('should have failed with 403 error'),
          (error) => {
            expect(error.status).toBe(403);
            expect(error.statusText).toBe('Forbidden');
          }
        );

        const req = httpMock.expectOne('api/user/1');
        req.flush('Unauthorized', { status: 403, statusText: 'Forbidden' });
      });

      it('devrait gérer l\'erreur HTTP 500 lors de la suppression', () => {
        service.delete('1').subscribe(
          () => fail('should have failed with server error'),
          (error) => {
            expect(error.status).toBe(500);
          }
        );

        const req = httpMock.expectOne('api/user/1');
        req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
      });

      it('devrait supprimer un utilisateur avec différents formats d\'id', () => {
        const userId = '123';

        service.delete(userId).subscribe();

        const req = httpMock.expectOne(`api/user/${userId}`);
        expect(req.request.method).toBe('DELETE');
        req.flush({ success: true });
      });
    });
  });

  describe('Tests d\'intégration', () => {
    it('devrait récupérer un utilisateur par id puis supprimer l\'utilisateur', (done) => {
      const mockUser: User = {
        id: 1,
        email: 'test@example.com',
        lastName: 'Dupont',
        firstName: 'Jean',
        admin: false,
        password: 'password',
        createdAt: new Date('2024-01-01')
      };

      const userId = '1';

      service.getById(userId).subscribe((user) => {
        expect(user).toEqual(mockUser);

        service.delete(userId).subscribe((response) => {
          expect(response).toBeDefined();
          done();
        });

        const deleteReq = httpMock.expectOne(`api/user/${userId}`);
        expect(deleteReq.request.method).toBe('DELETE');
        deleteReq.flush({ message: 'User deleted' });
      });

      const getReq = httpMock.expectOne(`api/user/${userId}`);
      expect(getReq.request.method).toBe('GET');
      getReq.flush(mockUser);
    });

    it('devrait gérer plusieurs requêtes consécutives à getById', () => {
      const mockUser1: User = {
        id: 1,
        email: 'user1@example.com',
        lastName: 'User',
        firstName: 'One',
        admin: false,
        password: 'password',
        createdAt: new Date()
      };

      const mockUser2: User = {
        id: 2,
        email: 'user2@example.com',
        lastName: 'User',
        firstName: 'Two',
        admin: true,
        password: 'password',
        createdAt: new Date()
      };

      service.getById('1').subscribe((user) => {
        expect(user.id).toBe(1);
      });

      service.getById('2').subscribe((user) => {
        expect(user.id).toBe(2);
      });

      const req1 = httpMock.expectOne('api/user/1');
      const req2 = httpMock.expectOne('api/user/2');

      req1.flush(mockUser1);
      req2.flush(mockUser2);
    });

    it('devrait gérer plusieurs requêtes consécutives de delete', () => {
      service.delete('1').subscribe((response) => {
        expect(response).toBeDefined();
      });

      service.delete('2').subscribe((response) => {
        expect(response).toBeDefined();
      });

      const req1 = httpMock.expectOne('api/user/1');
      const req2 = httpMock.expectOne('api/user/2');

      expect(req1.request.method).toBe('DELETE');
      expect(req2.request.method).toBe('DELETE');

      req1.flush({ success: true });
      req2.flush({ success: true });
    });

    it('devrait gérer des opérations mixtes réussies et échouées', () => {
      const mockUser: User = {
        id: 1,
        email: 'test@example.com',
        lastName: 'Test',
        firstName: 'User',
        admin: false,
        password: 'password',
        createdAt: new Date()
      };

      service.getById('1').subscribe((user) => {
        expect(user).toEqual(mockUser);
      });

      service.delete('999').subscribe(
        () => fail('should have failed'),
        (error) => {
          expect(error.status).toBe(404);
        }
      );

      const getReq = httpMock.expectOne('api/user/1');
      const deleteReq = httpMock.expectOne('api/user/999');

      getReq.flush(mockUser);
      deleteReq.flush('Not found', { status: 404, statusText: 'Not Found' });
    });

    it('devrait gérer une erreur sur getById suivie d\'une suppression réussie', () => {
      service.getById('999').subscribe(
        () => fail('should have failed'),
        (error) => {
          expect(error.status).toBe(404);
        }
      );

      service.delete('1').subscribe((response) => {
        expect(response).toBeDefined();
      });

      const getReq = httpMock.expectOne('api/user/999');
      const deleteReq = httpMock.expectOne('api/user/1');

      getReq.flush('Not found', { status: 404, statusText: 'Not Found' });
      deleteReq.flush({ success: true });
    });
  });
});
