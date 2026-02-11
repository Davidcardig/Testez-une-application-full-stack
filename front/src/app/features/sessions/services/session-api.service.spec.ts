import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const mockSession: Session = {
    id: 1,
    name: 'Yoga Session',
    description: 'Session de yoga relaxante',
    date: new Date('2024-12-25'),
    teacher_id: 1,
    users: [1, 2, 3],
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-02')
  };

  const mockSessions: Session[] = [
    mockSession,
    {
      id: 2,
      name: 'Méditation',
      description: 'Session de méditation',
      date: new Date('2024-12-26'),
      teacher_id: 2,
      users: [1, 3],
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-02')
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService]
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('devrait être créé', () => {
    expect(service).toBeTruthy();
  });

  describe('Tests unitaires', () => {
    describe('all()', () => {
      it('devrait retourner toutes les sessions', () => {
        service.all().subscribe((sessions) => {
          expect(sessions).toEqual(mockSessions);
          expect(sessions.length).toBe(2);
        });

        const req = httpMock.expectOne('api/session');
        expect(req.request.method).toBe('GET');
        req.flush(mockSessions);
      });

      it('devrait retourner un tableau vide quand aucune session n\'existe', () => {
        service.all().subscribe((sessions) => {
          expect(sessions).toEqual([]);
          expect(sessions.length).toBe(0);
        });

        const req = httpMock.expectOne('api/session');
        req.flush([]);
      });

      it('devrait gérer les erreurs HTTP pour all()', () => {
        service.all().subscribe(
          () => fail('devrait avoir échoué avec erreur serveur'),
          (error) => {
            expect(error.status).toBe(500);
            expect(error.statusText).toBe('Internal Server Error');
          }
        );

        const req = httpMock.expectOne('api/session');
        req.flush('Erreur serveur', { status: 500, statusText: 'Internal Server Error' });
      });
    });

    describe('detail()', () => {
      it('devrait retourner une session par id', () => {
        service.detail('1').subscribe((session) => {
          expect(session).toEqual(mockSession);
          expect(session.id).toBe(1);
          expect(session.name).toBe('Yoga Session');
        });

        const req = httpMock.expectOne('api/session/1');
        expect(req.request.method).toBe('GET');
        req.flush(mockSession);
      });

      it('devrait gérer l\'erreur 404 quand la session n\'est pas trouvée', () => {
        service.detail('999').subscribe(
          () => fail('devrait avoir échoué avec erreur 404'),
          (error) => {
            expect(error.status).toBe(404);
            expect(error.statusText).toBe('Not Found');
          }
        );

        const req = httpMock.expectOne('api/session/999');
        req.flush('Session introuvable', { status: 404, statusText: 'Not Found' });
      });

      it('devrait appeler le bon endpoint avec l\'id fourni', () => {
        const sessionId = '42';
        service.detail(sessionId).subscribe();

        const req = httpMock.expectOne(`api/session/${sessionId}`);
        expect(req.request.url).toBe(`api/session/${sessionId}`);
        req.flush(mockSession);
      });
    });

    describe('delete()', () => {
      it('devrait supprimer une session par id', () => {
        service.delete('1').subscribe((response) => {
          expect(response).toBeDefined();
        });

        const req = httpMock.expectOne('api/session/1');
        expect(req.request.method).toBe('DELETE');
        req.flush({ message: 'Session supprimée' });
      });

      it('devrait gérer l\'erreur 404 lors de la suppression d\'une session inexistante', () => {
        service.delete('999').subscribe(
          () => fail('devrait avoir échoué avec erreur 404'),
          (error) => {
            expect(error.status).toBe(404);
          }
        );

        const req = httpMock.expectOne('api/session/999');
        req.flush('Session introuvable', { status: 404, statusText: 'Not Found' });
      });

      it('devrait gérer l\'erreur 403 quand non autorisé à supprimer', () => {
        service.delete('1').subscribe(
          () => fail('devrait avoir échoué avec erreur 403'),
          (error) => {
            expect(error.status).toBe(403);
          }
        );

        const req = httpMock.expectOne('api/session/1');
        req.flush('Non autorisé', { status: 403, statusText: 'Forbidden' });
      });
    });

    describe('create()', () => {
      it('devrait créer une nouvelle session', () => {
        const newSession: Session = {
          name: 'Nouvelle Session',
          description: 'Description',
          date: new Date('2024-12-30'),
          teacher_id: 1,
          users: []
        };

        service.create(newSession).subscribe((session) => {
          expect(session).toBeDefined();
          expect(session.name).toBe('Nouvelle Session');
          expect(session.id).toBe(3);
        });

        const req = httpMock.expectOne('api/session');
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(newSession);
        req.flush({ ...newSession, id: 3 });
      });

      it('devrait gérer les erreurs de validation', () => {
        const invalidSession: Session = {
          name: '',
          description: '',
          date: new Date(),
          teacher_id: 0,
          users: []
        };

        service.create(invalidSession).subscribe(
          () => fail('devrait avoir échoué avec erreur de validation'),
          (error) => {
            expect(error.status).toBe(400);
          }
        );

        const req = httpMock.expectOne('api/session');
        req.flush('Données invalides', { status: 400, statusText: 'Bad Request' });
      });
    });

    describe('update()', () => {
      it('devrait mettre à jour une session existante', () => {
        const updatedSession: Session = {
          ...mockSession,
          name: 'Session Mise à Jour'
        };

        service.update('1', updatedSession).subscribe((session) => {
          expect(session).toBeDefined();
          expect(session.name).toBe('Session Mise à Jour');
        });

        const req = httpMock.expectOne('api/session/1');
        expect(req.request.method).toBe('PUT');
        expect(req.request.body).toEqual(updatedSession);
        req.flush(updatedSession);
      });

      it('devrait gérer l\'erreur 404 lors de la mise à jour d\'une session inexistante', () => {
        service.update('999', mockSession).subscribe(
          () => fail('devrait avoir échoué avec erreur 404'),
          (error) => {
            expect(error.status).toBe(404);
          }
        );

        const req = httpMock.expectOne('api/session/999');
        req.flush('Session introuvable', { status: 404, statusText: 'Not Found' });
      });
    });

    describe('participate()', () => {
      it('devrait permettre à un utilisateur de participer à une session', () => {
        const sessionId = '1';
        const userId = '5';

        service.participate(sessionId, userId).subscribe((response) => {
          expect(response).toBeUndefined(); // Retourne void
        });

        const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toBeNull();
        req.flush(null);
      });

      it('devrait gérer l\'erreur quand l\'utilisateur participe déjà', () => {
        service.participate('1', '1').subscribe(
          () => fail('devrait avoir échoué'),
          (error) => {
            expect(error.status).toBe(400);
          }
        );

        const req = httpMock.expectOne('api/session/1/participate/1');
        req.flush('Utilisateur déjà inscrit', { status: 400, statusText: 'Bad Request' });
      });

      it('devrait gérer l\'erreur 404 quand la session n\'existe pas', () => {
        service.participate('999', '1').subscribe(
          () => fail('devrait avoir échoué avec erreur 404'),
          (error) => {
            expect(error.status).toBe(404);
          }
        );

        const req = httpMock.expectOne('api/session/999/participate/1');
        req.flush('Session introuvable', { status: 404, statusText: 'Not Found' });
      });
    });

    describe('unParticipate()', () => {
      it('devrait permettre à un utilisateur de se désinscrire d\'une session', () => {
        const sessionId = '1';
        const userId = '2';

        service.unParticipate(sessionId, userId).subscribe((response) => {
          expect(response).toBeUndefined(); // Retourne void
        });

        const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
        expect(req.request.method).toBe('DELETE');
        req.flush(null);
      });

      it('devrait gérer l\'erreur quand l\'utilisateur n\'est pas inscrit', () => {
        service.unParticipate('1', '999').subscribe(
          () => fail('devrait avoir échoué'),
          (error) => {
            expect(error.status).toBe(400);
          }
        );

        const req = httpMock.expectOne('api/session/1/participate/999');
        req.flush('Utilisateur non inscrit', { status: 400, statusText: 'Bad Request' });
      });

      it('devrait gérer l\'erreur 404 quand la session n\'existe pas', () => {
        service.unParticipate('999', '1').subscribe(
          () => fail('devrait avoir échoué avec erreur 404'),
          (error) => {
            expect(error.status).toBe(404);
          }
        );

        const req = httpMock.expectOne('api/session/999/participate/1');
        req.flush('Session introuvable', { status: 404, statusText: 'Not Found' });
      });
    });
  });

  describe('Tests d\'intégration', () => {
    it('devrait créer puis récupérer une session', () => {
      const newSession: Session = {
        name: 'Test Session',
        description: 'Description test',
        date: new Date('2024-12-30'),
        teacher_id: 1,
        users: []
      };

      service.create(newSession).subscribe((created) => {
        expect(created.id).toBe(3);

        service.detail('3').subscribe((fetched) => {
          expect(fetched.id).toBe(3);
          expect(fetched.name).toBe('Test Session');
        });

        const detailReq = httpMock.expectOne('api/session/3');
        detailReq.flush({ ...newSession, id: 3 });
      });

      const createReq = httpMock.expectOne('api/session');
      createReq.flush({ ...newSession, id: 3 });
    });

    it('devrait récupérer toutes les sessions puis supprimer une session', () => {
      service.all().subscribe((sessions) => {
        expect(sessions.length).toBe(2);

        service.delete('1').subscribe((response) => {
          expect(response).toBeDefined();
        });

        const deleteReq = httpMock.expectOne('api/session/1');
        deleteReq.flush({ message: 'Supprimé' });
      });

      const allReq = httpMock.expectOne('api/session');
      allReq.flush(mockSessions);
    });

    it('devrait créer une session et permettre à un utilisateur d\'y participer', () => {
      const newSession: Session = {
        name: 'Session Participation',
        description: 'Test',
        date: new Date('2024-12-30'),
        teacher_id: 1,
        users: []
      };

      service.create(newSession).subscribe((created) => {
        expect(created.id).toBe(4);

        service.participate('4', '5').subscribe(() => {
          expect(true).toBe(true);
        });

        const participateReq = httpMock.expectOne('api/session/4/participate/5');
        participateReq.flush(null);
      });

      const createReq = httpMock.expectOne('api/session');
      createReq.flush({ ...newSession, id: 4 });
    });

    it('devrait mettre à jour une session puis vérifier les changements', () => {
      const updatedSession: Session = {
        ...mockSession,
        name: 'Nom Modifié'
      };

      service.update('1', updatedSession).subscribe((updated) => {
        expect(updated.name).toBe('Nom Modifié');

        service.detail('1').subscribe((fetched) => {
          expect(fetched.name).toBe('Nom Modifié');
        });

        const detailReq = httpMock.expectOne('api/session/1');
        detailReq.flush(updatedSession);
      });

      const updateReq = httpMock.expectOne('api/session/1');
      updateReq.flush(updatedSession);
    });

    it('devrait permettre à un utilisateur de participer puis de se désinscrire', () => {
      service.participate('1', '10').subscribe(() => {
        service.unParticipate('1', '10').subscribe(() => {
          expect(true).toBe(true);
        });

        const unParticipateReq = httpMock.expectOne('api/session/1/participate/10');
        unParticipateReq.flush(null);
      });

      const participateReq = httpMock.expectOne('api/session/1/participate/10');
      participateReq.flush(null);
    });

    it('devrait gérer plusieurs requêtes consécutives', () => {
      service.all().subscribe();
      service.detail('1').subscribe();
      service.detail('2').subscribe();

      const allReq = httpMock.expectOne('api/session');
      const detail1Req = httpMock.expectOne('api/session/1');
      const detail2Req = httpMock.expectOne('api/session/2');

      allReq.flush(mockSessions);
      detail1Req.flush(mockSession);
      detail2Req.flush(mockSessions[1]);
    });

    it('devrait gérer les opérations mixtes succès/échec', () => {
      service.detail('1').subscribe((session) => {
        expect(session).toEqual(mockSession);
      });

      service.detail('999').subscribe(
        () => fail('devrait échouer'),
        (error) => {
          expect(error.status).toBe(404);
        }
      );

      const req1 = httpMock.expectOne('api/session/1');
      const req2 = httpMock.expectOne('api/session/999');

      req1.flush(mockSession);
      req2.flush('Introuvable', { status: 404, statusText: 'Not Found' });
    });
  });
});
