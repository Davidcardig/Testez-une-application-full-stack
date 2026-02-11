import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { FormComponent } from './form.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('FormComponent - Tests Unitaires', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
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
    users: [1, 2],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockTeachers = [
    {
      id: 1,
      firstName: 'Jean',
      lastName: 'Dupont',
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id: 2,
      firstName: 'Marie',
      lastName: 'Martin',
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ];

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
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        SessionApiService,
        TeacherService
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    sessionApiService = TestBed.inject(SessionApiService);
    teacherService = TestBed.inject(TeacherService);
    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);

    jest.spyOn(teacherService, 'all').mockReturnValue(of(mockTeachers));

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
  });

  it('devrait créer le composant', () => {
    expect(component).toBeTruthy();
  });

  describe('méthode ngOnInit', () => {
    it('devrait rediriger vers /sessions si l\'utilisateur n\'est pas admin', () => {
      const mockSessionServiceNonAdmin = {
        sessionInformation: {
          admin: false,
          id: 2
        }
      };

      TestBed.resetTestingModule();
      TestBed.configureTestingModule({
        imports: [
          RouterTestingModule.withRoutes([]),
          HttpClientModule,
          MatCardModule,
          MatIconModule,
          MatFormFieldModule,
          MatInputModule,
          ReactiveFormsModule,
          MatSnackBarModule,
          MatSelectModule,
          BrowserAnimationsModule
        ],
        providers: [
          { provide: SessionService, useValue: mockSessionServiceNonAdmin },
          { provide: ActivatedRoute, useValue: mockActivatedRoute },
          SessionApiService,
          TeacherService
        ],
        declarations: [FormComponent]
      });

      const newRouter = TestBed.inject(Router);
      const navigateSpy = jest.spyOn(newRouter, 'navigate').mockResolvedValue(true);
      jest.spyOn(newRouter, 'url', 'get').mockReturnValue('/sessions/create');

      const newFixture = TestBed.createComponent(FormComponent);
      const newComponent = newFixture.componentInstance;

      newComponent.ngOnInit();

      expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
    });

    it('devrait initialiser le formulaire en mode création', () => {
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');

      component.ngOnInit();

      expect(component.onUpdate).toBe(false);
      expect(component.sessionForm).toBeDefined();
      expect(component.sessionForm?.get('name')?.value).toBe('');
    });

    it('devrait initialiser le formulaire en mode mise à jour', () => {
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
      jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));

      component.ngOnInit();

      expect(component.onUpdate).toBe(true);
      expect(component.sessionForm).toBeDefined();
    });
  });

  describe('méthode submit', () => {
    beforeEach(() => {
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
      component.ngOnInit();
    });

    it('devrait créer une nouvelle session en mode création', () => {
      const createSpy = jest.spyOn(sessionApiService, 'create').mockReturnValue(of(mockSession));
      const snackBarSpy = jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);
      const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

      component.sessionForm?.setValue({
        name: 'Nouvelle Session',
        date: '2026-01-25',
        teacher_id: 1,
        description: 'Description test'
      });

      component.submit();

      expect(createSpy).toHaveBeenCalled();
      expect(snackBarSpy).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
      expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
    });

    it('devrait mettre à jour une session en mode mise à jour', () => {
      component.onUpdate = true;
      component['id'] = '1';

      const updateSpy = jest.spyOn(sessionApiService, 'update').mockReturnValue(of(mockSession));
      const snackBarSpy = jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);
      const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

      component.sessionForm?.setValue({
        name: 'Session Modifiée',
        date: '2026-01-25',
        teacher_id: 1,
        description: 'Description modifiée'
      });

      component.submit();

      expect(updateSpy).toHaveBeenCalledWith('1', expect.any(Object));
      expect(snackBarSpy).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
      expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
    });
  });

  describe('validation du formulaire', () => {
    beforeEach(() => {
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
      component.ngOnInit();
    });

    it('devrait être invalide si les champs requis sont vides', () => {
      expect(component.sessionForm?.valid).toBe(false);
    });

    it('devrait être valide si tous les champs sont remplis correctement', () => {
      component.sessionForm?.setValue({
        name: 'Session Test',
        date: '2026-01-25',
        teacher_id: 1,
        description: 'Description valide'
      });

      expect(component.sessionForm?.valid).toBe(true);
    });

    it('devrait valider le champ name comme requis', () => {
      const nameControl = component.sessionForm?.get('name');
      expect(nameControl?.hasError('required')).toBe(true);

      nameControl?.setValue('Session Test');
      expect(nameControl?.hasError('required')).toBe(false);
    });

    it('devrait valider le champ date comme requis', () => {
      const dateControl = component.sessionForm?.get('date');
      expect(dateControl?.hasError('required')).toBe(true);

      dateControl?.setValue('2026-01-25');
      expect(dateControl?.hasError('required')).toBe(false);
    });

    it('devrait valider le champ teacher_id comme requis', () => {
      const teacherControl = component.sessionForm?.get('teacher_id');
      expect(teacherControl?.hasError('required')).toBe(true);

      teacherControl?.setValue(1);
      expect(teacherControl?.hasError('required')).toBe(false);
    });

    it('devrait valider le champ description comme requis', () => {
      const descriptionControl = component.sessionForm?.get('description');
      expect(descriptionControl?.hasError('required')).toBe(true);

      descriptionControl?.setValue('Description valide');
      expect(descriptionControl?.hasError('required')).toBe(false);
    });
  });

  describe('propriété teachers$', () => {
    it('devrait charger la liste des professeurs', (done) => {
      component.teachers$.subscribe(teachers => {
        expect(teachers).toEqual(mockTeachers);
        expect(teachers.length).toBe(2);
        done();
      });
    });
  });
});

