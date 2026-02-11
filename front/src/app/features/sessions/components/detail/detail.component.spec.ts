import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { DetailComponent } from './detail.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('DetailComponent - Tests Unitaires', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionService: SessionService;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let matSnackBar: MatSnackBar;
  let router: Router;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  };

  const mockSession = {
    id: 1,
    name: 'Yoga Session',
    description: 'Description de la session',
    date: new Date('2026-01-20'),
    teacher_id: 1,
    users: [1, 2, 3],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockTeacher = {
    id: 1,
    firstName: 'Jean',
    lastName: 'Dupont',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1')
      }
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        SessionApiService,
        TeacherService
      ],
    })
      .compileComponents();

    sessionService = TestBed.inject(SessionService);
    sessionApiService = TestBed.inject(SessionApiService);
    teacherService = TestBed.inject(TeacherService);
    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
  });

  it('devrait créer le composant', () => {
    expect(component).toBeTruthy();
  });

  it('devrait initialiser les propriétés dans le constructeur', () => {
    expect(component.sessionId).toBe('1');
    expect(component.isAdmin).toBe(true);
    expect(component.userId).toBe('1');
  });

  describe('méthode ngOnInit', () => {
    it('devrait appeler fetchSession lors de l\'initialisation', () => {
      const fetchSessionSpy = jest.spyOn(component as any, 'fetchSession').mockImplementation();

      component.ngOnInit();

      expect(fetchSessionSpy).toHaveBeenCalled();
    });
  });

  describe('méthode back', () => {
    it('devrait appeler window.history.back', () => {
      const backSpy = jest.spyOn(window.history, 'back').mockImplementation();

      component.back();

      expect(backSpy).toHaveBeenCalled();
    });
  });

  describe('méthode delete', () => {
    it('devrait supprimer la session et naviguer vers /sessions', () => {
      const deleteSpy = jest.spyOn(sessionApiService, 'delete').mockReturnValue(of({}));
      const snackBarSpy = jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);
      const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

      component.delete();

      expect(deleteSpy).toHaveBeenCalledWith('1');
      expect(snackBarSpy).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
      expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
    });
  });

  describe('méthode participate', () => {
    it('devrait appeler sessionApiService.participate et rafraîchir la session', () => {
      const participateSpy = jest.spyOn(sessionApiService, 'participate').mockReturnValue(of(void 0));
      const fetchSessionSpy = jest.spyOn(component as any, 'fetchSession').mockImplementation();

      component.participate();

      expect(participateSpy).toHaveBeenCalledWith('1', '1');
      expect(fetchSessionSpy).toHaveBeenCalled();
    });
  });

  describe('méthode unParticipate', () => {
    it('devrait appeler sessionApiService.unParticipate et rafraîchir la session', () => {
      const unParticipateSpy = jest.spyOn(sessionApiService, 'unParticipate').mockReturnValue(of(void 0));
      const fetchSessionSpy = jest.spyOn(component as any, 'fetchSession').mockImplementation();

      component.unParticipate();

      expect(unParticipateSpy).toHaveBeenCalledWith('1', '1');
      expect(fetchSessionSpy).toHaveBeenCalled();
    });
  });

  describe('méthode fetchSession', () => {
    it('devrait récupérer les détails de la session et du professeur', () => {
      const detailSpy = jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));
      const teacherDetailSpy = jest.spyOn(teacherService, 'detail').mockReturnValue(of(mockTeacher));

      component['fetchSession']();

      expect(detailSpy).toHaveBeenCalledWith('1');
      expect(component.session).toEqual(mockSession);
      expect(component.isParticipate).toBe(true);
      expect(teacherDetailSpy).toHaveBeenCalledWith('1');
      expect(component.teacher).toEqual(mockTeacher);
    });

    it('devrait définir isParticipate à false si l\'utilisateur ne participe pas', () => {
      const sessionWithoutUser = { ...mockSession, users: [2, 3] };
      jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(sessionWithoutUser));
      jest.spyOn(teacherService, 'detail').mockReturnValue(of(mockTeacher));

      component['fetchSession']();

      expect(component.isParticipate).toBe(false);
    });
  });

  describe('propriété isAdmin', () => {
    it('devrait être true pour un utilisateur admin', () => {
      expect(component.isAdmin).toBe(true);
    });

    it('devrait être false pour un utilisateur non-admin', async () => {
      const mockSessionServiceNonAdmin = {
        sessionInformation: {
          admin: false,
          id: 2
        }
      };

      await TestBed.resetTestingModule();
      await TestBed.configureTestingModule({
        imports: [
          RouterTestingModule.withRoutes([]),
          HttpClientModule,
          MatSnackBarModule,
          ReactiveFormsModule
        ],
        declarations: [DetailComponent],
        providers: [
          { provide: SessionService, useValue: mockSessionServiceNonAdmin },
          { provide: ActivatedRoute, useValue: mockActivatedRoute },
          SessionApiService,
          TeacherService
        ],
      }).compileComponents();

      const newFixture = TestBed.createComponent(DetailComponent);
      const newComponent = newFixture.componentInstance;

      expect(newComponent.isAdmin).toBe(false);
    });
  });
});

