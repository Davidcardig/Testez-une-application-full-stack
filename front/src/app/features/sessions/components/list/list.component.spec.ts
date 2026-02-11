import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { Session } from '../../interfaces/session.interface';

import { ListComponent } from './list.component';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionService: SessionService;
  let sessionApiService: SessionApiService;
  let httpMock: HttpTestingController;

  const mockSessions: Session[] = [
    {
      id: 1,
      name: 'Yoga Session',
      description: 'Session de yoga relaxante',
      date: new Date('2024-12-25'),
      teacher_id: 1,
      users: [1, 2, 3],
      createdAt: new Date('2024-01-01'),
      updatedAt: new Date('2024-01-02')
    },
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

  const mockSessionService = {
    sessionInformation: {
      token: 'test-token',
      type: 'Bearer',
      id: 1,
      username: 'admin',
      firstName: 'Admin',
      lastName: 'User',
      admin: true
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [
        HttpClientTestingModule,
        MatCardModule,
        MatIconModule,
        RouterTestingModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        SessionApiService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    sessionApiService = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('devrait être créé', () => {
    expect(component).toBeTruthy();
  });

  describe('Tests unitaires', () => {
    describe('Propriété sessions$', () => {
      it('devrait être défini comme un Observable', () => {
        expect(component.sessions$).toBeDefined();
        expect(typeof component.sessions$.subscribe).toBe('function');
      });

      it('devrait appeler sessionApiService.all() lors de l\'initialisation', () => {
        const spy = jest.spyOn(sessionApiService, 'all').mockReturnValue(of(mockSessions));

        const newComponent = new ListComponent(sessionService, sessionApiService);

        expect(spy).toHaveBeenCalled();
        newComponent.sessions$.subscribe(sessions => {
          expect(sessions).toEqual(mockSessions);
        });
      });

      it('devrait émettre les sessions depuis l\'API', (done) => {
        // S'abonner avant detectChanges pour capturer l'émission
        component.sessions$.subscribe(sessions => {
          expect(sessions).toEqual(mockSessions);
          expect(sessions.length).toBe(2);
          done();
        });

        fixture.detectChanges();

        // Il peut y avoir plusieurs requêtes (souscription manuelle + template)
        const reqs = httpMock.match('api/session');
        expect(reqs.length).toBeGreaterThanOrEqual(1);
        reqs.forEach(req => req.flush(mockSessions));
      });
    });

    describe('Getter user', () => {
      it('devrait retourner les informations de session de l\'utilisateur', () => {
        const user = component.user;

        expect(user).toBeDefined();
        expect(user?.admin).toBe(true);
        expect(user?.username).toBe('admin');
      });

      it('devrait retourner undefined si aucune session utilisateur n\'existe', () => {
        const mockEmptySessionService = {
          sessionInformation: undefined
        };

        TestBed.resetTestingModule();
        TestBed.configureTestingModule({
          declarations: [ListComponent],
          imports: [HttpClientTestingModule, MatCardModule, MatIconModule, RouterTestingModule],
          providers: [
            { provide: SessionService, useValue: mockEmptySessionService },
            SessionApiService
          ]
        });

        const newFixture = TestBed.createComponent(ListComponent);
        const newComponent = newFixture.componentInstance;

        expect(newComponent.user).toBeUndefined();
      });

      it('devrait indiquer si l\'utilisateur est admin', () => {
        expect(component.user?.admin).toBe(true);
      });

      it('devrait indiquer si l\'utilisateur n\'est pas admin', () => {
        const mockNonAdminSessionService = {
          sessionInformation: {
            token: 'test-token',
            type: 'Bearer',
            id: 2,
            username: 'user',
            firstName: 'Regular',
            lastName: 'User',
            admin: false
          }
        };

        TestBed.resetTestingModule();
        TestBed.configureTestingModule({
          declarations: [ListComponent],
          imports: [HttpClientTestingModule, MatCardModule, MatIconModule, RouterTestingModule],
          providers: [
            { provide: SessionService, useValue: mockNonAdminSessionService },
            SessionApiService
          ]
        });

        const newFixture = TestBed.createComponent(ListComponent);
        const newComponent = newFixture.componentInstance;

        expect(newComponent.user?.admin).toBe(false);
      });
    });

    describe('Constructeur et injection de dépendances', () => {
      it('devrait injecter SessionService', () => {
        expect(component['sessionService']).toBeDefined();
        expect(component['sessionService']).toBe(sessionService);
      });

      it('devrait injecter SessionApiService', () => {
        expect(component['sessionApiService']).toBeDefined();
        expect(component['sessionApiService']).toBe(sessionApiService);
      });
    });
  });

  describe('Tests d\'intégration', () => {
    it('devrait charger et afficher les sessions au démarrage', () => {
      fixture.detectChanges();

      const req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('GET');
      req.flush(mockSessions);

      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const sessionCards = compiled.querySelectorAll('.item');

      expect(sessionCards.length).toBe(2);
    });

    it('devrait afficher le nom de chaque session', () => {
      fixture.detectChanges();

      const req = httpMock.expectOne('api/session');
      req.flush(mockSessions);

      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const sessionTitles = compiled.querySelectorAll('mat-card-title');

      // Le premier titre est "Rentals available", les suivants sont les noms des sessions
      expect(sessionTitles[1].textContent).toContain('Yoga Session');
      expect(sessionTitles[2].textContent).toContain('Méditation');
    });

    it('devrait afficher le bouton Create pour un utilisateur admin', () => {
      fixture.detectChanges();

      const req = httpMock.expectOne('api/session');
      req.flush(mockSessions);

      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const createButton = compiled.querySelector('button[routerLink="create"]');

      expect(createButton).toBeTruthy();
      expect(createButton.textContent).toContain('Create');
    });

    it('ne devrait pas afficher le bouton Create pour un utilisateur non-admin', () => {
      const mockNonAdminSessionService = {
        sessionInformation: {
          token: 'test-token',
          type: 'Bearer',
          id: 2,
          username: 'user',
          firstName: 'Regular',
          lastName: 'User',
          admin: false
        }
      };

      TestBed.resetTestingModule();
      TestBed.configureTestingModule({
        declarations: [ListComponent],
        imports: [HttpClientTestingModule, MatCardModule, MatIconModule, RouterTestingModule],
        providers: [
          { provide: SessionService, useValue: mockNonAdminSessionService },
          SessionApiService
        ]
      }).compileComponents();

      const newFixture = TestBed.createComponent(ListComponent);
      newFixture.detectChanges();

      const newHttpMock = TestBed.inject(HttpTestingController);
      const req = newHttpMock.expectOne('api/session');
      req.flush(mockSessions);

      newFixture.detectChanges();

      const compiled = newFixture.nativeElement;
      const createButton = compiled.querySelector('button[routerLink="create"]');

      expect(createButton).toBeFalsy();

      newHttpMock.verify();
    });

    it('devrait afficher les boutons Edit pour un utilisateur admin', () => {
      fixture.detectChanges();

      const req = httpMock.expectOne('api/session');
      req.flush(mockSessions);

      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const editButtons = compiled.querySelectorAll('button mat-icon');

      const editIcons = Array.from(editButtons).filter((icon: any) =>
        icon.textContent.trim() === 'edit'
      );

      expect(editIcons.length).toBeGreaterThan(0);
    });

    it('devrait afficher les boutons Detail pour toutes les sessions', () => {
      fixture.detectChanges();

      const req = httpMock.expectOne('api/session');
      req.flush(mockSessions);

      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const detailButtons = compiled.querySelectorAll('button mat-icon');

      const searchIcons = Array.from(detailButtons).filter((icon: any) =>
        icon.textContent.trim() === 'search'
      );

      expect(searchIcons.length).toBe(2);
    });

    it('devrait afficher le message "Rentals available"', () => {
      fixture.detectChanges();

      const req = httpMock.expectOne('api/session');
      req.flush(mockSessions);

      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const title = compiled.querySelector('mat-card-title');

      expect(title.textContent).toContain('Rentals available');
    });

    it('devrait afficher la description de chaque session', () => {
      fixture.detectChanges();

      const req = httpMock.expectOne('api/session');
      req.flush(mockSessions);

      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const descriptions = compiled.querySelectorAll('mat-card-content p');

      expect(descriptions[0].textContent?.trim()).toContain('Session de yoga relaxante');
      expect(descriptions[1].textContent?.trim()).toContain('Session de méditation');
    });

    it('devrait afficher une liste vide quand il n\'y a pas de sessions', () => {
      fixture.detectChanges();

      const req = httpMock.expectOne('api/session');
      req.flush([]);

      fixture.detectChanges();

      const compiled = fixture.nativeElement;
      const sessionCards = compiled.querySelectorAll('.item');

      expect(sessionCards.length).toBe(0);
    });

    it('devrait réagir aux changements de sessions$', (done) => {
      const newSessions: Session[] = [
        {
          id: 3,
          name: 'Pilates',
          description: 'Session de Pilates',
          date: new Date('2024-12-27'),
          teacher_id: 3,
          users: [4, 5],
          createdAt: new Date('2024-01-01'),
          updatedAt: new Date('2024-01-02')
        }
      ];

      jest.spyOn(sessionApiService, 'all').mockReturnValue(of(newSessions));

      const newComponent = new ListComponent(sessionService, sessionApiService);

      newComponent.sessions$.subscribe(sessions => {
        expect(sessions).toEqual(newSessions);
        expect(sessions.length).toBe(1);
        expect(sessions[0].name).toBe('Pilates');
        done();
      });
    });
  });
});