describe('FormComponent - Tests d\'Intégration', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let httpMock: HttpTestingController;
  let router: Router;
  let matSnackBar: MatSnackBar;

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
    users: [1, 2],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockTeachers = [
    {
      id: 1,
      firstName: 'Jean',
      lastName: 'Dupont',
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id: 2,
      firstName: 'Marie',
      lastName: 'Martin',
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ];

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
        HttpClientTestingModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('devrait créer une nouvelle session et naviguer vers /sessions', () => {
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    const snackBarSpy = jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);

    fixture.detectChanges();

    const teachersReq = httpMock.expectOne('api/teacher');
    expect(teachersReq.request.method).toBe('GET');
    teachersReq.flush(mockTeachers);

    component.sessionForm?.setValue({
      name: 'Nouvelle Session',
      date: '2026-01-25',
      teacher_id: 1,
      description: 'Description test'
    });

    component.submit();

    const createReq = httpMock.expectOne('api/session');
    expect(createReq.request.method).toBe('POST');
    expect(createReq.request.body).toEqual({
      name: 'Nouvelle Session',
      date: '2026-01-25',
      teacher_id: 1,
      description: 'Description test'
    });
    createReq.flush(mockSession);

    expect(snackBarSpy).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });

  it('devrait mettre à jour une session existante', fakeAsync(() => {
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    const snackBarSpy = jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);

    fixture.detectChanges();

    // La requête de détail de session est faite en premier
    const detailReq = httpMock.expectOne('api/session/1');
    expect(detailReq.request.method).toBe('GET');
    detailReq.flush(mockSession);

    tick();
    fixture.detectChanges();

    // La requête des professeurs se fait quand le template souscrit à teachers$ (async pipe)
    const teachersReq = httpMock.expectOne('api/teacher');
    teachersReq.flush(mockTeachers);

    tick();
    fixture.detectChanges(); // Mise à jour du template avec les données

    component.sessionForm?.patchValue({
      name: 'Session Modifiée'
    });

    component.submit();

    const updateReq = httpMock.expectOne('api/session/1');
    expect(updateReq.request.method).toBe('PUT');
    expect(updateReq.request.body.name).toBe('Session Modifiée');
    updateReq.flush(mockSession);

    tick();

    expect(snackBarSpy).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  }));

  it('devrait charger les détails de la session en mode mise à jour', fakeAsync(() => {
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');

    fixture.detectChanges();

    // La requête de détail de session est faite dans ngOnInit
    const detailReq = httpMock.expectOne('api/session/1');
    expect(detailReq.request.method).toBe('GET');
    detailReq.flush(mockSession);

    tick();
    fixture.detectChanges();

    // La requête des professeurs se fait quand le template souscrit à teachers$ (async pipe)
    const teachersReq = httpMock.expectOne('api/teacher');
    teachersReq.flush(mockTeachers);

    tick();
    fixture.detectChanges(); // Mise à jour du template avec les données

    expect(component.onUpdate).toBe(true);
    expect(component.sessionForm?.get('name')?.value).toBe('Yoga Session');
    expect(component.sessionForm?.get('teacher_id')?.value).toBe(1);
    expect(component.sessionForm?.get('description')?.value).toBe('Description de la session');
  }));

  it('devrait charger la liste des professeurs au démarrage', fakeAsync(() => {
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');

    fixture.detectChanges();

    // La requête des professeurs se fait quand le template souscrit à teachers$ (async pipe)
    const teachersReqs = httpMock.match('api/teacher');
    expect(teachersReqs.length).toBeGreaterThanOrEqual(1);
    expect(teachersReqs[0].request.method).toBe('GET');

    // Répondre à toutes les requêtes
    teachersReqs.forEach(req => req.flush(mockTeachers));

    tick();
    fixture.detectChanges();

    // Vérifier que l'observable est défini
    expect(component.teachers$).toBeDefined();

    // Vérifier que le formulaire est créé en mode création
    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm).toBeDefined();
  }));

  it('devrait rediriger un utilisateur non-admin', () => {
    const mockSessionServiceNonAdmin = {
      sessionInformation: {
        admin: false,
        id: 2
      }
    };

    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientTestingModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionServiceNonAdmin },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ],
      declarations: [FormComponent]
    });

    const newHttpMock = TestBed.inject(HttpTestingController);
    const newRouter = TestBed.inject(Router);
    const navigateSpy = jest.spyOn(newRouter, 'navigate').mockResolvedValue(true);
    jest.spyOn(newRouter, 'url', 'get').mockReturnValue('/sessions/create');

    const newFixture = TestBed.createComponent(FormComponent);
    const newComponent = newFixture.componentInstance;

    newComponent.ngOnInit();

    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);

    newHttpMock.verify();
  });
});