describe('DetailComponent - Tests d\'Intégration', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let httpMock: HttpTestingController;
  let router: Router;
  let matSnackBar: MatSnackBar;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1')
      }
    }
  };

  const mockSession = {
    id: 1,
    name: 'Yoga Session',
    description: 'Description de la session',
    date: new Date('2026-01-20'),
    teacher_id: 1,
    users: [1, 2, 3],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockTeacher = {
    id: 1,
    firstName: 'Jean',
    lastName: 'Dupont',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientTestingModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ],
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('devrait charger les détails de la session et du professeur lors de l\'initialisation', () => {
    fixture.detectChanges();

    const sessionReq = httpMock.expectOne('api/session/1');
    expect(sessionReq.request.method).toBe('GET');
    sessionReq.flush(mockSession);

    const teacherReq = httpMock.expectOne('api/teacher/1');
    expect(teacherReq.request.method).toBe('GET');
    teacherReq.flush(mockTeacher);

    expect(component.session).toEqual(mockSession);
    expect(component.teacher).toEqual(mockTeacher);
    expect(component.isParticipate).toBe(true);
  });

  it('devrait supprimer une session et afficher un message de confirmation', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    const snackBarSpy = jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);

    component.delete();

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});

    expect(snackBarSpy).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('devrait permettre à un utilisateur de participer à une session', () => {
    component.participate();

    const participateReq = httpMock.expectOne('api/session/1/participate/1');
    expect(participateReq.request.method).toBe('POST');
    participateReq.flush({});

    const sessionReq = httpMock.expectOne('api/session/1');
    expect(sessionReq.request.method).toBe('GET');
    sessionReq.flush(mockSession);

    const teacherReq = httpMock.expectOne('api/teacher/1');
    expect(teacherReq.request.method).toBe('GET');
    teacherReq.flush(mockTeacher);
  });

  it('devrait permettre à un utilisateur de ne plus participer à une session', () => {
    component.unParticipate();

    const unParticipateReq = httpMock.expectOne('api/session/1/participate/1');
    expect(unParticipateReq.request.method).toBe('DELETE');
    unParticipateReq.flush({});

    const sessionReq = httpMock.expectOne('api/session/1');
    expect(sessionReq.request.method).toBe('GET');
    sessionReq.flush(mockSession);

    const teacherReq = httpMock.expectOne('api/teacher/1');
    expect(teacherReq.request.method).toBe('GET');
    teacherReq.flush(mockTeacher);
  });

  it('devrait mettre à jour isParticipate après participation', () => {
    const sessionWithoutUser = { ...mockSession, users: [2, 3] };
    const sessionWithUser = { ...mockSession, users: [1, 2, 3] };

    fixture.detectChanges();

    const initialSessionReq = httpMock.expectOne('api/session/1');
    initialSessionReq.flush(sessionWithoutUser);

    const initialTeacherReq = httpMock.expectOne('api/teacher/1');
    initialTeacherReq.flush(mockTeacher);

    expect(component.isParticipate).toBe(false);

    component.participate();

    const participateReq = httpMock.expectOne('api/session/1/participate/1');
    participateReq.flush({});

    const updatedSessionReq = httpMock.expectOne('api/session/1');
    updatedSessionReq.flush(sessionWithUser);

    const updatedTeacherReq = httpMock.expectOne('api/teacher/1');
    updatedTeacherReq.flush(mockTeacher);

    expect(component.isParticipate).toBe(true);
  });

  it('devrait gérer le cas où l\'utilisateur n\'est pas admin', async () => {
    const mockSessionServiceNonAdmin = {
      sessionInformation: {
        admin: false,
        id: 2
      }
    };

    await TestBed.resetTestingModule();
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientTestingModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionServiceNonAdmin },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ],
    }).compileComponents();

    const newHttpMock = TestBed.inject(HttpTestingController);
    const newFixture = TestBed.createComponent(DetailComponent);
    const newComponent = newFixture.componentInstance;

    expect(newComponent.isAdmin).toBe(false);
    expect(newComponent.userId).toBe('2');

    newHttpMock.verify();
  });
});

